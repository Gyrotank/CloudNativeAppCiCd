from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
import os, boto3

app = Flask(__name__)

s3_client = boto3.client('s3')

@app.route("/", methods=["GET"])
def health_check():
    return "Health check passed - v3"

@app.route("/image", methods=["POST"])
def upload_image():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file:
        filename = secure_filename(file.filename)
        bucket_name = app.config['S3_BUCKET_NAME']
        s3_client.upload_fileobj(
            file,
            bucket_name,
            filename,
            ExtraArgs={"ACL": "public-read"}
        )
        file_url = f"https://{bucket_name}.s3.amazonaws.com/{filename}"
        return jsonify({"message": "File uploaded successfully", "url": file_url}), 200
