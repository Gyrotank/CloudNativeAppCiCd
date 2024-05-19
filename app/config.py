import os

class Config:
    S3_BUCKET_NAME = os.getenv('S3_BUCKET_NAME')
    DYNAMODB_TABLE_NAME = os.getenv('DYNAMODB_TABLE_NAME')
    S3_REGION = os.getenv('AWS_REGION', 'eu-west-1')
