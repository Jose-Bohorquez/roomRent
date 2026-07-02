#!/bin/bash
# Crea el usuario de aplicación en MongoDB.
# Este script se ejecuta automáticamente la primera vez que el contenedor
# arranca con un volumen vacío. Las variables de entorno las inyecta Docker.
set -e

mongosh \
  --username "$MONGO_INITDB_ROOT_USERNAME" \
  --password "$MONGO_INITDB_ROOT_PASSWORD" \
  --authenticationDatabase admin \
  --quiet \
  --eval "
    db.getSiblingDB('room').createUser({
      user: '$MONGO_USERNAME',
      pwd:  '$MONGO_PASSWORD',
      roles: [{ role: 'readWrite', db: 'room' }]
    });
    print('Usuario de aplicación creado: ' + '$MONGO_USERNAME');
  "
