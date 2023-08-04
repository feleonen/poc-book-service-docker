#!/bin/bash

#stop container
docker stop book_service_app
sleep 10

#remove container
docker rm book_service_app
sleep 10

#remove image
docker rmi book_service_app
sleep 10

#enter service
cd poc-book-service

#build new jar
mvn clean package
sleep 10

#deploy new jar into docker
docker image build -t book_service_app .
sleep 10

#move onto docker folder
cd ..

#attach new image
docker container run -e "SPRING_PROFILES_ACTIVE=dev" -p 8080:8080 -p 8000:8000 --detach --name book_service_app book_service_app
sleep 10
