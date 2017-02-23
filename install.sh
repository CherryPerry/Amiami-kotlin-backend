#!/bin/bash

./gradlew build
mkdir /var/amiami
chmod +x build/libs/amiami-backend-1.0.jar
cp build/libs/amiami-backend-1.0.jar /var/amiami/app.jar
cp service.systemd /etc/systemd/system/amiami.service
systemctl enable amiami.service
systemctl daemon-reload
systemctl restart amiami.service