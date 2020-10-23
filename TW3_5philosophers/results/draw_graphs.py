import os
import pandas as pd
import matplotlib.pyplot as plt
plt.rcParams['axes.grid'] = True

# naive_asynch = pd.read_csv("jsNaiveAsynch.txt", index_col=False, header=0)
# naive_asynch.boxplot(by=['time'], column='avg_time')
# plt.show()
# exit()
all_results = {}
all_results['java with starving'] = pd.read_csv("javaStarve.txt", index_col=False, header=None, names=["avg_time", "n"])
all_results['java with conductor'] = pd.read_csv("javaCond.txt", index_col=False, header=0, names=["avg_time", "n"])
all_results['javaScript naive'] = pd.read_csv("jsNaive.txt", index_col=False, header=0, names=["avg_time", "n"])
all_results['javaScript asymmetric'] = pd.read_csv("jsAsym.txt", index_col=False, header=0, names=["avg_time", "n", 'idx'])
all_results['javaScript with conductor'] = pd.read_csv("jsCond.txt", index_col=False, header=0, names=["avg_time", "n", "idx"])
all_results['javaScript starving'] = pd.read_csv("jsBoth.txt", index_col=False, header=0, names=["avg_time", "n", "idx"])

# # arbiter.boxplot(column='avg_time')
# for name, df in all_results.items():
#     fig, axes = plt.subplots(1, 2)
#     df.boxplot(column='avg_time', ax=axes[0])
#     df.hist(column='avg_time', ax=axes[1])
#     fig.suptitle(name)
# key = 'javaScript starving'
# all_results[key][-10:].plot(x='idx', y='avg_time', style='o')
# plt.title(key)
# # arbiter.boxplot(by='n')
# plt.show()
# exit()


# fig, axes = plt.subplots(2, len(all_results))
# print(axes)
i = 0
# fig.suptitle("Summary")
for name, df in all_results.items():
    print(name)
    print("mean:", df['avg_time'].mean(),"median:",df['avg_time'].median() , "\n")
    # df.boxplot(column='avg_time', ax=axes[0, i])
    # axes[1, i].set_xlim([0, 20000])
    # axes[0, i].set_title(name)
    # axes[0, i].set_ylim([0, 20000])
    # df.hist(column='avg_time', ax=axes[1, i], range=[0, 20000], bins=20)
    # i += 1

# plt.show()
