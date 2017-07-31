from tools import *
from numpy.linalg import inv
from gp_optimizer import *
from hp_optimizer import *
import numpy as np
import tensorflow as tf

""""
gamma is the threshold for this optimizer
gammaG is the threshold for newton optimization
gammaH is the threshold for gradient descent
let's currently set threshold as hyperparameter changes
"""
class optimizer(object):
	def __init__(self,pairs,xl,kernelType,gpMethod,hpMethod,gpLearningRate = 0.1,
				hpLearningRate = 0.0001,gpMaxIter = 10 ,hpMaxIter = 10,optMaxIter = 10,
				useSaveGraph = 'no',saveGraph ='no',useSaveHessian ='yes'):
		self.pairs = pairs
		self.xl = xl
		self.kernelType = kernelType
		self.gpMethod =gpMethod
		self.hpMethod = hpMethod
		self.gpLearningRate = gpLearningRate
		self.hpLearningRate = hpLearningRate
		self.gpMaxIter = gpMaxIter
		self.hpMaxIter = hpMaxIter
		self.optMaxIter = optMaxIter
		self.useSaveGraph = useSaveGraph
		self.saveGraph = saveGraph
		self.useSaveHessian = useSaveHessian

	def compute(self):
		# initialize a kernel theta
		if self.kernelType == 'rbf':
			dimH = 1
		
		# theta0: hyper parameter
		# dimension depends on kernel function
		theta0 = np.repeat(0.5,dimH)
		
		# initialize an array of hidden values
		# dimG includes dimension for array u
		inds = get_inds_from_pairs(self.pairs)
		posDict = get_pos_check_dict(inds)
		dimG = len(inds)
		u0 = np.repeat(0.5,dimG)

		print 'Log info: start while loop in optimizer function'	
		iterCount = 0
		thetaOpt = theta0
		uOpt = u0
		while (iterCount < self.optMaxIter):
			
			""""Optimization on hidden values"""			
			gp = gp_optimizer(self.pairs,
								self.xl,uOpt,thetaOpt,
								self.kernelType,posDict,
								inds,self.gpMaxIter)

			uOpt = gp.gp_optimizer_gradientDescent(self.gpLearningRate,self.gpMethod)

			""""Optimization on kernel parameter"""
			hp = hp_optimizer(self.xl,
								inds,uOpt,
								thetaOpt,self.kernelType,
								self.hpMaxIter)

			thetaOpt = hp.hp_optimizer_gradientDescent(self.hpLearningRate,self.hpMethod)
			iterCount = iterCount + 1
		print 'Log info: finish optimization step'
		
		# based on optimized u, compute hessian matrix	
		if self.useSaveHessian == 'yes':
			hb = np.loadtxt(open("model/hessian.csv", "rb"), delimiter=",")
		else:
			uVars = tf.Variable(tf.zeros([dimG]), name='uVars')
			hb = get_gp_hessian(uVars, uOpt, self.pairs, posDict, dimG, self.useSaveGraph, self.saveGraph)
			np.savetxt('model/hessian.csv', hb, delimiter=',')
		
		# compute kernel matrix
		kernel = kernel_matrix_compute(self.xl,inds,self.kernelType,theta0)

		return {'u':uOpt,'hp':thetaOpt,'kernel':kernel,'inds':inds,'hb':hb}
