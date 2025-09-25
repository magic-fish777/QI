# -*- coding: utf-8 -*-
# @Time    : 2025/9/24 23:00
# @Author  : yeye
# @File    : recorder.py
# @Software: PyCharm
# @Desc    :
import sounddevice as sd
import soundfile as sf
import time, uuid

def record_audio(duration=5, samplerate=16000):
    print("🎙️ 开始录音...")
    unique_id = f"{int(time.time())}_{uuid.uuid4().hex[:8]}"
    filename = f"input_{unique_id}.wav"

    recording = sd.rec(int(duration * samplerate), samplerate=samplerate, channels=1, dtype="int16")
    sd.wait()
    sf.write(filename, recording, samplerate)
    print(f"录音完成: {filename}")
    return filename
