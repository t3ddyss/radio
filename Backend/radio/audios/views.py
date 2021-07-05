import os

from flask import Blueprint, send_file, current_app

from radio.utils import base_prefix

blueprint = Blueprint('audios', __name__, url_prefix=(base_prefix + '/audios'))


@blueprint.route('/')
def get_audio():
    return send_file(os.path.join(current_app.config['UPLOAD_FOLDER'], "track.mp3"),
                     mimetype='audio/mpeg')
