@echo off
echo Stopping and removing containers...
docker compose down -v

echo Removing Kafka and ZooKeeper volumes...
docker volume rm kafka-data-v1 zookeeper_data zookeeper_log 2>nul

echo Removing all unused volumes...
docker volume prune -f

echo Starting services...
docker compose up -d

echo Cleanup complete!
