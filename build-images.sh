pack config default-builder paketobuildpacks/builder:base

pack build authgateway:pk --path ./authgateway/target/authgateway-0.0.1-SNAPSHOT.jar
pack build blueorgreenfrontend:pk --path ./blueorgreenfrontend/target/blueorgreenfrontend-0.0.1-SNAPSHOT.jar
pack build blueorgreengateway:pk --path ./blueorgreengateway/target/blueorgreengateway-0.0.1-SNAPSHOT.jar
pack build blueorgreenservice:pk --path ./blueorgreenservice/target/blueorgreen-0.0.1-SNAPSHOT.jar
