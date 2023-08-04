#To remove all containers
docker rm -f $(docker ps -a -q)
sleep 2

#To remove network
docker network rm poc_book_network
sleep 2

#To remove volumes
docker volume rm $(docker volume ls -q)
sleep 2

docker rmi book_service_app
