package org.springframework.cloud.sample.routeservice.servicebroker;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "servicecatalogconfig")
public class ServiceCatalogConfig {

    @Getter
    private List<Service> services;

    public enum Type {
        TRIAL,
        BASIC,
        PREMIUM;
    }

    ServiceCatalogConfig() {
        this.services = new ArrayList<>();
    }

    @NoArgsConstructor
    static class Service {
        @Id
        @Getter
        @Setter
        private String id;
        @Getter
        @Setter
        private List<Plan> plans;
    }

    @NoArgsConstructor
    static class Plan {
        @Id
        @Getter
        @Setter
        private String id;
        @Getter
        @Setter
        private List<Config> configs;
    }

    @NoArgsConstructor
    static class Config {
        @Id
        @Getter
        @Setter
        private Type name;
        @Getter
        @Setter
        private int replenishRate;
        @Getter
        @Setter
        private int burstCapacity;
    }

}



