import argparse
import os
import tempfile
import sys



def get_parser():
	parser = argparse.ArgumentParser()

	# basic config
	parser.add_argument('--send', help = ' send：1 receive：0s', default = 1, type=int)
	parser.add_argument('--test',help = 'do test', default = 1 ,type=int)
	parser.add_argument('--input,',help  = 'input file code', default = 0, type = int)
	parser.add_argument('--input_file', help ='input file name', default = 'INPUT.txt')
	parser.add_argument('--packet_size',help='packet size',default = 2000)
	parser.add_argument('--time',help = 'listening time',default = 10)
	# parser.add_argument('carrier_wave_freq', help =' text bit carrier wave frequency ', default = 2000)
	# sender
	parser.add_argument('--preamble',help='preamble code', default = 4)
	parser.add_argument('-- encode', help = 'repeat:0 hamming code:1 convolution code:2 ', default =0)
	parser.add_argument('--packet_len', help ='one packet length', default = 5000)





	##receiver
	parser.add_argument('--threshold', help = 'correlation threshold', default = 6)


	###  for recordpart
	parser.add_argument(
	    '-l', '--list-devices', action='store_true',
	    help='show list of audio devices and exit')
	parser.add_argument(
	    '-d', '--device', type=str,
	    help='input device (numeric ID or substring)')
	parser.add_argument(
	    '-r', '--samplerate', type=int, help='sampling rate',default = 48000)
	parser.add_argument(
	    '-c', '--channels', type=int, default=1, help='number of input channels')
	parser.add_argument(
	    'filename', nargs='?', metavar='FILENAME',
	    help='audio file to store recording to')
	parser.add_argument(
	    '-t', '--subtype', type=str, help='sound file subtype (e.g. "PCM_24")')
	# args = parser.parse_args()
	return parser
	# return args