mkdir -p logs

nohup spring cloud configserver > logs/configserver.out 2>&1 </dev/null &

sleep 10

nohup spring cloud eureka > logs/eureka.out 2>&1 </dev/null &

sleep 10

cd blueorgreenservice
nohup ./mvnw spring-boot:run -Dspring-boot.run.profiles=blue > ../logs/blue.out 2>&1 </dev/null &
nohup ./mvnw spring-boot:run -Dspring-boot.run.profiles=green > ../logs/green.out 2>&1 </dev/null &
nohup ./mvnw spring-boot:run -Dspring-boot.run.profiles=slowgreen > ../logs/slowgreen.out 2>&1 </dev/null &
nohup ./mvnw spring-boot:run -Dspring-boot.run.profiles=yellow > ../logs/yellow.out 2>&1 </dev/null &

cd ../blueorgreenfrontend
nohup ./mvnw spring-boot:run -Dspring-boot.run.profiles=local > ../logs/frontend.out 2>&1 </dev/null &

cd ../blueorgreengateway
nohup ./mvnw spring-boot:run > ../logs/gateway.out 2>&1 </dev/null &

cd ../authgateway
nohup ./mvnw spring-boot:run > ../logs/authgateway.out 2>&1 </dev/null &

cd ..
