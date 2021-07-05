from flask import Flask

from radio import audios


def create_app(config_filename='config.py'):
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_pyfile(f"../{config_filename}")
    app.config.from_pyfile(config_filename)

    # register_extensions(app)
    register_blueprints(app)

    return app


# def register_extensions(app):


def register_blueprints(app):
    app.register_blueprint(audios.views.blueprint)
