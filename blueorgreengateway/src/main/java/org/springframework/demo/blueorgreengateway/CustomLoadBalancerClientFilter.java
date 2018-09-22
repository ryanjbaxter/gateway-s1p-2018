package org.springframework.demo.blueorgreengateway;

import java.net.URI;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.http.HttpCookie;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @author Ryan Baxter
 */
public class CustomLoadBalancerClientFilter extends LoadBalancerClientFilter {

	private static final Log log = LogFactory.getLog(LoadBalancerClientFilter.class);

	public CustomLoadBalancerClientFilter(LoadBalancerClient loadBalancer) {
		super(loadBalancer);
	}

	@Override
	protected ServiceInstance choose(ServerWebExchange exchange) {
		if("blueorgreen".equals(((URI) exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR)).getHost())) {
			MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
			log.warn("cookie: " + exchange.getRequest().getHeaders().get("cookie"));
			if (!cookies.containsKey("type") || !"premium".equals(cookies.getFirst("type").getValue())) {
				long future = System.currentTimeMillis() + 3000;
				while (System.currentTimeMillis() < future) {
					ServiceInstance instance = super.choose(exchange);
					if (instance != null && (instance.getMetadata() == null || instance.getMetadata().get("type") == null ||
							!"premium".equals(instance.getMetadata().get("type").toLowerCase()))) {
						return instance;
					}
				}
				return null;
			}
		}
		return super.choose(exchange);
	}

}
