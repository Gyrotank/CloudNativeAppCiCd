package com.glomozda.cloudnativeapp1.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glomozda.cloudnativeapp1.client.Clients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.*;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {
    private final String s3BucketName;

    private final String dynamoDbTableName;

    private final Clients clients;

    @Autowired
    public ImageServiceImpl(@Value("${environment.s3-bucket-name}") String s3BucketName,
                            @Value("${environment.dynamodb-table-name}") String dynamoDbTableName,
                            Clients clients) {
        this.s3BucketName = s3BucketName;
        this.dynamoDbTableName = dynamoDbTableName;
        this.clients = clients;
    }

    @Override
    public HttpStatusCode uploadImage(String id, byte[] imageFileContents) {
        try {
            clients.getInstance().getS3Client()
                    .putObject(s3BucketName, id,
                            new ByteArrayInputStream(imageFileContents), null);
            return HttpStatus.OK;
        } catch (AmazonServiceException ase) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        catch (AmazonClientException ace) {
            return HttpStatus.BAD_REQUEST;
        }
    }

    @Override
    public String getImagesDataByLabel(String label) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":pkValue", new AttributeValue().withS(label));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(dynamoDbTableName)
                .withKeyConditionExpression("#pk = :pkValue")
                .withExpressionAttributeNames(Map.of("#pk", "LabelValue"))
                .withExpressionAttributeValues(expressionAttributeValues);

        QueryResult queryResult = clients.getInstance().getDynamoDbClient().query(queryRequest);

        String result = "[]";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(queryResult.getItems());
        } catch (JacksonException e) {
            log.error(e.getOriginalMessage());
        }
        return result;
    }
}
