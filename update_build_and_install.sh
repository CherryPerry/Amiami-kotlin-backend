#!/bin/bash

./gradlew build
chmod +x build/libs/amiami-backend-1.0.jar
cp build/libs/amiami-backend-1.0.jar /var/amiami/app.jar
systemctl daemon-reload
systemctl restart amiami.service