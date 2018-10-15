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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.servicebroker.model.binding.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class RouteLoggingServiceBindingService implements ServiceInstanceBindingService {
	@Value("${vcap.application.uris[0]:localhost}")
	private String appRoute;

	@Override
	public Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
		URI uri = new DefaultUriBuilderFactory().builder()
				.scheme("https")
				.host(appRoute)
				.pathSegment("instanceId", request.getServiceInstanceId())
				.build();

		return Mono.just(CreateServiceInstanceRouteBindingResponse.builder()
				.routeServiceUrl(uri.toString())
				.build());
	}

	@Override
	public Mono<DeleteServiceInstanceBindingResponse> deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
		return Mono.empty();
	}

}
