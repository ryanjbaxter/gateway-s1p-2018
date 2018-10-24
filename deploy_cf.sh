#!/usr/bin/env bash

# Get user input
echo "Build apps? [N]: "
read BUILD
BUILD=${BUILD:-N}

# Create services
cf cs p-service-registry trial color-registry

# Build apps
if [[ $BUILD == "Y" ]]; then
  cd colorservice
  ./mvnw clean package
  cd ../
  cd colorfrontend
  ./mvnw clean package
  cd ../
  cd colorgateway
  ./mvnw clean package
  cd ../
fi

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

# Push apps
cd colorservice
cf push
cd ../
cd colorfrontend
cf push
cd ../
cd colorgateway
cf push
cd ../

# Add network policies
cf add-network-policy colorgateway --destination-app greenservice --protocol tcp --port 8080
cf add-network-policy colorgateway --destination-app blueservice --protocol tcp --port 8080
cf add-network-policy colorgateway --destination-app yellowservice --protocol tcp --port 8080
cf add-network-policy colorgateway --destination-app colorfrontend --protocol tcp --port 8080
