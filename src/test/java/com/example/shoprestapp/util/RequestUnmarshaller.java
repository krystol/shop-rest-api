package com.example.shoprestapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
public class RequestUnmarshaller {

    private final ObjectMapper objectMapper;

    @Autowired
    public RequestUnmarshaller(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String getContent(MvcResult mvcResult) throws IOException {
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(content).isNotNull().isNotEmpty();
        return content;
    }

    public <T> T unmarshall(MvcResult mvcResult, Class<T> cls) throws IOException {
        return unmarshall(mvcResult, objectMapper.readerFor(cls));
    }

    public <T> T unmarshall(MvcResult mvcResult, ObjectReader objectReader) throws IOException {
        String content = getContent(mvcResult);
        return objectReader.readValue(content);
    }

    public <T> List<T> unmarshallList(MvcResult mvcResult, Class<T> cls) throws IOException {
        return unmarshallList(mvcResult, objectMapper.readerFor(cls));
    }

    public <T> List<T> unmarshallList(MvcResult mvcResult, ObjectReader objectReader) throws IOException {
        String content = getContent(mvcResult);
        MappingIterator<T> objectMappingIterator = objectReader.readValues(content);
        return objectMappingIterator.readAll();
    }

    public <T> String toJson(T o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    public <T> T deserialize(String json, Class<? extends T> target) throws IOException {
        return objectMapper.readValue(json, target);
    }

    public <T> T deserializeFromFile(String pathToResource, Class<T> klasse) throws IOException {
        File fileJson = new File(klasse.getResource(pathToResource).getFile());
        T sampleInput = objectMapper.readValue(fileJson, klasse);
        return sampleInput;
    }
}
