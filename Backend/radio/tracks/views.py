import os

from flask import Blueprint, current_app, send_file

from radio.utils import base_prefix

blueprint = Blueprint('tracks', __name__, url_prefix=(base_prefix + '/tracks'))


@blueprint.route("/<track_uri>")
def stream_audio(track_uri):
    return send_file(os.path.join(current_app.config['UPLOAD_FOLDER'], track_uri),
                     mimetype='audio/mpeg')
