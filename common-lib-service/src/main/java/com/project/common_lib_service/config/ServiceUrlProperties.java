package com.project.common_lib_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "services")
@Getter
@Setter
public class ServiceUrlProperties {
    private Map<String, String> urls = new HashMap<>();

    /**
     * Get service URL by service name
     * @param serviceName the name of the service
     * @return the base URL of the service
     * @throws IllegalArgumentException if service name is not configured
     */
    public String getServiceUrl(String serviceName) {
        String url = urls.get(serviceName);
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Service URL not found for service: " + serviceName);
        }
        return url;
    }

    /**
     * Check if service URL exists
     */
    public boolean hasService(String serviceName) {
        return urls.containsKey(serviceName) && urls.get(serviceName) != null;
    }

    /**
     * Get service URL with default fallback
     */
    public String getServiceUrlOrDefault(String serviceName, String defaultUrl) {
        return urls.getOrDefault(serviceName, defaultUrl);
    }
}
