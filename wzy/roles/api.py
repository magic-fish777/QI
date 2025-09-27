# -*- coding: utf-8 -*-
# @Time    : 2025/9/24 23:19
# @Author  : yeye
# @File    : api.py
# @Software: PyCharm
# @Desc    : FastAPI 两接口示例

from fastapi import FastAPI, UploadFile, Form
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import Optional
import os
from uploader import upload_to_qiniu
from asr import asr_from_url
from llm import call_llm
from tts import tts_and_play
from prompt import system_prompt
from sensitive_filter import is_sensitive, find_sensitive
from role_manager import build_user_message_for_llm,get_system_prompt,ROLE_PROFILES
from spell_knowledge import run_role_skill

app = FastAPI(title="语音/文本聊天接口")

session_history = [{"role": "system", "content": system_prompt}]
AVAILABLE_ROLES = ["harry", "liuxiaoyan", "yiyangqianxi"]



# ------------------ 文本请求模型 ------------------
class ChatRequest(BaseModel):
    text: str
    role: str = "harry"

CHAT_HISTORIES = {}
# ---------------- 全局技能状态 ----------------
# 用于记录各角色当前技能模式和轮次
ROLE_SKILL_STATE = {}

@app.post("/chat/text")
async def chat_text(data: ChatRequest):
    user_text = data.text
    role = data.role if data.role in AVAILABLE_ROLES else "harry"

    if not user_text.strip():
        return JSONResponse(content={"reply": "未识别到有效内容"}, status_code=400)

    if is_sensitive(user_text):
        bad = find_sensitive(user_text)
        audio_file = tts_and_play("抱歉，我无法回答该内容。", role_key=role)
        return {"reply": f"检测到敏感词 {bad}，已拒绝响应", "audio_file": audio_file}
    print("user_text: ", user_text)
    # 尝试技能处理
    skill_reply = run_role_skill(user_text, role)
    print("reply: ", skill_reply)
    if skill_reply:
        audio_file = tts_and_play(skill_reply, role_key=role)
        return {"role": role, "user": user_text, "reply": skill_reply, "audio_file": audio_file}

    # 普通对话
    if role not in CHAT_HISTORIES:
        CHAT_HISTORIES[role] = [{"role": "system", "content": ROLE_PROFILES[role]["system"]}]

    CHAT_HISTORIES[role].append({"role": "user", "content": user_text})
    reply = call_llm(user_text, CHAT_HISTORIES[role])
    CHAT_HISTORIES[role].append({"role": "assistant", "content": reply})
    audio_file = tts_and_play(reply, role_key=role)

    return {"role": role, "user": user_text, "reply": reply, "audio_file": audio_file}






# ------------------ 音频接口 ------------------
@app.post("/chat/audio")
async def chat_audio(
    audio: UploadFile,
    role: Optional[str] = Form("harry")
):
    if not audio:
        return JSONResponse(content={"error": "必须上传音频"}, status_code=400)

    role = role if role in AVAILABLE_ROLES else "harry"

    # 保存音频
    temp_wav = f"temp_{audio.filename}"
    with open(temp_wav, "wb") as f:
        f.write(await audio.read())

    # 转 mp3
    mp3_path = temp_wav.replace(".wav", ".mp3")
    os.rename(temp_wav, mp3_path)

    # 上传并识别
    audio_url = upload_to_qiniu(mp3_path)
    user_text = asr_from_url(audio_url)

    if not user_text.strip():
        return JSONResponse(content={"reply": "未识别到有效内容"}, status_code=400)

    if is_sensitive(user_text):
        bad = find_sensitive(user_text)
        tts_and_play("抱歉，我无法回答该内容。")
        return {"reply": f"检测到敏感词 {bad}，已拒绝响应"}

    # 初始化角色对话历史
    if role not in CHAT_HISTORIES:
        CHAT_HISTORIES[role] = [{"role": "system", "content": get_system_prompt(role)}]

    # 用户消息加入历史
    CHAT_HISTORIES[role].append({"role": "user", "content": user_text})

    # 调用 LLM
    reply = call_llm(user_text, CHAT_HISTORIES[role])

    # 助手回复加入历史
    CHAT_HISTORIES[role].append({"role": "assistant", "content": reply})

    # TTS 生成音频
    audio_file = tts_and_play(reply, role_key=role)

    # 如果要直接播放，可以额外调用 playsound(audio_file)

    return {
        "role": role,
        "user": user_text,
        "reply": reply,
        "audio_file": audio_file  # 新增返回字段
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("api:app", host="0.0.0.0", port=8000, reload=True)
