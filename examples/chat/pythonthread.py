#!/usr/bin/python
import threading
import os
from subprocess import Popen, PIPE

def chatHandler():
    Person1Count = 0
    Person2Count = 0
    process = Popen(["./chat.sh"], stdout=PIPE)
    while True:
        message = (process.stdout.readline().strip().decode('UTF-8'))
        if (message[:8] == "Person 1"):
            Person1Count += 1
        else:
            Person2Count += 1
        print("Person 1: " + str(Person1Count) + "\nPerson 2: " + str(Person2Count) + "\n")

#Start function in background
thread = threading.Thread(target = chatHandler)
thread.start()

#Do other work
#...

#Wait for thread to finish
thread.join()