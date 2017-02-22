#!/bin/bash

./gradlew build
rm /var/amiami/app.jar
chmod +x build/libs/amiami-backend-1.0.jar
mv build/libs/amiami-backend-1.0.jar /var/amiami/app.jar
systemctl daemon-reload
systemctl restart amiami.service