package com.qiqiplay.ai.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qiqiplay.common.annotation.Excel;
import com.qiqiplay.common.core.domain.BaseEntity;

/**
 * 敏感词库对象 sys_sensitive_word
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public class SysSensitiveWord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 敏感词ID */
    private Long wordId;

    /** 敏感词内容 */
    @Excel(name = "敏感词内容")
    private String wordContent;

    /** 分类（1政治 2色情 3暴力） */
    @Excel(name = "分类", readConverterExp = "1=政治,2=色情,3=暴力")
    private String wordCategory;

    public void setWordId(Long wordId) 
    {
        this.wordId = wordId;
    }

    public Long getWordId() 
    {
        return wordId;
    }

    public void setWordContent(String wordContent) 
    {
        this.wordContent = wordContent;
    }

    public String getWordContent() 
    {
        return wordContent;
    }

    public void setWordCategory(String wordCategory) 
    {
        this.wordCategory = wordCategory;
    }

    public String getWordCategory() 
    {
        return wordCategory;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("wordId", getWordId())
            .append("wordContent", getWordContent())
            .append("wordCategory", getWordCategory())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
