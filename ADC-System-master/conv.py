from numpy import array
import numpy as np
import commpy.channelcoding.convcode as cc
# memory = array([4])
# g_matrix = array([[ 109	 , 79]]) # G(D) = [1+D^2, 1+D+D^2]
memory = array([3])
g_matrix = array([[ 109 , 79]]) # G(D) = [1+D^2, 1+D+D^2]
trellis = cc.Trellis(memory, g_matrix)
# a='111101011'
# message_bits = np.array(list(a))
# coded_bits = cc.conv_encode(message_bits, trellis, code_type='default', puncture_matrix=None)
# print(coded_bits)

bit_num = 8414
print(bit_num)
num_bin  = '{:016b}'.format(bit_num)
num_bits = [ ]
for i in  num_bin:
	if i =='0':
		num_bits.append(0)
	elif i == '1':
		num_bits.append(1)

num_encoded = cc.conv_encode(num_bits, trellis, code_type='default', puncture_matrix=None)
print(num_encoded)
# coded_bits = np.array([0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0])
# print(coded_bits)
ans = cc.viterbi_decode(num_encoded, trellis, tb_depth=None, decoding_type='hard')
print(ans)
# print(str(message_bits))
# ans = np.array2string(np.random.randint(0,2,10000))
# ans+= '\n'
# print (ans)
# output_file = open("INPUT.txt", "w")
# output_file.write(ans)