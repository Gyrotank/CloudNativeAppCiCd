package com.glomozda.cloudnativeapp1.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.glomozda.cloudnativeapp1.client.Clients;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Tags({@Tag("UnitTest"), @Tag("ServiceTest"), @Tag("ImageServiceTest")})
class ImageServiceImplTests {
    @Value("${environment.s3-bucket-name}")
    private String s3BucketName;

    @Value("${environment.dynamodb-table-name}")
    private String dynamoDbTableName;

    @Mock
    private Clients mockClients;
    @Mock
    private AmazonS3Client mockS3Client;
    @Mock
    private AmazonDynamoDBClient mockDynamoDbClient;

    private ImageServiceImpl imageServiceImpl;

    @BeforeEach
    void setUp() {
        imageServiceImpl = new ImageServiceImpl(s3BucketName, dynamoDbTableName, mockClients);
    }

    @Test
    void testUploadImage_NoException_OK() {
        //Arrange
        String id = "testIdOk";
        byte[] bytes = new byte[1];

        when(mockClients.getInstance()).thenReturn(new Clients() {
            @Override
            public Clients getInstance() {
                return this;
            }

            @Override
            public AmazonDynamoDB getDynamoDbClient() {
                return mockDynamoDbClient;
            }

            @Override
            public AmazonS3 getS3Client() {
                return mockS3Client;
            }
        });
        when(mockS3Client.putObject(eq(s3BucketName), eq(id), any(ByteArrayInputStream.class), eq(null)))
                .thenReturn(new PutObjectResult());

        //Act
        HttpStatusCode result = imageServiceImpl.uploadImage(id, bytes);

        //Assert
        verify(mockS3Client).putObject(eq(s3BucketName), eq(id), any(ByteArrayInputStream.class), eq(null));
        Assertions.assertEquals(HttpStatus.OK, result);
    }

    @Test
    void testUploadImage_AmazonServiceException_ServiceUnavailable() {
        //Arrange
        String id = "testIdAse";
        byte[] bytes = new byte[1];

        when(mockClients.getInstance()).thenReturn(new Clients() {
            @Override
            public Clients getInstance() {
                return this;
            }

            @Override
            public AmazonDynamoDB getDynamoDbClient() {
                return mockDynamoDbClient;
            }

            @Override
            public AmazonS3 getS3Client() {
                return mockS3Client;
            }
        });
        when(mockS3Client.putObject(eq(s3BucketName), eq(id), any(ByteArrayInputStream.class), eq(null)))
                .thenThrow(new AmazonServiceException("Error!"));

        //Act
        HttpStatusCode result = imageServiceImpl.uploadImage(id, bytes);

        //Assert
        verify(mockS3Client).putObject(eq(s3BucketName), eq(id), any(ByteArrayInputStream.class), eq(null));
        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result);
    }

    @Test
    void testUploadImage_AmazonClientException_ServiceUnavailable() {
        //Arrange
        String id = "testIdAce";
        byte[] bytes = new byte[1];

        when(mockClients.getInstance()).thenReturn(new Clients() {
            @Override
            public Clients getInstance() {
                return this;
            }

            @Override
            public AmazonDynamoDB getDynamoDbClient() {
                return mockDynamoDbClient;
            }

            @Override
            public AmazonS3 getS3Client() {
                return mockS3Client;
            }
        });
        when(mockS3Client.putObject(eq(s3BucketName), eq(id), any(ByteArrayInputStream.class), eq(null)))
                .thenThrow(new AmazonClientException("Error!"));

        //Act
        HttpStatusCode result = imageServiceImpl.uploadImage(id, bytes);

        //Assert
        verify(mockS3Client).putObject(eq(s3BucketName), eq(id), any(ByteArrayInputStream.class), eq(null));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    void testGetImagesDataByLabel_LabelExists_ListOfImagesWithLabel() {
        //Arrange
        String label = "testLabel";
        String expectedResult = """
                [ {
                  "ImageName" : {
                    "s" : "image1"
                  },
                  "LabelValue" : {
                    "s" : "testLabel"
                  }
                } ]""";

        when(mockClients.getInstance()).thenReturn(new Clients() {
            @Override
            public Clients getInstance() {
                return this;
            }

            @Override
            public AmazonDynamoDB getDynamoDbClient() {
                return mockDynamoDbClient;
            }

            @Override
            public AmazonS3 getS3Client() {
                return mockS3Client;
            }
        });

        Map<String, AttributeValue> queryResultItem1 = new HashMap<>();
        queryResultItem1.put("ImageName", new AttributeValue().withS("image1"));
        queryResultItem1.put("LabelValue", new AttributeValue().withS(label));
        when(mockDynamoDbClient.query(any()))
                .thenReturn(new QueryResult().withItems(queryResultItem1));

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":pkValue", new AttributeValue().withS(label));

        QueryRequest expectedQueryRequest = new QueryRequest()
                .withTableName(dynamoDbTableName)
                .withKeyConditionExpression("#pk = :pkValue")
                .withExpressionAttributeNames(Map.of("#pk", "LabelValue"))
                .withExpressionAttributeValues(expressionAttributeValues);

        //Act
        String result = imageServiceImpl.getImagesDataByLabel(label);

        //Assert
        verify(mockDynamoDbClient).query(expectedQueryRequest);
        Assertions.assertEquals(expectedResult.replace("\r", ""),
                result.replace("\r", ""));
    }

    @Test
    void testGetImagesDataByLabel_LabelNotExists_EmptyList() {
        //Arrange
        String label = "testBadLabel";
        String expectedResult = "[ ]";

        when(mockClients.getInstance()).thenReturn(new Clients() {
            @Override
            public Clients getInstance() {
                return this;
            }

            @Override
            public AmazonDynamoDB getDynamoDbClient() {
                return mockDynamoDbClient;
            }

            @Override
            public AmazonS3 getS3Client() {
                return mockS3Client;
            }
        });

        when(mockDynamoDbClient.query(any()))
                .thenReturn(new QueryResult().withItems(new ArrayList<>()));

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":pkValue", new AttributeValue().withS(label));

        QueryRequest expectedQueryRequest = new QueryRequest()
                .withTableName(dynamoDbTableName)
                .withKeyConditionExpression("#pk = :pkValue")
                .withExpressionAttributeNames(Map.of("#pk", "LabelValue"))
                .withExpressionAttributeValues(expressionAttributeValues);

        //Act
        String result = imageServiceImpl.getImagesDataByLabel(label);

        //Assert
        verify(mockDynamoDbClient).query(expectedQueryRequest);
        Assertions.assertEquals(expectedResult, result);
    }
}