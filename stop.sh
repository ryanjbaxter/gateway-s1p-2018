# Auth Gateway
http POST :8080/actuator/shutdown

# Gateway
http POST :8383/actuator/shutdown

# Frontend
http POST :9090/actuator/shutdown

# Backend
http POST :8181/actuator/shutdown
http POST :8282/actuator/shutdown
http POST :7070/actuator/shutdown
http POST :6060/actuator/shutdown

# Config Server & Eureka Server
http POST :8888/actuator/shutdown
http POST :8761/actuator/shutdown

