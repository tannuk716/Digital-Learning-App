package com.tannu.edureach.utils

class CommentParserImpl : CommentParser {
    
    private enum class State {
        CODE,
        IN_STRING,
        IN_RAW_STRING,
        IN_SINGLE_LINE_COMMENT,
        IN_MULTI_LINE_COMMENT,
        IN_KDOC_COMMENT
    }
    
    override fun parse(source: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var state = State.CODE
        var currentTokenStart = 0
        var currentContent = StringBuilder()
        var i = 0
        
        while (i < source.length) {
            val char = source[i]
            val nextChar = if (i + 1 < source.length) source[i + 1] else null
            val nextNextChar = if (i + 2 < source.length) source[i + 2] else null
            
            when (state) {
                State.CODE -> {
                    when {
                        char == '"' && nextChar == '"' && nextNextChar == '"' -> {
                            if (currentContent.isNotEmpty()) {
                                tokens.add(Token(TokenType.CODE, currentContent.toString(), currentTokenStart, i))
                                currentContent.clear()
                            }
                            currentTokenStart = i
                            currentContent.append("\"\"\"")
                            state = State.IN_RAW_STRING
                            i += 3
                            continue
                        }
                        char == '"' -> {
                            if (currentContent.isNotEmpty()) {
                                tokens.add(Token(TokenType.CODE, currentContent.toString(), currentTokenStart, i))
                                currentContent.clear()
                            }
                            currentTokenStart = i
                            currentContent.append(char)
                            state = State.IN_STRING
                        }
                        char == '/' && nextChar == '*' && nextNextChar == '*' -> {
                            if (currentContent.isNotEmpty()) {
                                tokens.add(Token(TokenType.CODE, currentContent.toString(), currentTokenStart, i))
                                currentContent.clear()
                            }
                            currentTokenStart = i
                            currentContent.append("/**")
                            state = State.IN_KDOC_COMMENT
                            i += 3
                            continue
                        }
                        char == '/' && nextChar == '*' -> {
                            if (currentContent.isNotEmpty()) {
                                tokens.add(Token(TokenType.CODE, currentContent.toString(), currentTokenStart, i))
                                currentContent.clear()
                            }
                            currentTokenStart = i
                            currentContent.append("/*")
                            state = State.IN_MULTI_LINE_COMMENT
                            i += 2
                            continue
                        }
                        char == '/' && nextChar == '/' -> {
                            if (currentContent.isNotEmpty()) {
                                tokens.add(Token(TokenType.CODE, currentContent.toString(), currentTokenStart, i))
                                currentContent.clear()
                            }
                            currentTokenStart = i
                            currentContent.append("//")
                            state = State.IN_SINGLE_LINE_COMMENT
                            i += 2
                            continue
                        }
                        else -> {
                            currentContent.append(char)
                        }
                    }
                }
                State.IN_STRING -> {
                    currentContent.append(char)
                    if (char == '\\') {
                        if (nextChar != null) {
                            currentContent.append(nextChar)
                            i += 2
                            continue
                        }
                    } else if (char == '"') {
                        tokens.add(Token(TokenType.STRING_LITERAL, currentContent.toString(), currentTokenStart, i + 1))
                        currentContent.clear()
                        currentTokenStart = i + 1
                        state = State.CODE
                    }
                }
                State.IN_RAW_STRING -> {
                    currentContent.append(char)
                    if (char == '"' && nextChar == '"' && nextNextChar == '"') {
                        currentContent.append("\"\"")
                        tokens.add(Token(TokenType.RAW_STRING_LITERAL, currentContent.toString(), currentTokenStart, i + 3))
                        currentContent.clear()
                        currentTokenStart = i + 3
                        state = State.CODE
                        i += 3
                        continue
                    }
                }
                State.IN_SINGLE_LINE_COMMENT -> {
                    if (char == '\n' || char == '\r') {
                        tokens.add(Token(TokenType.SINGLE_LINE_COMMENT, currentContent.toString(), currentTokenStart, i))
                        currentContent.clear()
                        currentTokenStart = i
                        state = State.CODE
                        continue
                    } else {
                        currentContent.append(char)
                    }
                }
                State.IN_MULTI_LINE_COMMENT -> {
                    currentContent.append(char)
                    if (char == '*' && nextChar == '/') {
                        currentContent.append('/')
                        tokens.add(Token(TokenType.MULTI_LINE_COMMENT, currentContent.toString(), currentTokenStart, i + 2))
                        currentContent.clear()
                        currentTokenStart = i + 2
                        state = State.CODE
                        i += 2
                        continue
                    }
                }
                State.IN_KDOC_COMMENT -> {
                    currentContent.append(char)
                    if (char == '*' && nextChar == '/') {
                        currentContent.append('/')
                        tokens.add(Token(TokenType.KDOC_COMMENT, currentContent.toString(), currentTokenStart, i + 2))
                        currentContent.clear()
                        currentTokenStart = i + 2
                        state = State.CODE
                        i += 2
                        continue
                    }
                }
            }
            i++
        }
        
        if (currentContent.isNotEmpty()) {
            val type = when (state) {
                State.IN_SINGLE_LINE_COMMENT -> TokenType.SINGLE_LINE_COMMENT
                State.IN_MULTI_LINE_COMMENT -> TokenType.MULTI_LINE_COMMENT
                State.IN_KDOC_COMMENT -> TokenType.KDOC_COMMENT
                State.IN_STRING -> TokenType.STRING_LITERAL
                State.IN_RAW_STRING -> TokenType.RAW_STRING_LITERAL
                else -> TokenType.CODE
            }
            tokens.add(Token(type, currentContent.toString(), currentTokenStart, source.length))
        }
        
        return tokens
    }
}
