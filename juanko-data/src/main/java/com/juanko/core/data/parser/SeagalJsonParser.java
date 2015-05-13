package com.juanko.core.data.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;

/**
 *
 * @author gaston
 */
public class SeagalJsonParser {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public SeagalJsonParser() {
        mapper.setDateFormat(format);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String parse(Object toParse) throws JsonProcessingException {
        return mapper.writeValueAsString(toParse);
    }
}
