""""
This is a testing file for learning tensorflow or others
"""

import tensorflow as tf
import numpy as np

""""
Tensorflow hessian example
"""

x = tf.Variable(np.random.random_sample(), dtype=tf.float32)
y = tf.Variable(np.random.random_sample(), dtype=tf.float32)

def cons(x):
    return tf.constant(x, dtype=tf.float32)

""""
f=x^3+2*x*y+3*y^3
"""
f = tf.pow(x, cons(3)) + cons(2) * x * y + cons(3) * tf.pow(y, cons(3))

def compute_hessian(fn, vars):
    mat = []
    for v1 in vars:
        temp = []
        for v2 in vars:
            # computing derivative twice, first w.r.t v2 and then w.r.t v1
            temp.append(tf.gradients(tf.gradients(f, v2)[0], v1)[0])
        # tensorflow returns None when there is no gradient, so we replace None with 0
        temp = [cons(0) if t == None else t for t in temp] 
        temp = tf.stack(temp)
        mat.append(temp)
    mat = tf.stack(mat)
    return mat

hessian = compute_hessian(f, [x, y])

sess = tf.Session()
sess.run(tf.initialize_all_variables())
print sess.run(hessian)

""""
To feed data later, we use placeholder
A placeholder is simply a variable that we will assign data to at a later date. 
It allows us to create our operations and build our computation graph
"""

x = tf.placeholder("float", None)
z = tf.placeholder("float", None)
y = tf.gradients(tf.exp(x)+tf.exp(z),x)

with tf.Session() as session:
    result = session.run(y, feed_dict={x: [1, 2, 3],z:[2,3,3]})
    print(result)

# test using variables
a=1
b=2
with tf.Session() as session:
    result = session.run(y, feed_dict={x: a,z:b})
    print(result)

""""
Based on the setting, below is a test for gradient computation
"""
def choice_prob_binary():
	p=tf.exp(un)/(tf.exp(un)+tf.exp(up))
	return p

un=tf.placeholder("float",None)
up=tf.placeholder("float",None)
p=choice_prob_binary()
y=tf.gradients(p,un)

with tf.Session() as session:
    result = session.run(y, feed_dict={un: [1, 2, 3],up:[1,2,3]})
    print(result)

""""
Tensorflow test
"""

def probability(u):
    pr=tf.pow(u[0],2)+tf.pow(u[1],2)
    return pr

u = tf.Variable(np.float32(np.repeat(1,2).reshape(2,1)))
#u=tf.placeholder(tf.float32, shape=[2,1])
v = np.float32(np.repeat(1,2).reshape(2,1))
p=probability(u)
y=tf.gradients(p,u)
with tf.Session() as session:
    result = session.run(y, feed_dict={u:v})
    print(result)

""""
Tensorflow test
"""
x = tf.placeholder(tf.float32, shape=[3,1])
x1 = tf.slice(x,begin=[0,0],size=[2,1])
x2 = tf.slice(x,begin=[2,0],size=[1,1])
#x1 = tf.placeholder(tf.float32, shape=[2,1])
#x2 = tf.placeholder(tf.float32, shape=[1,1])
v = np.float32(np.repeat(1,3).reshape(3,1))
y =tf.log(tf.pow(x1[0,0],2)+x1[1,0]+x2[0,0])
#x = tf.concat([x1,x2],0)
#y = tf.log(x)
s = tf.gradients(y,x)
#init_op = tf.global_variables_initializer()
with tf.Session() as sess:
    #sess.run(init_op)
    res = sess.run(s,feed_dict = {x:v})
    print(res)

""""
Tensorflow test
"""
x = tf.placeholder(tf.float32, shape=[1,3])
v = np.float32(np.repeat(1,3).reshape(1,3))
y = tf.pow(x,2)


