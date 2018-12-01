from heapq import heappush, heappop
import os
import time
def GetFreqE(file):
    freq_dict = {}
    for i in file:
        if i in freq_dict.keys():
            freq_dict[i] += 1
        else:
            freq_dict[i] = 1
    return freq_dict

def HuffmanEncode(freq_dict):
    flip2huff = []
    result = {}
    for i in freq_dict:
        heappush(flip2huff, (freq_dict[i], [i]))
        result[i] = ""
    while len(flip2huff) > 1:
        s1 = heappop(flip2huff)
        s2 = heappop(flip2huff)
        sback = (s1[0] + s2[0], [])
        for i in s1[1]:
            result[i] = '0' + result[i]
            sback[1].append(i)
        for i in s2[1]:
            result[i] = '1' + result[i]
            sback[1].append(i)
        heappush(flip2huff, sback)
    return result

def toBin(a):
    result = ""
    while a>0:
        result = str(a%2)+result
        a = a//2
    result = '0'*(8-len(result))+result
    return result

def FileEncode(file, dict, freq_dict):
    for i in freq_dict.keys():
        freq_dict[i] = toBin(freq_dict[i])
    k = ["00000000" for _ in range(128)]
    for i in freq_dict.keys():
        k[ord(i)] = freq_dict[i]
    result = ""
    for i in k:
        result += i
    for i in file:
        result += dict[i]
    return result
def toDec(a):
    result = 0
    for i in range(len(a)):
        result += 2**(len(a)-i-1)*int(a[i])
    return result

def GetFreqD(freq_dict):
    k = {}
    for i in range(128):
        if (freq_dict[8*i:8*i+8]!="00000000"):
            k[chr(i)] = toDec(freq_dict[8*i:8*i+8])
    # print(k)
    return k

def FileDecode(file,dict):
    a = dict.values()
    new_dict = {v: k for k, v in dict.items()}
    result = ""
    tmp = ""
    for i in file:
        tmp += i
        if tmp in a:
            result += new_dict[tmp]
            tmp = ""
    return result

def getHuffFile():
    f = open("file.txt", "r")
    file = f.read()
    f.close()
    freq_dict = GetFreqE(file)
    dict1 = HuffmanEncode(freq_dict)
    encode = FileEncode(file, dict1, freq_dict)
    f = open("INPUT.txt", "w")
    f.write(encode)
    f.close()

def getRealFile(start_time):
    if os.path.exists("OUTPUT.txt"):
        f = open("OUTPUT.txt", "r")
        file = f.read()
        f.close()
        freq_dict = file[0:128 * 8]
        file = file[128 * 8:]
        freq_dict = GetFreqD(freq_dict)
        dict = HuffmanEncode(freq_dict)
        decode = FileDecode(file, dict)
        print(decode)
        end_time = time.time()
        print('\nEnd at:',end_time)
        print('Total time:', (end_time - start_time),'s')
        f = open("SOURCE.txt", "w")
        f.write(decode)
        f.close()

if __name__ == '__main__':
    getRealFile()