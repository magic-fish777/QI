import requests
import base64
import sounddevice as sd
import soundfile as sf
import tempfile
import os
import qiniu
import uuid
import time
from playsound import playsound
# ==== API 配置 ====
API_KEY = "sk-4c5b56e51b13c3b113520509555bbce1cf58835687ab23b3a99ffaef478c2bc7"
ASR_URL = "https://openai.qiniu.com/v1/voice/asr"
TTS_URL = "https://openai.qiniu.com/v1/voice/tts"
LLM_URL = "https://openai.qiniu.com/v1/chat/completions"

# ==== 七牛云 OSS 配置 ====
ACCESS_KEY = "v3t0o82hzcjoZZXsphgQisB_tJL241CUjqInKBLn"
SECRET_KEY = "x4O8IzIhYXubI_2sFUtWLfmPiGLmX17flq4pY_tm"
BUCKET_NAME = "mp3-wav-data"
DOMAIN = "http://t3128eyqh.hd-bkt.clouddn.com"  # 你的bucket绑定的测试域名
HEADERS = {
    "Authorization": f"Bearer {API_KEY}",
    "Content-Type": "application/json"
}
# ==== 录音函数 ====
# def record_audio(filename="input.wav", duration=5, samplerate=16000):
#     print("🎙️ 开始录音...")
#     recording = sd.rec(int(duration * samplerate), samplerate=samplerate, channels=1, dtype="int16")
#     sd.wait()
#     sf.write(filename, recording, samplerate)
#     print(f"录音完成: {filename}")
#     return filename
# ==== 录音函数（随机文件名）====
def record_audio(duration=5, samplerate=16000):
    print("🎙️ 开始录音...")

    # 用 uuid + 时间戳生成唯一文件名
    unique_id = f"{int(time.time())}_{uuid.uuid4().hex[:8]}"
    filename = f"input_{unique_id}.wav"

    recording = sd.rec(int(duration * samplerate), samplerate=samplerate, channels=1, dtype="int16")
    sd.wait()
    sf.write(filename, recording, samplerate)
    print(f"录音完成: {filename}")
    return filename


# ==== 上传到七牛云 ====
def upload_to_qiniu(localfile, key=None):
    q = qiniu.Auth(ACCESS_KEY, SECRET_KEY)
    token = q.upload_token(BUCKET_NAME, key, 3600)

    if key is None:
        key = os.path.basename(localfile)

    ret, info = qiniu.put_file(token, key, localfile)
    # print(info.status_code)
    if info.status_code == 200:
        file_url = f"{DOMAIN}/{key}"
        print(f"上传成功: {file_url}")
        print(file_url)
        return file_url
    else:
        print("上传失败:", info)
        return None


# ==== ASR (通过 URL) ====
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


# ==== 调用大模型 ====
def call_llm(user_text, session_history):
    session_history.append({"role": "user", "content": user_text})
    payload = {
        "model": "gpt-oss-120b",
        "stream": False,
        "messages": session_history
    }
    resp = requests.post(LLM_URL, headers=HEADERS, json=payload, timeout=30).json()
    reply = resp["choices"][0]["message"]["content"]
    session_history.append({"role": "assistant", "content": reply})
    print("🤖 LLM 回复:", reply)
    return reply

def tts_and_play(text, out_file=None):
    url = "https://openai.qiniu.com/v1/voice/tts"
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {API_KEY}"
    }
    data = {
        "audio": {
            "voice_type": "qiniu_zh_female_wwxkjx",
            "encoding": "mp3",
            "speed_ratio": 1.0
        },
        "request": {"text": text}
    }

    response = requests.post(url, headers=headers, json=data).json()
    audio_base64 = response.get("data")

    if audio_base64:
        audio_bytes = base64.b64decode(audio_base64)

        # 🔑 自动生成唯一文件名
        if out_file is None:
            unique_id = f"{int(time.time())}_{uuid.uuid4().hex[:8]}"
            out_file = f"output_{unique_id}.mp3"

        with open(out_file, "wb") as f:
            f.write(audio_bytes)

        print(f"🔊 播放语音: {out_file}")
        playsound(out_file)

        # ✅ 播放后删除临时文件，避免堆积
        try:
            os.remove(out_file)
        except Exception as e:
            print("清理临时文件失败:", e)

    else:
        print("返回结果没有 audio 数据:", response)

# ==== 哈利·波特角色 system prompt ====
system_prompt = """
你是角色“哈利·波特风格”。请严格扮演角色。
行为规范：
1. 保持哈利的语气：友善、勇敢、略带幽默。
2. 在用户施咒时，用魔法效果描述 + 角色反应。
3. 如果用户输入以“讲故事”开头，请讲短故事或回忆霍格沃茨经历。
4. 对用户的问题尽量给出具体、贴近角色的回答。
5. 避免破坏角色设定（不要谈现实世界或 AI）。
6. 保持对话上下文记忆，不重复提问。
7. 如果用户问题涉及故事外内容，可以巧妙引导回角色世界。
8. 尽量丰富描述，加入动作、环境和情感细节。
9. 每次回答尽量控制在 3-5 句话内，如果是施咒或故事，可略微多一点，但不要超过 200 字。
10. 保持对话风格轻松、自然，而不是写小说章节。
"""
# ==== 主程序 ====
if __name__ == "__main__":
    session_history = [{"role": "system", "content": system_prompt}]

    while True:
        # 录音保存本地 wav
        wav_path = record_audio(duration=5)

        # 转 mp3（简单起见直接重命名）
        mp3_path = wav_path.replace(".wav", ".mp3")
        os.rename(wav_path, mp3_path)

        # 上传到 OSS
        audio_url = upload_to_qiniu(mp3_path)

        # 识别
        user_text = asr_from_url(audio_url)
        if not user_text.strip():
            print("未识别到语音")
            continue
        if user_text in ["退出", "exit", "quit"]:
            print("再见！")
            break

        # LLM
        reply = call_llm(user_text, session_history)

        # TTS + 播放
        tts_and_play(reply)
