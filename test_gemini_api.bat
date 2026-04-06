@echo off
setlocal enabledelayedexpansion

set API_KEY=AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM

echo Testing Gemini API with your key...
echo.

REM Test v1beta/models/gemini-pro:generateContent
echo Testing: v1beta/models/gemini-pro:generateContent
curl -s "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=%API_KEY%" -H "Content-Type: application/json" -d "{\"contents\":[{\"parts\":[{\"text\":\"Say hello\"}]}]}"
echo.
echo.

REM Test v1beta/models/gemini-1.5-flash:generateContent
echo Testing: v1beta/models/gemini-1.5-flash:generateContent
curl -s "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=%API_KEY%" -H "Content-Type: application/json" -d "{\"contents\":[{\"parts\":[{\"text\":\"Say hello\"}]}]}"
echo.
echo.

echo If you see a response with "candidates" and "text", the API key works!
pause
