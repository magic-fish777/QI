# -*- coding: utf-8 -*-
# @Time    : 2025/9/24 23:37
# @Author  : yeye
# @File    : get_list.py
# @Software: PyCharm
# @Desc    :
import requests

# ==============================
# 配置部分
# ==============================
API_KEY = "sk-4c5b56e51b13c3b113520509555bbce1cf58835687ab23b3a99ffaef478c2bc7"
BASE_URL = "https://openai.qiniu.com/v1"  # 主要接入点

# ==============================
# 获取音色列表
# ==============================
def get_voice_list():
    url = f"{BASE_URL}/voice/list"
    headers = {
        "Authorization": f"Bearer {API_KEY}"
    }

    response = requests.get(url, headers=headers)

    if response.status_code == 200:
        voices = response.json()  # 直接是列表，不是字典
        print("可用音色列表：")
        for v in voices:
            name = v.get("voice_name", "未知")
            vtype = v.get("voice_type", "未知")
            print(f"音色名称：{name}，类型：{vtype}")
        return voices
    else:
        print(f"请求失败，状态码：{response.status_code}, 信息：{response.text}")
        return []

if __name__ == "__main__":
    voice_list = get_voice_list()