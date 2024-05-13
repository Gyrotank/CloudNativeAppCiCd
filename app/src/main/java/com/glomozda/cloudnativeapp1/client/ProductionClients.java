package com.glomozda.cloudnativeapp1.client;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Getter
public class ProductionClients implements Clients {
    private final AmazonDynamoDB dynamoDbClient;
    private final AmazonS3 s3Client;

    private ProductionClients instance = null;

    private ProductionClients() {
        dynamoDbClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.EU_WEST_1)
                .build();

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    @Override
    public Clients getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ProductionClients();
        }
        return instance;
    }
}
