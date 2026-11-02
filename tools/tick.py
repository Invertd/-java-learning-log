# -*- coding: utf-8 -*-
"""打卡脚本：勾选任务 → 写日志 → git commit → push。

用法：
    python tools/tick.py                 查看今天的任务和进度
    python tools/tick.py 3               把第 3 项标记为完成，并提交
    python tools/tick.py 2 5 7           一次勾多项
    python tools/tick.py --status        看全部进度
    python tools/tick.py --day 05        查看/操作第 05 天
    python tools/tick.py 3 --no-push     只 commit 不 push
    python tools/tick.py --undo 3        撤销第 3 项的勾选
"""
import datetime
import pathlib
import re
import subprocess
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
DAYS_DIR = ROOT / "days"
WEEKS_DIR = ROOT / "weeks"
LOGS_DIR = ROOT / "logs"

WD_CN = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"]
TASK_RE = re.compile(r"^(\s*)- \[([ xX])\] \*\*(\d+)\.\*\*\s*(.+)$")


def setup_stdout():
    try:
        sys.stdout.reconfigure(encoding="utf-8")
    except Exception:
        pass


def all_day_files():
    return sorted(DAYS_DIR.glob("*.md"))


def all_week_files():
    def key(p):
        m = re.search(r"(\d{4}-\d{2}-\d{2})", p.name)
        return m.group(1) if m else p.name
    return sorted(WEEKS_DIR.glob("*.md"), key=key)


def parse_date(path: pathlib.Path):
    m = re.search(r"(\d{4}-\d{2}-\d{2})", path.name)
    return datetime.date.fromisoformat(m.group(1)) if m else None


def pick_file(day=None):
    """选定今天要操作的文件。"""
    files = all_day_files()
    if day:
        for f in files:
            if f.name.startswith(f"{int(day):02d}-") or f.name.startswith(f"{day}-"):
                return f
        for f in all_week_files():
            if day in f.name:
                return f
        raise SystemExit(f"找不到第 {day} 天的文件")

    today = datetime.date.today()
    for f in files:
        if parse_date(f) == today:
            return f
    # 无当天日文件 → 看是否落在某个周文件区间内
    for f in all_week_files():
        m = re.search(r"(\d{4}-\d{2}-\d{2})", f.name)
        if not m:
            continue
        start = datetime.date.fromisoformat(m.group(1))
        text = f.read_text(encoding="utf-8")
        m2 = re.search(r"→\s*(\d{4}-\d{2}-\d{2})", text)
        end = datetime.date.fromisoformat(m2.group(1)) if m2 else start + datetime.timedelta(days=6)
        if start <= today <= end:
            return f
    # 兜底：取最近的一个未来日文件，或最后一个
    future = [f for f in files if parse_date(f) and parse_date(f) >= today]
    if future:
        return future[0]
    return files[-1] if files else None


def read_tasks(path):
    tasks = []
    for line in path.read_text(encoding="utf-8").splitlines():
        m = TASK_RE.match(line)
        if m:
            tasks.append({"num": int(m.group(3)), "done": m.group(2).lower() == "x", "text": m.group(4)})
    return tasks


def day_label(path):
    text = path.read_text(encoding="utf-8")
    first = text.splitlines()[0] if text else path.name
    return first.lstrip("# ").strip()


def show(path):
    tasks = read_tasks(path)
    print(f"\n{day_label(path)}")
    print(f"文件：{path.relative_to(ROOT)}")
    done = sum(1 for t in tasks if t["done"])
    print(f"进度：{done}/{len(tasks)}\n")
    for t in tasks:
        mark = "x" if t["done"] else " "
        print(f"  [{mark}] {t['num']}. {t['text']}")
    print()
    if done == len(tasks) and tasks:
        print("这一天全做完了，去 logs 里写一句复盘吧。\n")


def git(*args, check=False):
    r = subprocess.run(["git", *args], cwd=ROOT, capture_output=True, text=True, encoding="utf-8", errors="replace")
    if check and r.returncode != 0:
        print(f"[git {' '.join(args)}] {r.stderr.strip()}")
    return r


def has_remote():
    r = git("remote")
    return "origin" in (r.stdout or "")


def tick(path, nums, push=True, undo=False):
    tasks = read_tasks(path)
    by_num = {t["num"]: t for t in tasks}
    lines = path.read_text(encoding="utf-8").splitlines()
    changed = []
    for n in nums:
        if n not in by_num:
            print(f"  跳过：没有第 {n} 项")
            continue
        if by_num[n]["done"] == (not undo):
            print(f"  跳过：第 {n} 项{'已经完成了' if not undo else '本来就没完成'}")
            continue
        new_mark = " " if undo else "x"
        pat = re.compile(rf"^(\s*)- \[[ xX]\] \*\*{n}\.\*\*\s*(.+)$")
        for i, line in enumerate(lines):
            m = pat.match(line)
            if m:
                head = m.group(1)
                lines[i] = line.replace(
                    line[line.index("- ["):line.index("]") + 1],
                    f"- [{new_mark}]",
                    1,
                )
                changed.append((n, m.group(2)))
                break
    if not changed:
        print("\n没有需要修改的任务。\n")
        return

    path.write_text("\n".join(lines) + "\n", encoding="utf-8")

    # 写日志
    LOGS_DIR.mkdir(exist_ok=True)
    now = datetime.datetime.now()
    log = LOGS_DIR / f"{now.date().isoformat()}.md"
    if not log.exists():
        log.write_text(f"# {now.date().isoformat()} {WD_CN[now.weekday()]}\n\n", encoding="utf-8")
    with log.open("a", encoding="utf-8") as f:
        for n, text in changed:
            act = "✅" if not undo else "↩️"
            f.write(f"- {now.strftime('%H:%M')} {act} {now_short(path)} 第 {n} 项 {text}\n")

    tasks2 = read_tasks(path)
    done = sum(1 for t in tasks2 if t["done"])
    print(f"\n已更新：{path.relative_to(ROOT)}  进度 {done}/{len(tasks2)}\n")

    if changed:
        first = changed[0]
        msg = f"{now_short(path)}: {'撤销' if undo else '完成'} 第{first[0]}项 {first[1][:40]}"
        git("add", "-A")
        r = git("commit", "-m", msg)
        if r.returncode == 0:
            print(f"✅ 已 commit: {msg}")
            if push and has_remote():
                p = git("push")
                if p.returncode == 0:
                    print("✅ 已 push 到 GitHub")
                else:
                    print(f"⚠️  push 失败，稍后手动 git push\n   {(p.stderr or '').strip()[:300]}")
            elif push:
                print("（还没配置 origin，先 git remote add origin …）")
        else:
            print(f"⚠️  commit 失败：{(r.stdout or '') + (r.stderr or '')}".strip()[:300])


def now_short(path):
    m = re.search(r"(\d{2})-(\d{4}-\d{2}-\d{2})", path.name)
    if m:
        return f"day{m.group(1)}"
    m = re.search(r"([A-Z]+\d+)-", path.name)
    return m.group(1) if m else path.stem


def status():
    files = all_day_files()
    total = 0
    done = 0
    print("\n每日进度")
    print("-" * 56)
    for f in files:
        tasks = read_tasks(f)
        if not tasks:
            continue
        d = sum(1 for t in tasks if t["done"])
        total += len(tasks)
        done += d
        bar = "█" * int(10 * d / len(tasks)) + "░" * (10 - int(10 * d / len(tasks)))
        flag = " ✅" if d == len(tasks) else ""
        print(f"  {now_short(f):8s} {bar} {d}/{len(tasks)}{flag}")
    print("-" * 56)
    weeks = all_week_files()
    if weeks:
        print("\n每周进度")
        for f in weeks:
            tasks = read_tasks(f)
            if not tasks:
                continue
            d = sum(1 for t in tasks if t["done"])
            print(f"  {f.name[:-3]:28s} {d}/{len(tasks)}")
    pct = (100 * done / total) if total else 0
    print(f"\n总计 {done}/{total} 项（{pct:.0f}%）\n")


def main():
    setup_stdout()
    args = [a for a in sys.argv[1:]]
    push = "--no-push" not in args
    args = [a for a in args if a != "--no-push"]
    undo = "--undo" in args
    args = [a for a in args if a != "--undo"]

    if "--status" in args:
        status()
        return

    day = None
    if "--day" in args:
        i = args.index("--day")
        try:
            day = args[i + 1]
        except IndexError:
            raise SystemExit("--day 后面要跟天数，例如 --day 05")
        del args[i:i + 2]

    path = pick_file(day)
    if path is None:
        raise SystemExit("还没有任务文件，先运行 python tools/gen_days.py")

    nums = []
    for a in args:
        if a.isdigit():
            nums.append(int(a))
        else:
            raise SystemExit(f"看不懂参数：{a}")

    if not nums:
        show(path)
        return
    tick(path, nums, push=push, undo=undo)


if __name__ == "__main__":
    main()
