@echo off
echo Cleaning up MySQL data...

:: Apagar os dados antigos do volume MySQL (se estiverem armazenados em um diretório local)
rmdir /s /q "docker\mysql"
mkdir "docker\mysql"

echo Recreating containers...
docker-compose down

:: Subir os contêineres novamente, incluindo o MySQL
docker-compose up -d

echo Cleanup complete!
