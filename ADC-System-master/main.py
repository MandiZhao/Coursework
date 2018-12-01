import sounddevice as sd
import numpy as np
import comm
import huffman
import rec
import config 
import os
sd.default.samplerate = fs = 48000
sd.default.channels = 1
start_time = 0
end_time = 0
# args = get_args()
parser = config.get_parser()
args = parser.parse_args()
if __name__ == '__main__':
	print("Hi~ Welcome to project check script")
	if args.send:
		huffman.getHuffFile()
		print (	'You are sending signal...')
		while True:
			part = input("Press any key to send\n")
			# if part == 'e':
			# 	exit
			comm.play_file()
			print('Done\n')

	else:
		print (	'You are receiving signal...')
		if  os.path.exists('record.wav'):
			os.remove('record.wav')

		if args.test:
			try:
				huffman.getHuffFile()
			except:
				print(' Can not do test !')
		part = input("Press any key to receive\n")
		rec,start_time = rec.record()
		comm.decode(rec)
		huffman.getRealFile(start_time)
		print("Done\n")



