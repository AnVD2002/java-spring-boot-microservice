package com.project.common_lib_service.config;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageResource {

    private final MessageSource messageSource;

    public MessageResource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String messageCode) {
        return messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
    }

    public String getMessage(String messageCode, Object[] args) {
        return messageSource.getMessage(messageCode, args, LocaleContextHolder.getLocale());
    }
}
