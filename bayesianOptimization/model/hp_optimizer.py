from tools import *
from numpy.linalg import inv
import numpy as np
import tensorflow as tf
import math

""""
compute hyperparameters for kernel function
"""

class hp_optimizer(object):

	def __init__(self,xl,inds,u0,theta0,
					kernelType,hpMaxIter):
		self.xl = xl
		self.inds = inds
		self.u0 = u0
		self.theta0 = theta0
		self.kernelType = kernelType
		self.hpMaxIter = hpMaxIter

	def hp_optimizer_gradientDescent(self,learningRate,method):
		# initialize hyperparameter array
		if self.kernelType == 'rbf':
			dimH = 1
		dimU = len(self.u0)
		# create initial values for optimization
		theta = tf.Variable(self.theta0, name='theta', dtype=tf.float32)
		# create a tensor matrix
		stacklist = []
		for i in range(dimU):
			for j in range(dimU):
				ind_i = int(self.inds[i])
				ind_j = int(self.inds[j])
				xi = tf.convert_to_tensor(self.xl[ind_i], dtype=tf.float32)
				xj = tf.convert_to_tensor(self.xl[ind_j], dtype=tf.float32)
				stacklist.append(tf.exp(-tf.norm(xi-xj,ord = 2)/(2*tf.square(theta))))
		# reshape stacklist to a matrix
		stacklist = tf.stack(stacklist)
		kernel = tf.reshape(stacklist,[dimU,dimU])
		kernelInv = tf.matrix_inverse(kernel)
		
		# create cost function
		u = tf.convert_to_tensor(self.u0)
		cost = 0.5*tf.log(tf.matrix_determinant(kernel)) \
				+0.5*tf.matmul(tf.matmul(tf.transpose(tf.reshape(u,[dimU,1])),kernelInv),tf.reshape(u,[dimU,1]))
		
		# call tensorflow's optimization tool
		opt = {}
		if method == 'gradient descent':
			opt = tf.train.GradientDescentOptimizer(learningRate)
		elif method == 'adam':
			opt = tf.train.AdamOptimizer(learningRate)
		train = opt.minimize(cost)
		
		# initialize variables
		init = tf.global_variables_initializer()
		with tf.Session() as session:
			session.run(init)
			for step in range(self.hpMaxIter):
				session.run(train)
				thetaOpt = session.run(theta)

		return thetaOpt
	