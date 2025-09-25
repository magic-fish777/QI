# -*- coding: utf-8 -*-
# @Time    : 2025/9/24 23:01
# @Author  : yeye
# @File    : asr.py
# @Software: PyCharm
# @Desc    :
import requests
from config import ASR_URL, HEADERS

def asr_from_url(audio_url):
    payload = {
        "model": "asr",
        "audio": {
            "format": "mp3",
            "url": audio_url
        }
    }
    response = requests.post(ASR_URL, headers=HEADERS, json=payload)
    result = response.json()
    print("识别结果:", result)
    text = result.get("data", {}).get("result", {}).get("text", "")
    return text
