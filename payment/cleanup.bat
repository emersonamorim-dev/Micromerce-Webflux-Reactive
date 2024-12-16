@echo off
echo Limpando ambiente Docker...

docker-compose down -v
docker system prune -f

echo Removendo volumes...
docker volume prune -f

echo Removendo containers parados...
docker rm -f $(docker ps -aq) 2>nul

echo Ambiente limpo com sucesso!
