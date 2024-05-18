from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
import boto3, uuid
import config

app = Flask(__name__)

s3_client = boto3.client('s3')

@app.route("/", methods=["GET"])
def health_check():
    return "Health check passed - v4"

@app.route("/image", methods=["POST"])
def upload_image():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file:
        original_filename = secure_filename(file.filename)
        filename, file_extension = original_filename.rsplit('.', 1)
        new_filename = f"{filename}_{uuid.uuid4()}.{file_extension}"
        bucket_name = config.Config.S3_BUCKET_NAME
        s3_client.upload_fileobj(
            file,
            bucket_name,
            new_filename
        )
        file_url = f"https://{bucket_name}.s3.amazonaws.com/{filename}"
        return jsonify({"message": "File uploaded successfully", "url": file_url}), 200
