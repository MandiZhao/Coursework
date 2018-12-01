#!/usr/bin/env python3


# Based on an example of sounddevice
# Link: https://python-sounddevice.readthedocs.io/en/0.3.12/examples.html#recording-with-arbitrary-duration
"""Create a recording with arbitrary duration.
PySoundFile (https://github.com/bastibe/PySoundFile/) has to be installed!
"""
import config
# import argparse
import tempfile
import queue
import sys
import numpy as np
import time

def int_or_str(text):
    """Helper function for argument parsing."""
    try:
        return int(text)
    except ValueError:
        return text

parser = config.get_parser()
args = parser.parse_args()

try:
    import sounddevice as sd
    import soundfile as sf
    import numpy  # Make sure NumPy is loaded before it is used in the callback
    assert numpy  # avoid "imported but unused" message (W0611)

    if args.list_devices:
        print(sd.query_devices())
        parser.exit(0)
    if args.samplerate is None:
        device_info = sd.query_devices(args.device, 'input')
        # soundfile expects an int, sounddevice provides a float:
        args.samplerate = int(device_info['default_samplerate'])
    if args.filename is None:
        args.filename = 'record.wav'
        # args.filename = tempfile.mktemp(prefix='delme_rec_unlimited_',
        #                                 suffix='.wav', dir='')

    q = queue.Queue()

    def callback(indata, frames, time, status):
        """This is called (from a separate thread) for each audio block."""
        if status:
            print(status, file=sys.stderr)
        q.put(indata.copy())



    def record():
        rec = []
        start = 0
        stop = 0
        # Make sure the file is opened before recording anything:
        with sf.SoundFile(args.filename, mode='x', samplerate=args.samplerate,
                          channels=args.channels, subtype=args.subtype) as file:
            with sd.InputStream(samplerate=args.samplerate, device=args.device,
                                channels=args.channels, callback=callback):
                print('#' * 50)
                print('Ready to receive')
                while not stop:
                    block_new = q.get()
                    # print(start,max(block_new) )

                    if start== 0 and max(block_new)>0.5:
                        start = 1
                        start_time = time.time()
                        print('#' * 50)
                        print('Begin to receive')
                        print('Start at:',start_time)

                    if start == 1:

                        rec = np.append(rec,block_new)
                        file.write(rec)   #  output the file
                    if start ==1 and max(block_new) < 0.2:
                        print('Receive stop')
                        print('#' * 50)
                        break
        return rec,start_time



except KeyboardInterrupt:
    print('\nRecording finished: ' + repr(args.filename))
    parser.exit(0)
except Exception as e:
    parser.exit(type(e).__name__ + ': ' + str(e))
