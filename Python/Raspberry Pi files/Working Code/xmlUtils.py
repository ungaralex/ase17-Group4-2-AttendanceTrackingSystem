import xml.etree.ElementTree as EL

def parseXml(data):
    return EL.fromstring(data)
    
def checkSuccess(xml):
    return xml.text == 'success'

def setPresented(xml, presented):
    presentedTag = EL.SubElement(xml, 'presented')
    presentedTag.text = str(presented)
    return xml

def toString(xml):
    return EL.tostring(xml, encoding='utf8', method='xml')