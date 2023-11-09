#!/bin/bash
docker images --filter "dangling=true" -q | xargs docker rmi
sudo docker-compose down
sudo docker stop xoso-dna-platform_admin_1
sudo docker stop xoso-dna-platform_api_1
sudo docker stop xoso-dna-platform_schedule_1
sudo docker rm xoso-dna-platform_admin_1
sudo docker rm xoso-dna-platform_api_1
sudo docker rm xoso-dna-platform_schedule_1
mvn clean install
sudo docker-compose build admin
sudo docker-compose build api
sudo docker-compose build schedule
sudo docker-compose up -d
