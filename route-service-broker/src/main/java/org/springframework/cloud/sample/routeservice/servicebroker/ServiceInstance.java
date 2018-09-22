package org.springframework.cloud.sample.routeservice.servicebroker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@NoArgsConstructor
public class ServiceInstance {

    @Id
    @Getter
    private String serviceInstanceId;
    @Getter
    private String serviceDefinitionId;
    @Getter
    private String planId = null;
//    @Getter
//    private String logLevel = "INFO";

    public static ServiceInstanceBuilder builder() {
        return new ServiceInstanceBuilder();
    }

    @Override
    public String toString() {
        return "ServiceInstance{" +
                "serviceInstanceId='" + serviceInstanceId + '\'' +
                "serviceDefinitionId='" + serviceDefinitionId + '\'' +
                "planId='" + planId + '\'' +
//                ", logLevel='" + logLevel + '\'' +
                '}';
    }

    @NoArgsConstructor
    static class ServiceInstanceBuilder {

        private String serviceInstanceId;
        private String serviceDefinitionId;
        private String planId;
//        private String logLevel;

        ServiceInstanceBuilder serviceInstanceId(String serviceInstanceId) {
            this.serviceInstanceId = serviceInstanceId;
            return this;
        }

        ServiceInstanceBuilder serviceDefinitionId(String serviceDefinitionId) {
            this.serviceDefinitionId = serviceDefinitionId;
            return this;
        }

        ServiceInstanceBuilder planId(String planId) {
            this.planId = planId;
            return this;
        }

//        ServiceInstanceBuilder logLevel(String logLevel) {
//            this.logLevel = logLevel;
//            return this;
//        }

        ServiceInstance build() {
            return new ServiceInstance(serviceInstanceId, serviceDefinitionId, planId);
        }

    }

}

