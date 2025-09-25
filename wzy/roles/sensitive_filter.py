# -*- coding: utf-8 -*-
# @Time    : 2025/9/24 23:07
# @Author  : yeye
# @File    : sensitive_filter.py
# @Software: PyCharm
# @Desc    :
# sensitive_filter.py
import os

# 从文件加载敏感词（每行一个）
def load_sensitive_words(path="sensitive_words.txt"):
    words = set()
    if not os.path.exists(path):
        return words
    with open(path, "r", encoding="utf-8") as f:
        for line in f:
            w = line.strip()
            if w:
                words.add(w.lower())
    return words

SENSITIVE_WORDS = load_sensitive_words()

def find_sensitive(text: str):
    """返回匹配到的敏感词列表（小写匹配）"""
    if not text:
        return []
    t = text.lower()
    hits = [w for w in SENSITIVE_WORDS if w in t]
    return hits

def is_sensitive(text: str):
    return len(find_sensitive(text)) > 0
