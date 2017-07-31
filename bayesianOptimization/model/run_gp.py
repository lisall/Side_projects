import sys
import os
os.chdir('/Users/jieyang/Desktop/bo_demo/gp/model')
import numpy as np 
from tools import *
from gp_optimizer import *
from optimizer import *
from hp_optimizer import *
from bayesian_optimizer import *

# set up directory and name for files

os.chdir('/Users/jieyang/Desktop/bo_demo/gp')
dataPath = 'data/toy/'
dataFilePair = dataPath + 'pairwise.csv'
dataFileLabel = dataPath + 'label.csv'
dataFileUnlabel = dataPath + 'unlabel.csv'
dataFileOracle = dataPath + 'oracle.csv'

""""
read files
dataPair: store pairwise relation
structure: pair # | preferred ID | non-preferred ID
dataLabel: key-value pair where key is the obs # and values are features
structure: evidence # | feature vector {x_1,x_2,x_3,...,x_n}
dataNest: evidence # | nest #
dataOracle: choice id | ranking
"""

dataPair = read_into_key_values(dataFilePair)
dataLabel = read_into_key_values(dataFileLabel)
dataUnlabel = read_into_key_values(dataFileUnlabel)
dataOracle = read_into_key_values(dataFileOracle)

""""
pairs: pairwise relation data
xu: unlabeled instances 
xl: labeled instances
oracle: store the rank information
kernelType: kernel function type
gpMethod: first order optimization method
hpMethod: same
gpLearningRate/hpLearningRate: learning rate used in tensorflow gradient descent method
gpMaxIter/hpMaxIter: maximum # of iterations for gp/hp optimizer
useSaveGraph: whether or not use saved tensorflow graph (to expedite hessian computation process)
saveGraph: whether or not save tensorflow graph
useSaveHessian: whether or not use saved hessian matrix (real values)
stepNum: # of steps for bayesian optimization function
"""

pairs = dataPair
xu = dataUnlabel
xl = dataLabel
oracle = dataOracle
kernelType = 'rbf'
gpMethod = 'gradient descent'
hpMethod = 'gradient descent'
gpLearningRate = 0.3
gpMaxIter = 5
hpLearningRate = 0.0001
hpMaxIter = 5
optMaxIter = 5
useSaveGraph = 'no'
saveGraph = 'no'
useSaveHessian = 'no'
stepNum = 4

# run bayesian optimizer

bayesian_optimizer(pairs,xl,xu,oracle,kernelType,gpMethod,hpMethod,gpLearningRate,
	hpLearningRate,gpMaxIter,hpMaxIter,optMaxIter,useSaveGraph, saveGraph, useSaveHessian, stepNum)

