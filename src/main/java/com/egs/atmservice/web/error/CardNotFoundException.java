package com.egs.atmservice.web.error;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CardNotFoundException extends Exception {

    private final String entityName;

    private final String errorKey;

    public CardNotFoundException(String defaultMessage, String entityName, String errorKey) {
        this(ErrorConstants.DEFAULT_TYPE, defaultMessage, entityName, errorKey);
    }

    public CardNotFoundException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(defaultMessage);
        getAlertParameters(entityName, errorKey);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "error." + errorKey);
        parameters.put("params", entityName);
        return parameters;
    }
}
