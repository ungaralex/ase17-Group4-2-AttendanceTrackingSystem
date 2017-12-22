import requests
import xmlUtils

def postAttendance(data):
    
    try:
        headers = {'Content-Type': 'application/xml'} # set what your server accepts
        response = requests.post('http://ase-2017-alex.appspot.com/rest/post/attendance', data=data, headers=headers)

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