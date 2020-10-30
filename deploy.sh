#!/bin/bash
./mvnw clean package && \
docker build -f src/main/docker/Dockerfile.jvm -t taaja/greeneagle . && \
docker save -o taaja-greeneagle.tar taaja/greeneagle && \
scp taaja-greeneagle.tar taaja@taaja.io:/home/taaja && \
ssh taaja@taaja.io 'docker load --quiet --input /home/taaja/taaja-greeneagle.tar && rm -f /home/taaja/taaja-greeneagle.tar && cd /home/taaja/deployment/taaja && docker-compose up -d'