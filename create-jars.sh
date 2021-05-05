mkdir -p logs

cd ../blueorgreenservice
nohup ./mvnw clean package -DskipTests -Dspring.cloud.contract.verifier.skip=true > ../logs/jar-blueandgreenservice.out 2>&1 </dev/null &

cd ../blueorgreenfrontend
nohup ./mvnw clean package -DskipTests > ../logs/jar-frontend.out 2>&1 </dev/null &

cd ../blueorgreengateway
nohup ./mvnw clean package -DskipTests > ../logs/jar-gateway.out 2>&1 </dev/null &

cd ../authgateway
nohup ./mvnw clean package -DskipTests > ../logs/jar-authgateway.out 2>&1 </dev/null &

cd ..
