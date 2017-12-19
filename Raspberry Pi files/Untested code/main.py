#!/usr/bin/env python
#-*- coding: UTF-8 -*-

import pifacecad
import os
import time
import signal
import sys
import qrScanner
import xmlUtils
import requestHandler

# kill on ctrl + c
def signal_handler(signal, frame):
    if sys.version_info < (3,0): 
        # the python2 code forks
        os.kill(os.getppid(),9)
    os.kill(os.getpid(),9)
signal.signal(signal.SIGINT, signal_handler)




# Main Routine: Choose Presented > Scan > Parse > Modify XML > Post Attendance > Show status

cad = pifacecad.PiFaceCAD()

listener = pifacecad.SwitchEventListener(chip=cad)

cad.lcd.clear()
cad.lcd.write('Student presented? Y/N')

def trackAttendance(presented):
    global cad

    cad.lcd.clear()
    cad.lcd.write('Scanning...')
    qrcode = qrScanner.scan()
    cad.lcd.clear()
    cad.lcd.write('Scan successfull!')
    xml = xmlUtils.parse(qrcode)
    xml = xmlUtils.setPresented(xml, presented)
    xmlString = xmlUtils.toString(xml)
    success = requestHandler.postAttendance(xml)
    
    cad.lcd.clear()
    
    time.sleep(1)
    if success:
        cad.lcd.write('Attendance was posted!')
    else:
        cad.lcd.write('An error occured...')
    
    time.sleep(1)
    cad.lcd.clear()
    cad.lcd.write('Student presented? Y/N')
    

def hasPresented(event):
    trackAttendance(True)
def hasNotPresented(event):
    trackAttendance(False)

listener.register(0, pifacecad.IODIR_FALLING_EDGE, hasPresented)
listener.register(1, pifacecad.IODIR_FALLING_EDGE, hasNotPresented)

listener.activate()
