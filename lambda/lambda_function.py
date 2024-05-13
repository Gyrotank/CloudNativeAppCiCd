import json
import boto3

rekognition = boto3.client("rekognition")
dynamodb = boto3.resource("dynamodb")
table = dynamodb.Table("CloudNativeApp1Table")

def lambda_handler(event, context):
    for event_record in event["Records"]:
        body = json.loads(event_record["body"])
        message = json.loads(body["Message"])

        for record in message["Records"]:
            bucket = record["s3"]["bucket"]["name"]
            key = record["s3"]["object"]["key"]

            try:
                response = rekognition.detect_labels(Image={"S3Object": {"Bucket": bucket, "Name": key}})
            except Exception as e:
                print(f"Error processing object {key} from bucket {bucket} by rekognition service.")
                raise

            labels = [label["Name"] for label in response["Labels"]]
            for label in labels:
                image_metadata = {"LabelValue": label, "ImageName": key}
                table.put_item(Item=image_metadata)