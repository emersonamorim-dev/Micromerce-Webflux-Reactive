#!/bin/bash

echo "Stopping all containers..."
docker-compose down

echo "Removing all containers..."
docker rm -f $(docker ps -a -q) 2>/dev/null || true

echo "Removing all volumes..."
docker volume rm $(docker volume ls -q) 2>/dev/null || true

echo "Creating necessary directories..."
mkdir -p docker/elasticsearch/data
mkdir -p docker/mysql

echo "Setting permissions..."
chmod -R 777 docker/elasticsearch/data
chmod -R 777 docker/mysql

echo "Starting services..."
docker-compose up -d
