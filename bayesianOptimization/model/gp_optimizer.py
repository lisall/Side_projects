from tools import *
from numpy.linalg import inv
import numpy as np
import tensorflow as tf

""""
This is a class used to compute hidden value u 
for each instance
"""

class gp_optimizer(object):

	def __init__(self,pairs,xl,u0,theta0,
					kernelType,posDict,
					inds,gpMaxIter):
		self.pairs = pairs
		self.xl = xl
		self.u0 = u0
		self.theta0 = theta0
		self.kernelType = kernelType
		self.posDict = posDict
		self.inds = inds
		self.gpMaxIter = gpMaxIter
	
	def gp_optimizer_gradientDescent(self,learningRate,method):
		dim = len(self.u0)
		# set up an initial variable array
		u = tf.Variable(self.u0, name='u', dtype=tf.float32)
		
		# compute kernel matrix
		kernel = kernel_matrix_compute(self.xl,self.inds,self.kernelType,self.theta0)
		kernel = tf.convert_to_tensor(kernel, dtype=tf.float32)
		# construct the cost function
		ll = cost_function_wrapper(u,self.pairs,self.posDict) \
			+0.5*tf.matmul(tf.matmul(tf.transpose(tf.reshape(u,[dim,1])),kernel),tf.reshape(u,[dim,1]))
		
		opt = {}
		# build optimizer
		if method == 'gradient descent':
			opt = tf.train.GradientDescentOptimizer(learningRate)
		elif method == 'adam':
			opt = tf.train.AdamOptimizer(learningRate)
		train = opt.minimize(ll)
		
		# initialize variables
		init = tf.global_variables_initializer()
		with tf.Session() as session:
			session.run(init)
			for step in range(self.gpMaxIter):
				session.run(train)
				uOpt = session.run(u)
		
		return uOpt




