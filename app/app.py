from flask import Flask
from http import HTTPStatus
from werkzeug.exceptions import HTTPException

app = Flask(__name__)

@app.route("/")
def hello():
    return "Hello Docker!\n"

@app.errorhandler(HTTPException)
def handle_http_exception(exception: HTTPException):
    return exception.description, exception.code

@app.errorhandler(Exception)
def handle_exception(exception: Exception):
    return HTTPStatus.INTERNAL_SERVER_ERROR.description, HTTPStatus.INTERNAL_SERVER_ERROR
