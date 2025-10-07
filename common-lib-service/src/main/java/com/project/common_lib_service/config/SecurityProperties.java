package com.project.common_lib_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
public class SecurityProperties {
    private List<String> whitelist = new ArrayList<>();
    private List<String> defaultWhitelist = new ArrayList<>();

    public List<String> getMergedWhitelist() {
        List<String> merged = new ArrayList<>(defaultWhitelist);
        merged.addAll(whitelist);
        return merged.stream().distinct().toList();
    }
}
