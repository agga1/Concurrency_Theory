import os
import pandas as pd
import matplotlib.pyplot as plt

all_results = {}
all_results['java with starving'] = pd.read_csv("javaStarve.txt", index_col=False, header=None, names=["avg_time", "n"])
all_results['java with conductor'] = pd.read_csv("javaCond.txt", index_col=False, header=0, names=["avg_time", "n"])
all_results['javaScript naive'] = pd.read_csv("jsNaive.txt", index_col=False, header=0, names=["avg_time", "n"])
all_results['javaScript asymmetric'] = pd.read_csv("jsAsym.txt", index_col=False, header=0, names=["avg_time", "n", 'id'])
all_results['javaScript with conductor'] = pd.read_csv("jsCond.txt", index_col=False, header=0, names=["avg_time", "n", "id"])
all_results['javaScript starving'] = pd.read_csv("jsBoth.txt", index_col=False, header=0, names=["avg_time", "n", "id"])

# # arbiter.boxplot(column='avg_time')
# for name, df in all_results.items():
#     fig, axes = plt.subplots(1, 2)
#     df.boxplot(column='avg_time', ax=axes[0])
#     df.hist(column='avg_time', ax=axes[1])
#     fig.suptitle(name)

all_results['javaScript asymmetric'][29:35].groupby('id').avg_time.mean().plot()
# arbiter.boxplot(by='n')
plt.show()
exit()
fig, axes = plt.subplots(2, len(all_results))
print(axes)
i = 0
fig.suptitle("Summary")
for name, df in all_results.items():
    df.boxplot(column='avg_time', ax=axes[0, i])
    axes[1, i].set_xlim([0, 20000])
    axes[0, i].set_title(name)
    axes[0, i].set_ylim([0, 20000])
    df.hist(column='avg_time', ax=axes[1, i], range=[0, 20000], bins=20)
    i += 1

plt.show()
