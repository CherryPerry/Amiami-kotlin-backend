#!/bin/bash

./gradlew jar
rm /var/amiami/app.jar
mkdir /var/amiami
mv build/libs/amiami-backend-1.0.jar /var/amiami/app.jar
rm /etc/systemd/system/amiami.service
mv service.systemd /etc/systemd/system/amiami.service
systemctl enable amiami.service
systemctl daemon-reload
systemctl restart amiami.service