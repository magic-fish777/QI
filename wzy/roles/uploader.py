# -*- coding: utf-8 -*-
# @Time    : 2025/9/24 23:00
# @Author  : yeye
# @File    : uploader.py
# @Software: PyCharm
# @Desc    :
import os
import qiniu
from config import ACCESS_KEY, SECRET_KEY, BUCKET_NAME, DOMAIN

def upload_to_qiniu(localfile, key=None):
    q = qiniu.Auth(ACCESS_KEY, SECRET_KEY)
    token = q.upload_token(BUCKET_NAME, key, 3600)

    if key is None:
        key = os.path.basename(localfile)

    ret, info = qiniu.put_file(token, key, localfile)
    if info.status_code == 200:
        file_url = f"{DOMAIN}/{key}"
        print(f"上传成功: {file_url}")
        return file_url
    else:
        print("上传失败:", info)
        return None
