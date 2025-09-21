import random

num_molecules = 100000
num_chlorine_atoms = 250000

# CH4, CH3Cl, CH2Cl2, CHCl3, CCl4
molecules = [num_molecules, 0, 0, 0, 0]

#random.seed(0)

for _ in range(num_chlorine_atoms):
    collision = random.randint(1, num_molecules - molecules[-1])
    sum = 0
    for j in range(len(molecules) - 1):
        sum += molecules[j]
        if collision <= sum:
            molecules[j] -= 1
            molecules[j+1] += 1
            break

print(molecules)