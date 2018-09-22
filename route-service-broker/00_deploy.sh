# CLEANUP

cf urs cfapps.io subscription-gateway --hostname cool-app -f
cf unmap-route cool-app cfapps.io --hostname cool-app

cf urs cfapps.io subscription-gateway --hostname appgateway -f
#cf unmap-route appgateway cfapps.io --hostname appgateway

cf ds subscription-gateway -f

cf delete-service-broker route-service -f

#cf d route-service-broker -f
#cf ds route-service-mongodb -f
#cf ds route-service-redis -f

cf delete-orphaned-routes -f

# DEPLOY

#cd ~/workspace/route-service-broker-thumbnail
#cf push

cd ~/workspace/route-service-broker
./gradlew assemble
#cf cs mlab sandbox route-service-mongodb
#cf cs rediscloud 30mb route-service-redis
cf push

# RECONFIGURE

cf create-service-broker route-service admin supersecret https://route-service-broker.cfapps.io --space-scoped

cf cs route-service standard subscription-gateway

cf map-route cool-app cfapps.io --hostname cool-app
cf brs cfapps.io subscription-gateway --hostname cool-app

#cf map-route appgateway cfapps.io --hostname appgateway
cf brs cfapps.io subscription-gateway --hostname appgateway
