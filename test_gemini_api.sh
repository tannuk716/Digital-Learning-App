#!/bin/bash

# Test Gemini API with the configured API key
# This verifies the API key works before building the app

API_KEY="AIzaSyBv6PBn1CgB6_kbqK38tf8DrpT3cR1kotc"
MODEL="gemini-flash-latest"
ENDPOINT="https://generativelanguage.googleapis.com/v1beta/models/${MODEL}:generateContent?key=${API_KEY}"

echo "========================================="
echo "Testing Gemini API Configuration"
echo "========================================="
echo ""
echo "API Key: ${API_KEY:0:10}...${API_KEY: -5}"
echo "Model: $MODEL"
echo "Endpoint: $ENDPOINT"
echo ""
echo "Sending test request..."
echo ""

# Test request
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" \
  -H 'Content-Type: application/json' \
  -d '{
    "contents": [{
      "parts": [{
        "text": "What is 2+2? Answer in one sentence."
      }]
    }],
    "generationConfig": {
      "temperature": 0.7,
      "maxOutputTokens": 100
    }
  }' \
  "$ENDPOINT")

# Extract HTTP code
HTTP_CODE=$(echo "$RESPONSE" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed 's/HTTP_CODE:[0-9]*$//')

echo "========================================="
echo "Response:"
echo "========================================="
echo "HTTP Status: $HTTP_CODE"
echo ""

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ SUCCESS! API key is valid and working."
    echo ""
    echo "Response body:"
    echo "$BODY" | python3 -m json.tool 2>/dev/null || echo "$BODY"
    echo ""
    echo "========================================="
    echo "✅ Your Gemini API is ready for production!"
    echo "========================================="
    exit 0
else
    echo "❌ ERROR! API request failed."
    echo ""
    echo "Response body:"
    echo "$BODY"
    echo ""
    echo "========================================="
    echo "❌ Please check your API key configuration"
    echo "========================================="
    exit 1
fi
