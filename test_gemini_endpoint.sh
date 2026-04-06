#!/bin/bash

# Test script to verify which Gemini endpoint works
API_KEY="AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM"

echo "=========================================="
echo "Testing Gemini API Endpoints"
echo "=========================================="
echo ""

# Test data
TEST_PROMPT="What is 2+2?"
REQUEST_BODY='{
  "contents": [{
    "parts": [{
      "text": "'"$TEST_PROMPT"'"
    }]
  }],
  "generationConfig": {
    "temperature": 0.7,
    "maxOutputTokens": 1024
  }
}'

# Test 1: v1/models/gemini-pro (CURRENT)
echo "Test 1: v1/models/gemini-pro"
echo "Endpoint: https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent"
echo ""
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST \
  "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key=$API_KEY" \
  -H "Content-Type: application/json" \
  -d "$REQUEST_BODY")

HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "HTTP Status: $HTTP_CODE"
if [ "$HTTP_CODE" = "200" ]; then
  echo "✓ SUCCESS!"
  echo "$BODY" | jq -r '.candidates[0].content.parts[0].text' 2>/dev/null || echo "$BODY"
else
  echo "✗ FAILED"
  echo "$BODY" | jq '.' 2>/dev/null || echo "$BODY"
fi
echo ""
echo "=========================================="
echo ""

# Test 2: v1beta/models/gemini-pro
echo "Test 2: v1beta/models/gemini-pro"
echo "Endpoint: https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"
echo ""
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST \
  "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=$API_KEY" \
  -H "Content-Type: application/json" \
  -d "$REQUEST_BODY")

HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "HTTP Status: $HTTP_CODE"
if [ "$HTTP_CODE" = "200" ]; then
  echo "✓ SUCCESS!"
  echo "$BODY" | jq -r '.candidates[0].content.parts[0].text' 2>/dev/null || echo "$BODY"
else
  echo "✗ FAILED"
  echo "$BODY" | jq '.' 2>/dev/null || echo "$BODY"
fi
echo ""
echo "=========================================="
echo ""

# Test 3: v1beta/models/gemini-1.5-flash
echo "Test 3: v1beta/models/gemini-1.5-flash"
echo "Endpoint: https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
echo ""
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST \
  "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$API_KEY" \
  -H "Content-Type: application/json" \
  -d "$REQUEST_BODY")

HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "HTTP Status: $HTTP_CODE"
if [ "$HTTP_CODE" = "200" ]; then
  echo "✓ SUCCESS!"
  echo "$BODY" | jq -r '.candidates[0].content.parts[0].text' 2>/dev/null || echo "$BODY"
else
  echo "✗ FAILED"
  echo "$BODY" | jq '.' 2>/dev/null || echo "$BODY"
fi
echo ""
echo "=========================================="
echo ""

# Test 4: v1beta/models/gemini-1.5-flash-latest
echo "Test 4: v1beta/models/gemini-1.5-flash-latest"
echo "Endpoint: https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent"
echo ""
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST \
  "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=$API_KEY" \
  -H "Content-Type: application/json" \
  -d "$REQUEST_BODY")

HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "HTTP Status: $HTTP_CODE"
if [ "$HTTP_CODE" = "200" ]; then
  echo "✓ SUCCESS!"
  echo "$BODY" | jq -r '.candidates[0].content.parts[0].text' 2>/dev/null || echo "$BODY"
else
  echo "✗ FAILED"
  echo "$BODY" | jq '.' 2>/dev/null || echo "$BODY"
fi
echo ""
echo "=========================================="
echo ""

echo "Testing complete!"
echo ""
echo "Summary:"
echo "- If Test 1 succeeded, the app should work now (v1/gemini-pro)"
echo "- If Test 1 failed but others succeeded, update GeminiApiService.kt to use that endpoint"
echo "- If all tests failed, check API key permissions in Google Cloud Console"
