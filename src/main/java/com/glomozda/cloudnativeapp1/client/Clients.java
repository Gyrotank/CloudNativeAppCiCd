package com.glomozda.cloudnativeapp1.client;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.s3.AmazonS3;

public interface Clients {
    Clients getInstance();

    AmazonDynamoDB getDynamoDbClient();

    AmazonS3 getS3Client();
}
