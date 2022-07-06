import requests
import json
import time
from multiprocessing import Process

host = 'http://localhost:8083'
headers = {'Content-Type': 'application/json'}
workspace = 'workspace_2'
user = 'u1'


def start_recording():
    url = host + '/remote/recorder/workspaces/%(workspace)s/users/%(user)s/recordings' % {
        "workspace": workspace, "user": user}
    data = {
        "sessionId": "ses_PrATC23KkG",
        "token": "wss://192.168.0.9:8000?sessionId=ses_HRE5AYUoiR&token=tok_ZEzn3pVf1RJvcYal&role=PUBLISHER&version=2.0.0&recorder=true&options={\"iceServers\":[{\"username\":\"remote\",\"credential\":\"remote\",\"url\":\"turn:192.168.0.9:3478\"}],\"role\":\"PUSLISHER\",\"wsUri\":\"wss://192.168.0.9:8073/remote/websocket\"}",
        "resolution": "480p",
        "framerate": 20,
        "recordingTimeLimit": 5,
        "recordingFilename": "2020-10-30_12-00-01.mp4",
        "metaData": {
            "field1": "test",
            "field2": "test",
            "field3": {
                "field4": "test"
            }
        }
    }
    print("request: start-recording")
    res = requests.post(url, headers=headers, data=json.dumps(data))
    print("response: start-recording. recording_id:%s" %
          json.loads(res.text)["data"]["recordingId"])


def list_recordings():
    url = host + '/remote/recorder/workspaces/%(workspace)s/users/%(user)s/recordings' % {
        "workspace": workspace, "user": user}
    print("request: get-recordings")
    res = requests.get(url, headers=headers)
    print("response: get-recordings")
    return json.loads(res.text)["data"]["infos"]


def stop_recording(recording_id):
    url = host + '/remote/recorder/workspaces/%(workspace)s/users/%(user)s/recordings/%(recording_id)s' % {
        "workspace": workspace, "user": user, "recording_id": recording_id}
    print("request: stop-recordings. recording_id:%s" % recording_id)
    res = requests.delete(url, headers=headers)
    code = json.loads(res.text)["code"]
    print("response: stop-recordings. recording_id:%(recording_id)s res:%(code)s" %
          {"code": code, "recording_id": recording_id})


def list_and_stop_recording():
    list = list_recordings()
    for r in list:
        if r["status"] == "preparing":
            stop_recording(r["recordingId"])


th_start_recording = Process(target=start_recording, args=())
th_list_and_stop_recording = Process(target=list_and_stop_recording, args=())

th_start_recording.start()
time.sleep(1)
th_list_and_stop_recording.start()

th_start_recording.join()
th_list_and_stop_recording.join()
