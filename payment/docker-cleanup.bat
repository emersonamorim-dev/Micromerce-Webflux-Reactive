@echo off
echo Stopping all containers...
docker-compose down

echo Removing all containers...
for /f "tokens=*" %%i in ('docker ps -aq') do docker rm -f %%i

echo Removing all volumes...
for /f "tokens=*" %%i in ('docker volume ls -q') do docker volume rm %%i

echo Creating necessary directories...
if not exist "docker\elasticsearch\data" mkdir "docker\elasticsearch\data"
if not exist "docker\mysql" mkdir "docker\mysql"

echo Starting services...
docker-compose up -d
