#!/bin/bash

echo "Creating Postgres container..."
docker run --name postgresql  -e 'ACCEPT_EULA=Y' -e POSTGRES_PASSWORD=D1ngCr0n -p 5432:5432 -d postgres
sleep 10

echo "Building POC APP image..."
cd poc-book-service
mvn clean package
docker image build -t book_service_app .
cd ../
sleep 10

echo "Creating ActiveMQ container..."
docker run -d --name activemq -p 61616:61616 -p 8161:8161 webcenter/activemq
sleep 10

echo "Creating POC_BOOK_NETWORK..."
docker network create poc_book_network
docker network connect poc_book_network postgresql
docker network connect poc_book_network activemq
sleep 10

echo "Creating POC Initial Data DB..."
docker cp create_poc_db.sql postgresql:/
docker exec -it postgresql psql -U postgres -f /create_poc_db.sql
sleep 10

echo  "Creating Book POC APP container..."
docker container run -e "SPRING_PROFILES_ACTIVE=dev" -p 8080:8080 -p 8000:8000 --detach --name book_service_app book_service_app
sleep 10

docker network connect poc_book_network book_service_app
sleep 10

echo "All is complete, enjoy!..."
