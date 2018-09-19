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
