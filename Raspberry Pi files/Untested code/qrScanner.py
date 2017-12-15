#!/usr/bin/env python
#-*- coding: UTF-8 -*-
import os, signal, subprocess
import requests

def scan():
    zbarcam=subprocess.Popen("zbarcam --raw --nodisplay /dev/video0", stdout=subprocess.PIPE, shell=True, preexec_fn=os.setsid)
    print "zbarcam erfolgreich gestartet..."
    while True:
        qrcodetext=zbarcam.stdout.readline()
        if qrcodetext!="":
            break
    os.killpg(zbarcam.pid, signal.SIGTERM)
    print "zbarcam erfolgreich gestoppt"
    return qrcodetext