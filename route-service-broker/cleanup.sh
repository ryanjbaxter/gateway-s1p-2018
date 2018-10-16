# CLEANUP

cf urs cfapps.io subscription-gateway --hostname techfest-cool-app -f
cf unmap-route cool-app cfapps.io --hostname techfest-cool-app

cf urs cfapps.io subscription-gateway --hostname techfest-colorgateway -f
cf unmap-route colorgateway cfapps.io --hostname techfest-colorgateway

cf ds subscription-gateway -f

cf delete-service-broker techfest-route-service -f

cf d techfest-route-service-broker -f
cf ds route-service-mongodb -f
cf ds route-service-redis -f

cf delete-orphaned-routes -f
