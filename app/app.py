from flask import Flask
import os

app = Flask(__name__)

@app.route("/", methods=["GET"])
def health_check():
    return "Health check passed - v2"
