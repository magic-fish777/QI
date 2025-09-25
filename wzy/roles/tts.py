# -*- coding: utf-8 -*-
# @Time    : 2025/9/24 23:01
# @Author  : yeye
# @File    : tts.py
# @Software: PyCharm
# @Desc    :
import requests, base64, os, time, uuid
from playsound import playsound
from config import TTS_URL, API_KEY, HEADERS
from role_manager import get_role_voice


# def tts_and_play(text, role_key=None, out_file=None):
#
#     voice_type = get_role_voice(role_key)
#     if not voice_type:
#         print(f"[WARN] 角色 {role_key} 对应音色未找到，使用默认音色")
#         voice_type = "qiniu_zh_female_wwxkjx"
#
#     data = {
#         "audio": {
#             "voice_type": voice_type,
#             "encoding": "mp3",
#             "speed_ratio": 1.0
#         },
#         "request": {"text": text}
#     }
#
#     response = requests.post(TTS_URL, headers=HEADERS, json=data).json()
#     print("TTS response:", response)  # 🔍 打印返回
#     audio_base64 = response.get("data")
#     if audio_base64:
#         audio_bytes = base64.b64decode(audio_base64)
#         if out_file is None:
#             out_file = f"output_{int(time.time())}_{uuid.uuid4().hex[:8]}.mp3"
#         with open(out_file, "wb") as f:
#             f.write(audio_bytes)
#         playsound(out_file)
#         try:
#             os.remove(out_file)
#         except:
#             pass
#     else:
#         print("TTS 没有返回音频数据")


def tts_and_play(text, role_key=None, out_file=None):
    voice_type = get_role_voice(role_key)
    print(f"使用音色: {voice_type}")

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {API_KEY}"
    }
    data = {
        "audio": {
            "voice_type": voice_type,
            "encoding": "mp3",
            "speed_ratio": 1.0
        },
        "request": {"text": text}
    }

    response = requests.post(TTS_URL, headers=headers, json=data).json()
    audio_base64 = response.get("data")

    if not audio_base64:
        print("返回结果没有 audio 数据:", response)
        return None

    audio_bytes = base64.b64decode(audio_base64)

    # 统一保存目录
    AUDIO_DIR = "audio_outputs"
    os.makedirs(AUDIO_DIR, exist_ok=True)

    if out_file is None:
        unique_id = f"{int(time.time())}_{uuid.uuid4().hex[:8]}"
        out_file = os.path.join(AUDIO_DIR, f"output_{unique_id}.mp3")
    else:
        out_file = os.path.join(AUDIO_DIR, out_file)

    with open(out_file, "wb") as f:
        f.write(audio_bytes)

    return out_file
# def tts_and_play(text,role_key=None, out_file=None):
#     voice_type = get_role_voice(role_key)
#     print(voice_type)
#     headers = {
#         "Content-Type": "application/json",
#         "Authorization": f"Bearer {API_KEY}"
#     }
#     data = {
#         "audio": {
#             "voice_type": voice_type,
#             "encoding": "mp3",
#             "speed_ratio": 1.0
#         },
#         "request": {"text": text}
#     }
#
#     response = requests.post(TTS_URL, headers=headers, json=data).json()
#     audio_base64 = response.get("data")
#
#     if audio_base64:
#         audio_bytes = base64.b64decode(audio_base64)
#
#         if out_file is None:
#             unique_id = f"{int(time.time())}_{uuid.uuid4().hex[:8]}"
#             out_file = f"output_{unique_id}.mp3"
#
#         with open(out_file, "wb") as f:
#             f.write(audio_bytes)
#
#         print(f"🔊 播放语音: {out_file}")
#         playsound(out_file)
#
#         try:
#             os.remove(out_file)
#         except Exception as e:
#             print("清理临时文件失败:", e)
#     else:
#         print("返回结果没有 audio 数据:", response)
