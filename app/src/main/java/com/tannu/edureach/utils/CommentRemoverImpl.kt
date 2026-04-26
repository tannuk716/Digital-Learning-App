package com.tannu.edureach.utils

class CommentRemoverImpl : CommentRemover {
    
    override fun removeComments(tokens: List<Token>): String {
        val result = StringBuilder()
        
        for (token in tokens) {
            when (token.type) {
                TokenType.CODE,
                TokenType.STRING_LITERAL,
                TokenType.RAW_STRING_LITERAL -> {
                    result.append(token.content)
                }
                TokenType.SINGLE_LINE_COMMENT -> {
                    if (token.content.endsWith('\n')) {
                        result.append('\n')
                    }
                }
                TokenType.MULTI_LINE_COMMENT,
                TokenType.KDOC_COMMENT -> {
                }
            }
        }
        
        return result.toString()
    }
    
    override fun preserveWhitespace(original: String, cleaned: String): String {
        return cleaned
    }
}