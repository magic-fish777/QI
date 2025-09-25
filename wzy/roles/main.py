# -*- coding: utf-8 -*-
# @Time    : 2025/9/24 23:02
# @Author  : yeye
# @File    : main.py
# @Software: PyCharm
# @Desc    :
import os
from recorder import record_audio
from uploader import upload_to_qiniu
from asr import asr_from_url
from llm import call_llm
from tts import tts_and_play
from prompt import system_prompt
from sensitive_filter import is_sensitive, find_sensitive
if __name__ == "__main__":
    session_history = [{"role": "system", "content": system_prompt}]

    while True:
        wav_path = record_audio(duration=5)
        mp3_path = wav_path.replace(".wav", ".mp3")
        os.rename(wav_path, mp3_path)

        audio_url = upload_to_qiniu(mp3_path)
        user_text = asr_from_url(audio_url)

        if not user_text.strip():
            print("未识别到语音")
            continue
        if user_text in ["退出", "exit", "quit"]:
            print("再见！")
            break

        if is_sensitive(user_text):
            bad = find_sensitive(user_text)
            print(f"检测到敏感词 {bad}，已拒绝响应")
            # 你可以选择给一个默认回复或直接 continue
            tts_and_play("抱歉，我无法回答该内容。")
            continue

        # ====== Step1: 构建带角色信息的 user 文本 ======
        from role_manager import build_user_message_for_llm

        # 如果你想动态切换角色，可从会话或命令中解析 role_key
        role_key = None  # 保持默认（harry）
        # 把用户文本和简单 meta 拼接后交给 call_llm
        extra_meta = {}  # 后续会加入 emotion/intent/language
        user_for_llm = build_user_message_for_llm(user_text, role_key=role_key, extra_meta=extra_meta)

        # 调用 LLM（不改原有 call_llm）
        reply = call_llm(user_for_llm, session_history)

        tts_and_play(reply)
