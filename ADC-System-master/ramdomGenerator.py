import random
import huffman

s = ""
for i in range(500):
    s += chr(random.randint(48,122))
freq_dict = huffman.GetFreqE(s)
dict1 = huffman.HuffmanEncode(freq_dict)
encode = huffman.FileEncode(s, dict1, freq_dict)
f = open("file.txt", "r")
file = f.read()
f.close()
print(len(file))
