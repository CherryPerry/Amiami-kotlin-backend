#!/bin/bash

./gradlew build
chmod +x build/libs/amiami-backend-1.1.jar
cp build/libs/amiami-backend-1.1.jar /var/amiami/app.jar
systemctl daemon-reload
systemctl restart amiami.service