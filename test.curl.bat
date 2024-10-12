@echo off

REM --------------------------------------------------
REM Monster Trading Cards Game
REM --------------------------------------------------
title Monster Trading Cards Game
echo CURL Testing for Monster Trading Cards Game
echo Syntax: $1 [pause]
echo - pause: optional, if set, the script will pause after each block
echo.

set "pauseFlag=0"
for %%a in (%*) do (
    if /I "%%a"=="pause" (
        set "pauseFlag=1"
    )
)

if %pauseFlag%==1 pause

REM --------------------------------------------------
echo 1) Create Users (Registration)
REM Create User
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
echo "Should return HTTP 201"
echo.
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"altenhof\", \"Password\":\"markus\"}"
echo "Should return HTTP 201"
echo.
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"admin\",    \"Password\":\"istrator\"}"
echo "Should return HTTP 201"
echo.

if %pauseFlag%==1 pause

echo should fail:
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
echo "Should return HTTP 4xx - User already exists"
echo.
curl -i -X POST http://localhost:10001/users --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"different\"}"
echo "Should return HTTP 4xx - User already exists"
echo. 
echo.

if %pauseFlag%==1 pause

REM --------------------------------------------------
echo 2) Login Users
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
echo "should return HTTP 200 with generated token for the user, here: kienboec-mtcgToken"
echo.
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"altenhof\", \"Password\":\"markus\"}"
echo "should return HTTP 200 with generated token for the user, here: altenhof-mtcgToken"
echo.
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"admin\",    \"Password\":\"istrator\"}"
echo "should return HTTP 200 with generated token for the user, here: admin-mtcgToken"
echo.

if %pauseFlag%==1 pause

echo should fail:
curl -i -X POST http://localhost:10001/sessions --header "Content-Type: application/json" -d "{\"Username\":\"kienboec\", \"Password\":\"different\"}"
echo "Should return HTTP 4xx - Login failed"
echo.
echo.

if %pauseFlag%==1 pause

curl -i -X POST http://localhost:10001/packages --header "Content-Type: application/json" -d "{\"cards\": [{\"name\": \"WaterSpell\", \"elementType\": \"Water\", \"damage\": 20}, {\"name\": \"FireSpell\", \"elementType\": \"Fire\", \"damage\": 25}, {\"name\": \"Goblin\", \"elementType\": \"Earth\", \"damage\": 15}, {\"name\": \"Knight\", \"elementType\": \"Normal\", \"damage\": 30}, {\"name\": \"Dragon\", \"elementType\": \"Fire\", \"damage\": 50}]}"
echo "should return HTTP 201 with the package id"
echo.
curl -i -X POST http://localhost:10001/transactions/packages --header "Authorization: kienboec-mtcgToken"
echo "should return HTTP 200 with the package content"
echo.