import xmlUtils
import requests
from requests.auth import HTTPBasicAuth

def postAttendance(data):
    
    try:
        headers = {'Content-Type': 'application/xml'} # set what your server accepts
        response = requests.post('http://ase-2017-alex.appspot.com/rest/tut/postAttendance', data=data, headers=headers, auth=HTTPBasicAuth('ase-tutor', 'ase2017'))
        #response = requests.post('http://ase2017-att-tracker.appspot.com/rest/tut/postAttendance', data=data, headers=headers, auth=HTTPBasicAuth('ase-tutor', 'ase2017'))
        
        # Consider any status other than 2xx an error
        if not response.status_code // 100 == 2:
            print format(response)
            return False
        
        # Response should have <saved> tag with either success / error
        xml = xmlUtils.parseXml(response.text)
        return xmlUtils.checkSuccess(xml)
    except requests.exceptions.RequestException as e:
        # A serious problem happened, like an SSLError or InvalidURL
        print format(e)
        return False