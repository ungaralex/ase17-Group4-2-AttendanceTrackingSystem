#!/usr/bin/env python
#-*- coding: UTF-8 -*-
'''
erzeugt QR-Codes: muss dazu ausgedruckt werden
liest QR-Codes
'''
import os, signal, subprocess
import xml.etree.ElementTree as EL
import requests

strfile1 = "qrcode"
def erzeugen():
    text=raw_input("Geben Sie einen Text für den QRCode an: ")
    os.system("qrencode -o "+strfile1+".png '"+text+"'")
    print "QRCode unter: "+strfile1+".png"
def lesen():
    zbarcam=subprocess.Popen("zbarcam --raw --nodisplay /dev/video0", stdout=subprocess.PIPE, shell=True, preexec_fn=os.setsid)
    print "zbarcam erfolgreich gestartet..."
    while True:
        qrcodetext=zbarcam.stdout.readline()
        if qrcodetext!="":
            os.killpg(zbarcam.pid, signal.SIGTERM)
            data = EL.fromstring(qrcodetext)
            headers = {'Content-Type': 'application/xml'} # set what your server accepts
            print qrcodetext
            print requests.post('http://ase-2017-alex.appspot.com/rest/post/attendance', data=data, headers=headers)
            break
    print "zbarcam erfolgreich gestoppt"
    return qrcodetext