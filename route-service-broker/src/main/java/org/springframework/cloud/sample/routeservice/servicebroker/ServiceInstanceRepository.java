package org.springframework.cloud.sample.routeservice.servicebroker;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServiceInstanceRepository extends MongoRepository<ServiceInstance, String> {
}
