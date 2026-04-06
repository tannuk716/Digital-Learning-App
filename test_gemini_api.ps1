# Test Gemini API with the configured API key
# This verifies the API key works before building the app

$API_KEY = "AIzaSyBv6PBn1CgB6_kbqK38tf8DrpT3cR1kotc"
$MODEL = "gemini-flash-latest"
$ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL`:generateContent?key=$API_KEY"

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Testing Gemini API Configuration" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "API Key: $($API_KEY.Substring(0,10))...$($API_KEY.Substring($API_KEY.Length-5))"
Write-Host "Model: $MODEL"
Write-Host "Endpoint: $ENDPOINT"
Write-Host ""
Write-Host "Sending test request..." -ForegroundColor Yellow
Write-Host ""

# Test request body
$body = @{
    contents = @(
        @{
            parts = @(
                @{
                    text = "What is 2+2? Answer in one sentence."
                }
            )
        }
    )
    generationConfig = @{
        temperature = 0.7
        maxOutputTokens = 100
    }
} | ConvertTo-Json -Depth 10

try {
    $response = Invoke-WebRequest -Uri $ENDPOINT `
        -Method POST `
        -ContentType "application/json" `
        -Body $body `
        -UseBasicParsing

    Write-Host "=========================================" -ForegroundColor Cyan
    Write-Host "Response:" -ForegroundColor Cyan
    Write-Host "=========================================" -ForegroundColor Cyan
    Write-Host "HTTP Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host ""
    
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ SUCCESS! API key is valid and working." -ForegroundColor Green
        Write-Host ""
        Write-Host "Response body:"
        $jsonResponse = $response.Content | ConvertFrom-Json
        $jsonResponse | ConvertTo-Json -Depth 10
        Write-Host ""
        Write-Host "=========================================" -ForegroundColor Green
        Write-Host "✅ Your Gemini API is ready for production!" -ForegroundColor Green
        Write-Host "=========================================" -ForegroundColor Green
        exit 0
    }
} catch {
    Write-Host "=========================================" -ForegroundColor Red
    Write-Host "Response:" -ForegroundColor Red
    Write-Host "=========================================" -ForegroundColor Red
    Write-Host "HTTP Status: $($_.Exception.Response.StatusCode.Value__)" -ForegroundColor Red
    Write-Host ""
    Write-Host "❌ ERROR! API request failed." -ForegroundColor Red
    Write-Host ""
    Write-Host "Error details:"
    Write-Host $_.Exception.Message
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host ""
        Write-Host "Response body:"
        Write-Host $responseBody
    }
    
    Write-Host ""
    Write-Host "=========================================" -ForegroundColor Red
    Write-Host "❌ Please check your API key configuration" -ForegroundColor Red
    Write-Host "=========================================" -ForegroundColor Red
    exit 1
}
