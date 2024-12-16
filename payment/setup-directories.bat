@echo off
echo Creating data directories...

mkdir "docker\kafka\data" 2>nul
mkdir "docker\zookeeper\data" 2>nul
mkdir "docker\zookeeper\log" 2>nul
mkdir "docker\mysql" 2>nul
mkdir "docker\redis\data" 2>nul

echo Data directories created successfully!

echo Cleaning up Docker...
docker-compose down -v
docker system prune -f

echo Starting services...
docker-compose up -d

echo Waiting for services to be healthy...
timeout /t 30
