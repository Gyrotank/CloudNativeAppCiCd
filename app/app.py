from flask import Flask

app = Flask(__name__)

@app.route("/", methods=["GET"])
def health_check():
    return "Health check passed"

if _name_ == "__main__":
    app.run(host="0.0.0.0", port=8080)