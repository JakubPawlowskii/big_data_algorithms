import matplotlib.pyplot as plt
import numpy as np

# Read the data from the file line by line
page_titles = []
page_ranks = []
# with open('data/Knot_theory/page_rank.txt') as f:
with open('data/Eigenstate_thermalization_hypothesis/page_rank.txt') as f:
# with open('data/Quantum_mechanics/page_rank.txt') as f:
    for line in f.readlines():
        page_title, page_rank = line.split(',')
        page_titles.append(page_title)
        page_ranks.append(float(page_rank))


print(np.sum(page_ranks))


# argsort returns the indices that would sort an array
# We want to sort the page ranks in descending order
# and then use those indices to sort the page titles
# in the same order
sorted_indices = np.argsort(page_ranks)[::-1]
page_titles = np.array(page_titles)[sorted_indices]
page_ranks = np.array(page_ranks)[sorted_indices]

print(page_ranks[0])
print(page_titles[0])

    
plt.loglog(1,page_ranks[0], 'o', label=page_titles[0])
plt.loglog(2,page_ranks[1], 'o', label=page_titles[1])
plt.loglog(3,page_ranks[2], 'o', label=page_titles[2])
plt.loglog(4,page_ranks[3], 'o', label=page_titles[3])
plt.loglog(5,page_ranks[4], 'o', label=page_titles[4])
plt.loglog(6,page_ranks[5], 'o', label=page_titles[5])
plt.loglog(7,page_ranks[6], 'o', label=page_titles[6])
plt.loglog(8,page_ranks[7], 'o', label=page_titles[7])

plt.loglog(np.arange(9, len(page_ranks)+1), page_ranks[8:], 'o', label='Other Pages')

plt.xlabel('Page Index')
plt.ylabel('Page Rank')
plt.legend()
plt.show()
