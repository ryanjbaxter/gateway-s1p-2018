#!/usr/bin/env bash
cf cs p-service-registry trial bluegreen-registry

# Wait until services are ready
while cf services | grep 'create in progress'
do
  sleep 20
  echo "Waiting for services to initialize..."
done

# Check to see if any services failed to create
if cf services | grep 'create failed'; then
  echo "Service initialization - failed. Exiting."
  return 1
fi
echo "Service initialization - successful"

cd blueorgreenservice
./mvnw clean package
cf push
cd ../
cd blueorgreenfrontend
./mvnw clean package
cf push
cd ../
cd blueorgreengateway
./mvnw clean package
cf push
cd ../

#Add network policies
cf add-network-policy blueorgreengateway --destination-app greenservice --protocol tcp --port 8080
cf add-network-policy blueorgreengateway --destination-app blueservice --protocol tcp --port 8080
cf add-network-policy blueorgreengateway --destination-app yellowservice --protocol tcp --port 8080
cf add-network-policy blueorgreengateway --destination-app blueorgreenfrontend --protocol tcp --port 8080
