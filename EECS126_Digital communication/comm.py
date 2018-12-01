# coding=utf-8
import sounddevice as sd
import matplotlib.pyplot as plt
import numpy as np
import math
import config
import commpy.channelcoding.convcode as cc
import heapq
from scipy.io import wavfile



###############################################
#convolutional code parameters
memory_len = 3
memory = np.array([memory_len])
g_matrix = np.array([[ 109 ,  79]]) # G(D) use IEEE 802.11 standard
trellis = cc.Trellis(memory, g_matrix)

# #######################################
# global parameters and setting
# args = get_args()
# send = args.send  # True for send ; False for receive
parser = config.get_parser()
args = parser.parse_args()
sd.default.samplerate = 48000
sd.default.channels = 1 # play channel 
carrier_wave_freq = 2000 # bit 
bit_rate = 1000
bit_len = int(sd.default.samplerate/bit_rate)

packet_size = args.packet_size 
time = args.time # listening time for receiver


test = args.test   # print test indicator
# repeat = False # no need repeat as of now
#############################################



###############################################
# Generate Bit signal 

one_ms = np.linspace(0, 1 / bit_rate, bit_len, endpoint = False)
signal_one = np.sin(2 * np.pi * carrier_wave_freq * one_ms + np.pi)
signal_zero = np.sin(2 * np.pi * carrier_wave_freq * one_ms)

###################################################################
# Generate Preamblem 
######################################################
preamble1 = np.zeros(5*bit_len)
preamble1[0:4*bit_len] = -np.cos(2*np.pi*carrier_wave_freq*(np.linspace(0, 4/bit_rate, 4*bit_len)))

threshold1 = 0.9

if  args.preamble == 1:
	preamble = preamble1
	threshold = threshold1
########################################################
preamble2 = np.zeros(10*bit_len)
preamble2[0:4*bit_len] = -np.cos(2*np.pi*1000*(np.linspace(0, 4/bit_rate, 4*bit_len)))
preamble2[4*bit_len:8*bit_len] = -np.sin(2*np.pi*500*(np.linspace(0, 4/bit_rate, 4*bit_len)))

threshold2 = 0.7

if  args.preamble == 2:
	preamble = preamble2
	threshold = threshold2
#############################################
preamble3 = np.zeros(10*bit_len)
preamble3[0:4*bit_len] = -np.cos(2*np.pi*3000*(np.linspace(0, 4/bit_rate, 4*bit_len)))

threshold3 = 0.7

if  args.preamble == 3:
	preamble = preamble3
	threshold = threshold3
# #######################################

preamble4 = np.zeros(10*bit_len)
preamble4[0:2*bit_len] = -np.cos(2*np.pi*3000*(np.linspace(0, 2/bit_rate, 2*bit_len)))
preamble4[2*bit_len:4*bit_len] = -np.sin(2*np.pi*3000*(np.linspace(0, 2/bit_rate, 2*bit_len)))
preamble4[4*bit_len:6*bit_len] = np.cos(2*np.pi*3000*(np.linspace(0, 2/bit_rate, 2*bit_len)))
preamble4[6*bit_len:8*bit_len] = np.sin(2*np.pi*3000*(np.linspace(0, 2/bit_rate, 2*bit_len)))

threshold4 = 0.5

if  args.preamble == 4:
	preamble = preamble4
	threshold = threshold4
	
#####################################


preamble_energy = math.sqrt(np.dot(preamble,preamble))

def get_weight(arr1,arr2):
	arr2 = arr2[2:-2]
	l1 = len(arr1)
	l2 = len(arr2)
	c = []
	c.append(sum(arr1[0:l2]*arr2))
	for i in range(l1-l2):
		c.append(sum(arr1[i:i+l2]*arr2))
	return max(c)



# #######################################
# Modulate Function
def add_one(arr,index):
	if type(arr) is not np.ndarray:
		print("add : wrong input")
		exit()
	else:
		arr[index:index+bit_len] = signal_one
		return bit_len

def add_zero(arr,index):
	if type(arr) is not np.ndarray:
		print("add : wrong input")
		exit()
	else:
		arr[index:index+bit_len] = signal_zero
		return bit_len

def add_preamble(arr, index):
	preamble_len = len(preamble)
	arr[index : index+ preamble_len] = preamble
	return index + preamble_len

#################################################
# Sychronize Function
def find_header(v,index):
	# v= arr
	n = index
	correlation = np.dot(preamble, v[n: n + len(preamble)])
	energy = math.sqrt(np.dot(v[n: n + len(preamble)], v[n: n + len(preamble)]))
	ratio = correlation / energy / preamble_energy
	# find the potential slice
	# print(n,ratio)
	while ratio<threshold:
		correlation = np.dot(preamble, v[n: n + len(preamble)])
		energy = math.sqrt(np.dot(v[n: n + len(preamble)], v[n: n + len(preamble)]))
		ratio = correlation / energy / preamble_energy
		n += 1
		if n + len(preamble) > len(v):
			print(' Can not find header')
			break

	print("start point :",n)
	max_ratio = 0

	# find the maximal point of correlation
	for i in range(1, 1000):
		correlation = np.dot(preamble, v[n + i: n + i + len(preamble)])
		energy = math.sqrt(np.dot(v[n + i: n + i + len(preamble)], v[n + i: n + i + len(preamble)]))
		curr_ratio = correlation / energy / preamble_energy
		if curr_ratio > max_ratio:
			max_ratio = curr_ratio
			header = n + i
	print("peak point :",header)
	idx = header + len(preamble)
	return idx




def play_file():
	# Read in the Input File
	f = open(args.input_file, 'r')
	context = f.readlines()
	f.close()
	if (len(context) != 1):
		print("wrong input file")
		exit()
	text = context[0]


	######### bit part
	message_bits = []
	for i in text:
		if i =='0':
			message_bits.append(0)
		elif i == '1':
			message_bits.append(1)
	text_encoded = cc.conv_encode(message_bits, trellis, code_type='default', puncture_matrix=None)
	
	print('Actual bits number: ',len(text_encoded))
	###########
	bit_num = len(text_encoded)
	print('Transmit bits number: ',bit_num)
	num_bin  = '{:016b}'.format(bit_num)
	num_bits = [ ]
	for i in  num_bin:
		if i =='0':
			num_bits.append(0)
		elif i == '1':
			num_bits.append(1)

	num_encoded = cc.conv_encode(num_bits, trellis, code_type='default', puncture_matrix=None)

	# print(num_encoded)



	output_wave = np.zeros(500*bit_len + (bit_num)*bit_len )
	index = 0
	# send the first packet
	index = add_preamble(output_wave, index)
	for i in num_encoded:
		if i == 0:
			index += add_zero(output_wave,index)
		elif i == 1:
			index += add_one(output_wave,index)

	
	num = 0 
	for i in text_encoded:
		if num % packet_size ==0:
			index  += 10*bit_len
			index = add_preamble(output_wave, index)
		num += 1
		if i == 0:
			index += add_zero(output_wave,index)
		elif i == 1:
			index += add_one(output_wave,index)
	


	sd.play(output_wave, blocking=True)





def decode(v): # demodulate and decode 
	idx = 0
	


	# Decode the Bit Length
	print('Bit Length Packet...')
	idx = find_header(v,idx)
	start_point = idx
	ans = [ ] # received bit list 
	for i in range(2*(16+memory_len)): # 32 bit for bit_len info (add mempry_len due to conv code prop)
		bit = v[idx : idx + bit_len ]
		weight0 = get_weight(signal_zero,bit)
		weight1 = get_weight(signal_one,bit)
		if weight0 >= weight1: ans.append(0)
		else: ans.append(1)
		idx += bit_len
	# print(ans)

	# decode bit length info by Veterbi Algorithm
	ans = np.array(ans)         
	ans = cc.viterbi_decode(ans, trellis, tb_depth=None, decoding_type='hard')[:-memory_len]
	num_decoded = ''
	for i in range(len(ans)):
		if ans[i] == 1:
			num_decoded +='1'
		elif ans[i] == 0:
			num_decoded +='0'
	#Print encoded and decoded  bit num
	# print('bit num endoded info: ', num_decoded)
	bit_num = int(num_decoded,2)
	print('The num of bits will be received is: ', bit_num)


	# Decode Text Part
	ans = []
	packet_num = 0
	for j in range(bit_num):
		
		if j % packet_size == 0: # if a new packet begin 
			idx += 3*bit_len
			packet_num +=1 
			print('\nText info packet' , packet_num)
			idx = find_header(v,idx) # Re-synchronize

		bit = v[idx : idx + bit_len]
		weight0 = get_weight(signal_zero,bit)
		weight1 = get_weight(signal_one,bit)
		if weight0 >= weight1: ans.append(0)
		else: ans.append(1)
		# ans.append(weight1)
		idx += bit_len
	end_point = idx


	# decode by Veterbi Algorithm
	ans = np.array(ans)         
	ans = cc.viterbi_decode(ans, trellis, tb_depth=None, decoding_type='hard')[:-memory_len]
	text_decoded = ''
	for i in range(len(ans)):
		if ans[i] == 1:
			text_decoded +='1'
		elif ans[i] == 0:
			text_decoded +='0'
	
	if test:
		f = open("INPUT.txt", 'r')
		context = f.readlines()
		f.close()
		if (len(context) != 1):
			print("wrong input file")
			exit()
		text = context[0]
		print('\nTransimission time: ' , (end_point - start_point)/sd.default.samplerate,'s')
		print('Receive encoded bit num: ',bit_num)
		print('Receive decoded bit num: ', len(text_decoded))
		print('Actual send bit', len(text))
		err = 0
		err_list = []
		for i in range(min(len(text_decoded),len(text))):
			if text[i] != text_decoded[i]:
				err += 1
				err_list.append(i)
		print("Total error bit num : ", err)
		print('Acuuracy rate :' , 1-err/len(text))
		print('Error list:', err_list,'\n')


		output_file = open("OUTPUT.txt", "w")
		output_file.write(text_decoded)


	else:
		output_file = open("OUTPUT.txt", "w")
		output_file.write(text_decoded)





# ############################################
# Abandon as of now 
# # include repeat
# 	else:
# 		print('Begin to decode the first packet....')
# 		ans_list = []
# 		bit_num = 0 
# 		max_power = 0
# 		last_power = 0
# 		while True:
# 			bit = v[idx : idx + bit_len]
# 			power = np.dot(preamble, v[idx : idx + 5 * bit_len])
# 			energy  = np.linalg.norm(bit)
# 			# print (energy)
# 			# print(power)
# 			if power < last_power and last_power == max_power and last_power > 15*energy and power > 0 :
# 				 # print('Received bit num',bit_num-1)
# 				ans = ans[:-1]
# 				break
# 			last_power = power
# 			max_power = max(max_power,power)
# 			weight0 = sum(signal_zero * bit)
# 			weight1 = sum(signal_one * bit)
# 			# print ('0 weight: ', weight0)
# 			# print('1 weight:', weight1)
# 			ans_list.append([weight0,weight1])
# 			idx += bit_len
# 			bit_num += 1


# 		print('\nLooking for the scond packet...')
# 		n = idx + 10*bit_len
# 		power = np.dot(preamble, v[n: n + 5 * bit_len])
# 		 # according to the device


# 		# find the header
# 		while power < threshold:
# 			power = np.dot(preamble, v[n: n + 5 * bit_len])
# 			n += 1
# 		print("first step :",n)
# 		max_power = 0
# 		header = n
# 		for i in range(1, 1):
# 			curr_power = np.dot(preamble, v[n + i: n + i + 5 * bit_len])
# 			if curr_power > max_power:
# 				max_power = curr_power
# 				header = n + i
# 		print("second step :",header)
# 		idx =  header + bit_len * 5 # pass the preamble

# 		print('Begin to decode the second packet....')
# 		first_bit_num = bit_num -2
# 		bit_num = 0 
# 		max_power = 0
# 		last_power = 0
# 		while True:
# 			if bit_num > first_bit_num:
# 				break
# 			bit = v[idx : idx + bit_len]
# 			power = np.dot(preamble, v[idx : idx + 5 * bit_len])
# 			energy  = np.linalg.norm(bit)
# 			# print (energy)
# 			# print(power)
# 			if power < last_power and last_power == max_power and last_power > 15*energy and power > 0 :
# 				ans = ans[:-1]
# 				break
# 			last_power = power
# 			max_power = max(max_power,power)
# 			weight0 = sum(signal_zero * bit)
# 			weight1 = sum(signal_one * bit)
# 			ans_list[bit_num][0]+= weight0
# 			ans_list[bit_num][1]+= weight1
# 			idx += bit_len
# 			bit_num += 1

# 		for i in range(bit_num):

# 			if ans_list[i][0] >= ans_list[i][1]:
# 				ans.append(0)   
# 			else: 
# 				ans.append(1)  
# 		ans = np.array(ans)         
# 		ans = cc.viterbi_decode(ans, trellis, tb_depth=None, decoding_type='hard')[:-2]
# 		text_decoded = ''
# 		for i in range(len(ans)):
# 			if ans[i] == 1:
# 				text_decoded +='1'
# 			elif ans[i] == 0:
# 				text_decoded +='0'
		
# 		if test:
# 			f = open("INPUT.txt", 'r')
# 			context = f.readlines()
# 			f.close()
# 			if (len(context) != 1):
# 				print("wrong input file")
# 				exit()
# 			text = context[0]
# 			print(text_decoded)
# 			print(text)
# 			print('Receive bit num: ',bit_num)
# 			print('Text bit num', len(text))
# 			err = 0
# 			err_list = []
# 			for i in range(len(text)):
# 				if text[i] != text_decoded[i]:
# 					err += 1
# 					err_list.append(i)
# 			print("total error : ", err)
# 			print('error rate :' , err/bit_num)
# 			print('error list:', err_list)
# 		else:

# 			print('Receive bit num: ',bit_num)
# 			print('Decode as below: ')
# 			print(ans)
# 			output_file = open("OUTPUT.txt", "w")
# 			output_file.write(ans)