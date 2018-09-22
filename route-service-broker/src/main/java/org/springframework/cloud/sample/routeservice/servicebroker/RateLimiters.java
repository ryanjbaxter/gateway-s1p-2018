package org.springframework.cloud.sample.routeservice.servicebroker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.sample.routeservice.servicebroker.ServiceCatalogConfig.Plan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RateLimiters implements ApplicationContextAware, InitializingBean {
    private final Logger log = LoggerFactory.getLogger(RateLimiters.class);

    public Map<String, RedisRateLimiter> redisRateLimiterMap;

    private ApplicationContext applicationContext;

    @Autowired
    private ServiceInstanceRepository serviceInstanceRepository;

    @Autowired
    ServiceCatalogConfig serviceCatalogConfig;

    public RateLimiters() {
         redisRateLimiterMap = new HashMap<String, RedisRateLimiter>();
    }

    public RedisRateLimiter getLimiter(String instanceId) {
            return redisRateLimiterMap.get(instanceId);
    }

    public void addLimiter(String serviceInstanceId, String serviceDefinitionId, String planId) {

        List<Plan> plans = serviceCatalogConfig.getServices().stream()
                .filter(s -> s.getId().equals(serviceDefinitionId))
                .findFirst().get()
                .getPlans();

        Plan plan = plans.stream()
                .filter(p -> p.getId().equals(planId))
                .findFirst().get();

        plan.getConfigs().forEach(config -> {
            String name = config.getName().name();
            int replenishRate = config.getReplenishRate();
            int burstCapacity = config.getBurstCapacity();
            RedisRateLimiter rrl = new RedisRateLimiter(replenishRate, burstCapacity);
            rrl.setApplicationContext(applicationContext);
            redisRateLimiterMap.put(serviceInstanceId.concat("_" + name.toUpperCase()), rrl);
            log.info("RateLimiters size = {}", redisRateLimiterMap.size());

        });
    }

    public void removeLimiter(String instanceId) {
        redisRateLimiterMap.remove(instanceId);
        log.info("RateLimiters size = {}", redisRateLimiterMap.size());
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        // load map from repo
        serviceInstanceRepository.findAll().forEach(e -> addLimiter(e.getServiceInstanceId(), e.getServiceDefinitionId(), e.getPlanId()));
    }
}
