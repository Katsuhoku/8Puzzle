import os
import time
import subprocess
import numpy as np
import seaborn as sb
import matplotlib.pyplot as plt

env = dict(os.environ)
env['JAVA_OPTS'] = 'foo'
subprocess.run(['javac', '-encoding', 'utf-8', '*.java'], env=env)

for i in range(3,6):
    with open(f'./{i}x{i}/results.csv', 'w') as f:
        for j in range(100):
            start_time = time.time()
            subprocess.run(['java', 'Test', f'./{i}x{i}/input{j}.txt', f'./{i}x{i}/output{j}.txt'], env=env)
            exe_time = time.time() - start_time

            with open(f'./{i}x{i}/output{j}.txt', 'r') as test:
                seq = test.readline()[:-1]
                best = test.readline()

            f.write(f'{j},{exe_time},{best},\"{seq}\"\n')

            with open(f'./{i}x{i}/input{j}.txt', 'r') as inf:
                inf.readline()
                aux = []
                for _ in range(i):
                    l = inf.readline()
                    aux.append([int(i) for i in l.replace('\n', '').split(',')])

            plt.figure(figsize=(3,3), dpi=150)
            fs = 2
            if (i == 4):
                fs = 1.5
            if (i == 5):
                fs = 1
            sb.set(font_scale=fs)
            heat_map = sb.heatmap(aux, xticklabels=False, yticklabels=False, cmap='RdPu', annot=True, cbar=False)
            plt.savefig(f'./{i}x{i}/board{j}.png')
            plt.close()
