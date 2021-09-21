from random import shuffle, randint
from math import pow
from sys import argv

size = int(argv[1])
p = [
    [3,5,10,15,20,40,80,150],
    [5,10,15,25,40,80,150,300],
    [10,17,25,40,60,120,300,500],
    [10,20,30,40,60,120,300,500]
]

def genChild(direc, perm, blank):
    newblank = list(blank)

    if direc == 0:
        newblank[1] += 1
    elif direc == 1:
        newblank[0] -= 1
    elif direc == 2:
        newblank[1] -= 1
    elif direc == 3:
        newblank[0] += 1

    newperm = []
    for i in range(size):
        newperm.append(list(perm[i]))

    if newblank[0] in range(0, size) and newblank[1] in range(0, size):
        newperm[blank[0]][blank[1]] = newperm[newblank[0]][newblank[1]]
        newperm[newblank[0]][newblank[1]] = 0
        return newperm, newblank
    else: return None, None
        


sol = [i for i in range(int(pow(size, 2)))]

for i in range(160):
    perm = [[0 for _ in range(size)] for _ in range(size)]
    for j,e in enumerate(sol):
        perm[int(j/size)][j%size] = sol[j]
    blank = [0,0]

    mov = p[size - 3][0]
    if i >= 20: mov = p[size - 3][1]
    if i >= 40: mov = p[size - 3][2]
    if i >= 60: mov = p[size - 3][3]
    if i >= 80: mov = p[size - 3][4]
    if i >= 100: mov = p[size - 3][5]
    if i >= 120: mov = p[size - 3][6]
    if i >= 140: mov = p[size - 3][7]

    movs = [randint(0,2000) % 4 for _ in range(mov)]
    shuffle(movs)
    for m in movs:
        auxperm, auxblank = genChild(m, perm, blank)
        if auxperm == None:
            auxperm, auxblank = genChild((m + 2) % 4, perm, blank)
        
        perm = auxperm
        blank = auxblank
        

    '''
    for x in range(mov):
        movs = []
        for a in range(1,5):
            auxperm, auxblank = genChild(a, perm, blank)
            if auxperm != None: movs.append([auxperm, auxblank])
        
        pair = movs[randint(0, len(movs) - 1)]
        perm = pair[0]
        blank = pair[1]
    '''

    
    with open(f'../{size}x{size}/input{i}.txt', 'w') as f:
        f.write(f'{size}\n')
        for k in range(int(pow(size, 2))):
            f.write(f'{perm[int(k/size)][k%size]}')
            f.write(',') if k % size < (size - 1) else f.write('\n')

        for k in range(int(pow(size, 2))):
            f.write(f'{sol[k]}')
            f.write(',') if k % size < (size - 1) else f.write('\n')
    