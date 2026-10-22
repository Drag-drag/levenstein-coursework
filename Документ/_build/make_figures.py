# -*- coding: utf-8 -*-
"""Построение рисунков для пояснительной записки."""
import os
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch, Polygon, FancyArrowPatch

plt.rcParams["font.family"] = "DejaVu Sans"
plt.rcParams["font.size"] = 11

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "images")
os.makedirs(OUT, exist_ok=True)


def save(fig, name):
    path = os.path.join(OUT, name)
    fig.savefig(path, dpi=200, bbox_inches="tight", facecolor="white")
    plt.close(fig)
    print("saved", path)


# ---------------------------------------------------------------- рисунок 2.1
def flowchart():
    fig, ax = plt.subplots(figsize=(7.2, 9.6))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12.5)
    ax.axis("off")

    def text(x, y, s, size=10.5, weight="normal"):
        ax.text(x, y, s, ha="center", va="center", fontsize=size,
                weight=weight, linespacing=1.25)

    def rect(x, y, w, h, s, size=10.5):
        ax.add_patch(FancyBboxPatch((x - w / 2, y - h / 2), w, h,
                                    boxstyle="round,pad=0.02,rounding_size=0.08",
                                    linewidth=1.3, edgecolor="black", facecolor="white"))
        text(x, y, s, size)

    def diamond(x, y, w, h, s, size=10.5):
        ax.add_patch(Polygon([[x, y + h / 2], [x + w / 2, y],
                              [x, y - h / 2], [x - w / 2, y]],
                             closed=True, linewidth=1.3,
                             edgecolor="black", facecolor="white"))
        text(x, y, s, size)

    def oval(x, y, w, h, s):
        ax.add_patch(FancyBboxPatch((x - w / 2, y - h / 2), w, h,
                                    boxstyle="round,pad=0.02,rounding_size=0.45",
                                    linewidth=1.3, edgecolor="black", facecolor="white"))
        text(x, y, s)

    def para(x, y, w, h, s, size=10.5):
        ax.add_patch(Polygon([[x - w / 2 + 0.25, y + h / 2],
                              [x + w / 2 + 0.25, y + h / 2],
                              [x + w / 2 - 0.25, y - h / 2],
                              [x - w / 2 - 0.25, y - h / 2]],
                             closed=True, linewidth=1.3,
                             edgecolor="black", facecolor="white"))
        text(x, y, s, size)

    def arrow(x1, y1, x2, y2, label=None, lx=0, ly=0):
        ax.add_patch(FancyArrowPatch((x1, y1), (x2, y2),
                                     arrowstyle="-|>", mutation_scale=14,
                                     linewidth=1.2, color="black",
                                     shrinkA=0, shrinkB=0))
        if label:
            ax.text((x1 + x2) / 2 + lx, (y1 + y2) / 2 + ly, label,
                    fontsize=9.5, ha="center", va="center", style="italic")

    oval(4.4, 11.9, 1.6, 0.6, "Начало")
    para(4.4, 10.8, 4.4, 0.7, "Ввод строк a и b")
    diamond(4.4, 9.3, 4.4, 1.1, "длина a = 0\nили длина b = 0?", 10)
    rect(8.4, 9.3, 2.8, 0.9, "вернуть длину\nнепустой строки")
    oval(8.4, 7.9, 1.6, 0.6, "Конец")

    rect(4.4, 7.5, 4.8, 0.9, "d[i][0] = i,  d[0][j] = j\n(инициализация границ)")
    rect(4.4, 6.2, 5.6, 0.8, "для i = 1..n,  для j = 1..m")
    rect(4.4, 4.7, 5.8, 1.3,
         "c = (a[i] = b[j]) ? 0 : 1\n"
         "d[i][j] = min(d[i-1][j] + 1,\n"
         "              d[i][j-1] + 1,\n"
         "              d[i-1][j-1] + c)")
    diamond(4.4, 2.9, 4.8, 1.1, "все клетки\nпосчитаны?")
    para(4.4, 1.5, 4.2, 0.7, "вывод d[n][m]")
    oval(4.4, 0.4, 1.6, 0.6, "Конец")

    arrow(4.4, 11.6, 4.4, 11.15)
    arrow(4.4, 10.45, 4.4, 9.85)
    arrow(6.6, 9.3, 7.0, 9.3, "да", 0.0, 0.2)
    arrow(8.4, 8.85, 8.4, 8.2)
    arrow(4.4, 8.75, 4.4, 7.95, "нет", 0.3, 0)
    arrow(4.4, 7.05, 4.4, 6.6)
    arrow(4.4, 5.8, 4.4, 5.35)
    arrow(4.4, 4.05, 4.4, 3.45)
    arrow(4.4, 2.35, 4.4, 1.85, "да", 0.3, 0)
    arrow(4.4, 1.15, 4.4, 0.7)

    # цикл: возврат от ромба к блоку циклов
    ax.add_patch(FancyArrowPatch((2.0, 2.9), (1.6, 6.2),
                                 connectionstyle="arc3,rad=-0.3",
                                 arrowstyle="-|>", mutation_scale=14,
                                 linewidth=1.2, color="black"))
    ax.text(0.75, 4.55, "нет", fontsize=9.5, ha="center", va="center", style="italic")

    save(fig, "fig_2_1_flowchart.png")


# ---------------------------------------------------------------- рисунок 2.2
def antidiagonals():
    fig, ax = plt.subplots(figsize=(7.2, 6.6))
    n = 6
    ax.set_xlim(-1.0, n + 1.4)
    ax.set_ylim(-0.4, n + 1.4)
    ax.set_aspect("equal")
    ax.axis("off")

    colors = plt.cm.Blues
    target = (3, 3)
    deps = {(2, 3), (3, 2), (2, 2)}

    for i in range(n + 1):
        for j in range(n + 1):
            face = colors((i + j) / (2 * n) * 0.75 + 0.12)
            edge, lw = "white", 1.5
            if (i, j) in deps:
                edge, lw = "#b00020", 3.0
            if (i, j) == target:
                face, edge, lw = "#ffd9d9", "#b00020", 3.0

            ax.add_patch(plt.Rectangle((j, n - i), 1, 1, facecolor=face,
                                       edgecolor=edge, linewidth=lw))
            ax.text(j + 0.5, n - i + 0.5, f"d[{i}][{j}]",
                    ha="center", va="center", fontsize=8)

    # подписи антидиагоналей выносятся за пределы матрицы
    for s in range(0, 2 * n + 1):
        cells = [(i, s - i) for i in range(n + 1) if 0 <= s - i <= n]
        i0, j0 = cells[0]
        if i0 == 0:
            ax.text(j0 + 0.5, n + 1.15, f"s={s}", fontsize=8.5,
                    color="#444444", ha="center", va="bottom")
        elif j0 == 0:
            ax.text(-0.15, n - i0 + 0.5, f"s={s}", fontsize=8.5,
                    color="#444444", ha="right", va="center")
        else:
            ax.text(n + 1.15, n - i0 + 0.5, f"s={s}", fontsize=8.5,
                    color="#444444", ha="left", va="center")

    from matplotlib.patches import Patch
    save(fig, "fig_2_2_diagonals.png")


# ---------------------------------------------------------------- рисунок 2.3
def time_plot():
    sizes = [100, 250, 500, 1000, 2000, 4000]
    matrix = [0.151, 0.933, 2.028, 8.632, 31.693, 201.803]
    rows = [0.094, 0.320, 1.179, 4.486, 28.079, 92.062]

    fig, ax = plt.subplots(figsize=(7.0, 4.4))
    ax.plot(sizes, matrix, "o-", label="полная матрица O(n·m)", linewidth=2)
    ax.plot(sizes, rows, "s-", label="две строки O(min(n, m))", linewidth=2)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("Длина строк n = m, символов")
    ax.set_ylabel("Время, мс")
    ax.grid(True, which="both", linestyle=":", alpha=0.6)
    ax.legend()
    fig.tight_layout()
    save(fig, "fig_2_3_time.png")


# ---------------------------------------------------------------- рисунок 2.4
def speedup_plot():
    threads = [1, 2, 4, 8]
    data = {
        500: [95.221, 39.888, 22.152, 15.149],
        1000: [320.192, 179.706, 99.088, 61.532],
        2000: [861.057, 539.235, 325.905, 224.965],
    }

    fig, ax = plt.subplots(figsize=(7.0, 4.4))
    ax.plot(threads, threads, "k--", linewidth=1.2, label="идеальное ускорение")
    for n, times in data.items():
        speedup = [times[0] / t for t in times]
        ax.plot(threads, speedup, "o-", linewidth=2, label=f"n = {n}")

    ax.set_xlabel("Число потоков p")
    ax.set_ylabel("Ускорение S = T(1) / T(p)")
    ax.set_xticks(threads)
    ax.grid(True, linestyle=":", alpha=0.6)
    ax.legend()
    fig.tight_layout()
    save(fig, "fig_2_4_speedup.png")


# ---------------------------------------------------------------- рисунок 2.5
def memory_plot():
    sizes = [100, 250, 500, 1000, 2000, 4000]
    matrix = [(n + 1) ** 2 * 4 / 1024 for n in sizes]
    rows = [2 * (n + 1) * 4 / 1024 for n in sizes]

    fig, ax = plt.subplots(figsize=(7.0, 4.4))
    ax.plot(sizes, matrix, "o-", linewidth=2, label="полная матрица")
    ax.plot(sizes, rows, "s-", linewidth=2, label="две строки")
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("Длина строк n = m, символов")
    ax.set_ylabel("Память, КБ")
    ax.grid(True, which="both", linestyle=":", alpha=0.6)
    ax.legend()
    fig.tight_layout()
    save(fig, "fig_2_5_memory.png")


if __name__ == "__main__":
    flowchart()
    antidiagonals()
    time_plot()
    speedup_plot()
    memory_plot()
