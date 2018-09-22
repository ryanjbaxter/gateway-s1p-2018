/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sample.routeservice.servicebroker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RouteLoggingServiceInstanceService implements ServiceInstanceService {
	private final Logger log = LoggerFactory.getLogger(ServiceInstanceService.class);

	ServiceInstanceRepository serviceInstanceRepository;

	RateLimiters rateLimiters;

	public RouteLoggingServiceInstanceService(ServiceInstanceRepository serviceInstanceRepository, RateLimiters rateLimiters ) {
		this.serviceInstanceRepository = serviceInstanceRepository;
		this.rateLimiters = rateLimiters;
	}

	@Override
	public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {

		ServiceInstance serviceInstance = ServiceInstance.builder()
				.serviceInstanceId(request.getServiceInstanceId())
				.serviceDefinitionId(request.getServiceDefinitionId())
				.planId(request.getPlanId())
//				.logLevel((String)request.getParameters().get("log-level"))
				.build();

		log.info("Received create-service request: {}", serviceInstance.toString());

		serviceInstanceRepository.save(serviceInstance);

		log.info("Saved new service instance info [serviceInstanceId={}].", serviceInstance.getServiceInstanceId());
		log.info("Service instance count: {}", serviceInstanceRepository.count());
		if (log.isInfoEnabled()) {
			serviceInstanceRepository.findAll().forEach(System.out::println);
		}

		rateLimiters.addLimiter(serviceInstance.getServiceInstanceId(), serviceInstance.getServiceDefinitionId(), serviceInstance.getPlanId());

		return Mono.just(CreateServiceInstanceResponse.builder()
				.instanceExisted(false)
				.build());
	}


	@Override
	public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {

		String serviceInstanceId = request.getServiceInstanceId();

		log.info("Received delete-service request: {}", serviceInstanceId);

		serviceInstanceRepository.deleteById(serviceInstanceId);

		log.info("Deleted service instance info [serviceInstanceId={}].", serviceInstanceId);
		log.info("Service instance count: {}", serviceInstanceRepository.count());

		rateLimiters.removeLimiter(serviceInstanceId);

		return Mono.just(DeleteServiceInstanceResponse.builder()
				.build());
	}
}
