import numpy as np 
from tools import *
from gp_optimizer import *
from optimizer import *
from hp_optimizer import *

""""
This function is the framework for bayesian optimization
"""

def bayesian_optimizer(pairs,xl,xu,oracle,kernelType,
						gpMethod,hpMethod,
						gpLearningRate,hpLearningRate,
						gpMaxIter,hpMaxIter,
						optMaxIter,useSaveGraph, 
						saveGraph,useSaveHessian,
						stepNum):

	print 'Log info: bayesian optimization starts'
	for t in range(stepNum):
		# run optimizer
		res = optimizer(pairs,xl,kernelType,
							gpMethod,hpMethod,gpLearningRate,
							hpLearningRate,gpMaxIter,hpMaxIter,
							optMaxIter,useSaveGraph, saveGraph, 
							useSaveHessian).compute()
		inds = res['inds']
		# get current estimations
		dist = gp_distribution(inds,res['u'],xl,res['kernel'],res['hb'])
		# get current highest utility/quality
		ub = np.max(dist.get()['mean'])
		keycb = inds[np.argmax(dist.get()['mean'])]
		xcb = xl[keycb]
		# print out results for this step
		gpPrint = gp_print(res['inds'],dist.get()['mean'],dist.get()['cov'])
		# loop through all unlabeled observations and find the highest expected improvement
		xlb = None
		keylb = None
		ei0 = 0
		ulb = 0
		for key in xu:
			xt = xu[key]
			pred = dist.predict(xt,res['hp'],kernelType,ub)
			ei1,ulb = pred['ei'],pred['mean']
			if ei1 > ei0:
				ei0 = ei1
				xlb = xt
				keylb = key
		# plot GP and output to step t's fig
		# add the predicted point to chart
		gpPlot = gp_plot(dist.get()['mean'],xl,res['inds'],t,xlb,ulb)
		# ask oracle to compare xlb and xcb
		if oracle_check(keylb,keycb,oracle) == True:
			# update pairwise relation
			pairs[max(pairs.keys(),key=int)+1] = (keylb,keycb)
		else:
			pairs[max(pairs.keys(),key=int)+1] = (keycb,keylb)
		# add xlb to xl
		xl[keylb] = xlb
		# remove xlb from unlabeled data
		del xu[keylb]
	print 'Log info: bayesian optimization ends'
	
	return None

