# Audio Digital Communication (ADC) System
Implement a digital communication system base on audio signal to transmit infomation from one laptop to another.

## Prerequisites
 
### SoundDevice
We use [Sounddevice](https://python-sounddevice.readthedocs.io/en/0.3.12/installation.html) for record and play the audio signal. Dowload and install instructions can be found at: https://python-sounddevice.readthedocs.io/en/0.3.12/installation.html

### Soundfile
We use [Soundfile](https://pysoundfile.readthedocs.io/en/0.9.0/) to read and write sound files. Dowload and install instructions can be found at:https://pysoundfile.readthedocs.io/en/0.9.0/

### Commpy
We use [Commpy](https://commpy.readthedocs.io/en/latest/index.html) to implementation channel coding. Dowload and install instructions can be found at:https://commpy.readthedocs.io/en/latest/index.html 

## Running the tests
To test the accuracy of the system (The recevier also need the sent file)

**file.txt** is the file you want to send.

**SOURCE.txt** is the file received.


For receiver 
```
python3 main.py --send 0 
```
For transmitter
```
python3 main.py --send 1 
```

Only receive without accuracy test, **test** should be set to 0.
```
python3 main.py --send 0/1 --test 0
```



