import xml.etree.ElementTree as EL

def parseXml(data):
    return EL.fromstring(qrcodetext)
    
def checkSuccess(xml):
    return xml.getroot().text != 'success'

def setPresented(xml, presented):
    presentedTag = EL.SubElement(xml.getroot(), 'presented')
    presentedTag.text = str(presented)
    return xml