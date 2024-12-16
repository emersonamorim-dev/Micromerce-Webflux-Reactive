@echo off
echo Stopping all containers...
docker-compose down -v

echo Cleaning up data directories...
rmdir /s /q "docker\kafka\data"
rmdir /s /q "docker\zookeeper\data"
rmdir /s /q "docker\zookeeper\log"
rmdir /s /q "docker\mysql"
rmdir /s /q "docker\elasticsearch\data"

echo Creating data directories...
mkdir "docker\kafka\data"
mkdir "docker\zookeeper\data"
mkdir "docker\zookeeper\log"
mkdir "docker\mysql"
mkdir "docker\elasticsearch\data"

echo Starting services...
docker-compose up -d

echo Environment reset complete! Waiting for services to be healthy...
timeout /t 30
