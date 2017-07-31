import numpy as np
import tensorflow as tf
from scipy.stats import norm
from numpy.linalg import inv
from prettytable import PrettyTable

from matplotlib import pyplot as plt
from matplotlib.pyplot import savefig as savefig

from sklearn.gaussian_process import GaussianProcessRegressor
from sklearn.gaussian_process.kernels import RBF

""""
read data into dictionary
"""

def read_into_key_values(dir):
	d={}
	with open(dir) as f:
		for line in f:
			(key, val) = line.split(',',1)
			d[float(key)] = (tuple(float(x) for x in val.split(',')))

	return d

""""
kernel_value_compute: compute distance
http://www.cs.toronto.edu/~duvenaud/cookbook/index.html

radial basis function:
k(x,x')=sigma^2 exp(-(x-x')^2/2theta^2)
squared exponential kernel:
k(x,x')=sigma^2 exp(-(x-x')^2/2theta^2)

inputs:
arrays x1,x2
string kernel type
"""

def kernel_value_compute(x1,x2,kernelType,theta):
	""""in case x1, x2 are no np array """
	x1=np.array(x1)
	x2=np.array(x2)
	if kernelType=='rbf':
		return np.exp(-np.sum((x1-x2)**2)/(2*theta**2))

	return 'please set your kernel type'

""""
kernel_matrix_compute: compute kernel matrix
"""

def kernel_matrix_compute(x,inds,kernelType,theta):
	"""" initialize a zero matrix """
	matrix=np.zeros((len(x),len(x)))
	for i in range(len(x)):
		for j in range(len(x)):
			ind_i = int(inds[i])
			ind_j = int(inds[j])
			matrix[i][j]=kernel_value_compute(x[ind_i],x[ind_j],kernelType,theta)

	return matrix

""""
a single probability function
"""

def pref_probability(um,un):
	# get elements from input tensor
	pr=tf.divide(tf.exp(um),(tf.exp(um)+tf.exp(un)))

	return pr

""""
a cost function only for prod_{(i,j)} p(x_i > x_j|f)
"""

def cost_function_wrapper(u,pairs,posDict):
	ll = 0
	for key in pairs:
		p = pairs[key]
		# get position in variables
		posm = posDict[p[0]]
		posn = posDict[p[1]]
		# slice the variables to each pair
		um = tf.slice(u,begin=[posm],size=[1])
		un = tf.slice(u,begin=[posn],size=[1])
		ll = ll + tf.log(pref_probability(um,un))

	# output is negative log-likelihood
	return -ll

""""
build tensorflow graph for hessian
"""

def get_gp_hessian(uVars, u, pairs, posDict, dim, useSaveGraph, saveGraph):
	ll = cost_function_wrapper(uVars,pairs,posDict)
	varFeeds = np.float32(u.reshape((len(u))))
	# whether use saved graph or build a new graph
	if useSaveGraph == 'yes':
		sess = tf.Session()
		saver = tf.train.import_meta_graph('hessian_model.meta')
		saver.restore(sess,tf.train.latest_checkpoint('./'))
		graph = tf.get_default_graph()
		uVars = graph.get_tensor_by_name("uVars:0")
		feed_dict = {uVars:varFeeds}
		hess = graph.get_tensor_by_name("hess:0")
	else:
		hess = tf.hessians(ll, uVars, name = 'hess')[0]
		sess = tf.Session()
		sess.run(tf.global_variables_initializer())
		saver = tf.train.Saver()
		feed_dict = {uVars:varFeeds}	
	res = sess.run(hess,feed_dict)
	# to save hess graph
	if saveGraph == 'yes':
		save_path = saver.save(sess, 'hessian_model')

	return res

""""
return a list of unique and sorted instances' keys
"""

def get_inds_from_pairs(pairs):
	res = []
	for key in pairs:
		res.append(pairs[key][0])
		res.append(pairs[key][1])

	return list(sorted(set(res)))

""""
return a dictionary storing {instance key ~ position in inds}
"""

def get_pos_check_dict(inds):
	res = {}
	for i in range(len(inds)):
		res[inds[i]] = i

	return res

""""
hessian test
the purpose of this function is to verify hessian computation
compare it with tensorflow's tf.hessians() function
we test z = x^2 + y^3
"""

def hessian_test():
	dim = 2
	varAll = tf.Variable(np.float32(np.repeat(1,dim).reshape(dim,1)))
	x = tf.slice(varAll,begin=[0,0],size=[1,1])
	y = tf.slice(varAll,begin=[1,0],size=[1,1])
	f = tf.pow(x,2) + tf.pow(y,3)
	df = tf.gradients(f,varAll)[0]
	for i in range (dim):
	# take the i th value of the gradient vector dll
		dff_i = tf.slice(df,begin=[i,0],size=[1,1])
		# feed it to tf.gradients to compute the second derivative.
		ddff_i = tf.gradients(dff_i, varAll)[0]
		if i == 0: hess = ddff_i
		else: hess = tf.concat([hess, ddff_i],1)
	xy = np.array([1,1])
	varFeeds = np.float32(xy.reshape((dim,1)))
	with tf.Session() as sess:
		feed_dict = {varAll:varFeeds}
        res = sess.run(hess, feed_dict)

	return res

""""
oracle check
if true:
	x1 is preferred than x2
"""

def oracle_check(x1,x2,oracle):
	if oracle[x1] < oracle[x2]:
		return True
	else:
		return False

""""
print out results
"""

def gp_print(inds,u,cov):
	var = cov.diagonal()
	p = PrettyTable(['instance #','hidden means', 'variances'])
	for i in range(len(u)) :
		p.add_row([int(inds[i]),"{0:.4f}".format(u[i]),"{0:.4f}".format(var[i])])
	print p
	
	return None

""""
class to compute distributions
"""

class gp_distribution(object):

	def __init__(self,inds,u,xl,kernel,hb):
		self.inds = inds
		self.u = u
		self.xl = xl
		self.kernel = kernel
		self.hb = hb

	def get(self):
		mean = self.u
		cov = inv(inv(self.kernel)+self.hb)
		
		return {'mean':mean,'cov':cov}

	def predict(self,xt,theta,kernelType,ub):
		# compute kt
		# kernel distances between instance xt with other instances in xl
		kt = np.zeros(len(self.u))
		for i in range(len(self.u)):
			ind_i = int(self.inds[i])
			kt[i] = kernel_value_compute(self.xl[ind_i],xt,kernelType,theta)
		# compute mean
		mean = np.matmul(np.matmul(np.transpose(kt),inv(self.kernel)),self.u)
		# compute var
		var = kernel_value_compute(xt,xt,kernelType,theta) \
				-np.matmul(np.matmul(np.transpose(kt),inv(inv(self.hb)+self.kernel)),kt)
		# compute expected improvement
		ei = norm.cdf((mean-ub)/var**0.5)
		
		return {'mean':mean,'var':var, 'ei': ei}

""""
a function to plot
"""

def gp_plot(u,xl,ind,step,xlb,ulb):
	# compute converted distance: x values in x-y plot
	dimXl = len(xl[xl.keys()[0]])
	dimU = len(u)
	origin = np.zeros(dimXl)
	convertedDis = np.zeros(dimU)
	for i in range(len(ind)):
		convertedDis[i] = np.linalg.norm(xl[ind[i]]-origin)
	convertedDis = np.atleast_2d(convertedDis).T
	# compute dummy points for curve
	x = np.atleast_2d(np.linspace(np.min(convertedDis)-0.15, np.max(convertedDis)+0.15, 1000)).T
	# compute dummy ys based on dummy points
	kernel = RBF(1, (1e-2, 1e2))
	gp = GaussianProcessRegressor(kernel=kernel, n_restarts_optimizer=9)
	gp.fit(convertedDis,u)
	u_pred, sigma = gp.predict(x, return_std=True)

	fig = plt.figure()
	plt.plot(convertedDis, u, 'r.', markersize=10, label=u'Observations')
	plt.plot(x, u_pred, 'b-')
	plt.fill(np.concatenate([x, x[::-1]]),
	         np.concatenate([u_pred - 1.9600 * sigma,
	                        (u_pred + 1.9600 * sigma)[::-1]]),
	         alpha=.5, fc='b', ec='None', label='95% confidence interval')
	
	# compute (x,y) for addtional point
	convertedXlb = np.linalg.norm(xlb-origin)
	plt.plot(convertedXlb,ulb, 'r.', marker = 'x', markersize = 6, label=u'Next to sample')
	plt.xlabel('$x(converted)$')
	plt.ylabel('$f(x)$')
	plt.ylim(-7, 7)
	plt.legend(loc='upper left')
	fig.set_size_inches(10, 5)
	fn = 'graph/fig' + str(step)
	savefig(fn, bbox_inches='tight')

	return None






