import os
import time
import threading
import subprocess
import numpy as np
import seaborn as sb
import matplotlib.pyplot as plt

env = dict(os.environ)
data = [[{} for j in range(100)] for i in range(3)]

def run_test(size, i):
    start_time = time.time()
    subprocess.run(['java', '-cp', '../class', 'src.Main', f'../{size}x{size}/input{i}.txt', f'../{size}x{size}/output{i}.txt'], env=env)
    exe_time = time.time() - start_time

    with open(f'../{size}x{size}/output{i}.txt', 'r') as test:
        seq = test.readline()[:-1]
        best = test.readline()

    data[size - 3][i] = {
        'exe': exe_time,
        'best': best,
        'seq': seq
    }

    with open(f'../{size}x{size}/input{i}.txt', 'r') as inf:
        inf.readline()
        aux = []
        for _ in range(i):
            l = inf.readline()
            aux.append([int(tile) for tile in l.replace('\n', '').split(',')])

    plt.figure(figsize=(3,3), dpi=150)
    fs = 2
    if (size == 4):
        fs = 1.5
    if (size == 5):
        fs = 1
    sb.set(font_scale=fs)
    heat_map = sb.heatmap(aux, xticklabels=False, yticklabels=False, cmap='RdPu', annot=True, cbar=False)
    plt.savefig(f'../{size}x{size}/board{i}.png')
    plt.close()

def main():
    env['JAVA_OPTS'] = 'foo'
    subprocess.run(['javac', '-encoding', 'utf-8', '-d', '../class/', '../src/*.java'], env=env)

    for i in range(3,6):
        for j in range(100):
            x = threading.Thread(target=run_test, args=(i,j,))
            x.start()

        with open(f'../{i}x{i}/results.csv', 'w') as f:
            for j in range(100):
                exe_time = data[i][j]['exe']
                best = data[i][j]['best']
                seq = data[i][j]['seq']

                f.write(f'{j},{exe_time},{best},\"{seq}\"\n')

if __name__ == '__main__':
    main()