# -*- coding: utf-8 -*-
# @Time    : 2025/9/24 23:01
# @Author  : yeye
# @File    : llm.py
# @Software: PyCharm
# @Desc    :
import requests
from config import LLM_URL, HEADERS

import re
def clean_text(text: str) -> str:
    text = re.sub(r"[\*\[\]]+", "", text)
    text = re.sub(r"\s+", " ", text)
    return text.strip()
def call_llm(user_text, session_history):
    session_history.append({"role": "user", "content": user_text})
    payload = {
        "model": "gpt-oss-120b",
        "stream": False,
        "messages": session_history
    }
    resp = requests.post(LLM_URL, headers=HEADERS, json=payload, timeout=30).json()
    reply = resp["choices"][0]["message"]["content"]
    reply = clean_text(reply)
    session_history.append({"role": "assistant", "content": reply})
    print("🤖 LLM 回复:", reply)
    return reply


def classify_text(text: str, task: str, choices=None, timeout=15):
    """
    使用 LLM 做分类任务：返回原始 LLM 文本（建议要求 JSON 格式返回）
    调用示例 task="情绪分类: 选项[positive, neutral, negative, sad]"
    """
    sys_prompt = "你是一个简短文本的分类器。请只用一句话或一个词回答（尽量给出标准选项），不要长篇输出。"
    user_prompt = f"任务描述: {task}\n文本: '''{text}'''\n请直接输出分类标签。"
    payload = {
        "model": "gpt-oss-120b",
        "stream": False,
        "messages": [
            {"role": "system", "content": sys_prompt},
            {"role": "user", "content": user_prompt}
        ]
    }
    try:
        resp = requests.post(LLM_URL, headers=HEADERS, json=payload, timeout=timeout)
        data = resp.json()
        label = data["choices"][0]["message"]["content"].strip()
        return label
    except Exception as e:
        # 请求失败则返回空字符串
        return ""
