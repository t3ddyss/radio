import os

from flask import Blueprint, send_file, current_app, Response, stream_with_context

from radio.utils import base_prefix

blueprint = Blueprint('audios', __name__, url_prefix=(base_prefix + '/audios'))


@blueprint.route('/test')
def get_audio():
    return send_file(os.path.join(current_app.config['UPLOAD_FOLDER'], "track.mp3"),
                     mimetype='audio/mpeg')


@blueprint.route("/")
def stream_audio():
    chunk_size = 1024 * 1024 // 2  # 512Kb

    def generate():
        with open(os.path.join(current_app.config['UPLOAD_FOLDER'], "track.mp3"), "rb") as audio:
            data = audio.read(chunk_size)
            while data:
                yield data
                data = audio.read(chunk_size)

    return Response(stream_with_context(generate()), mimetype="audio/mpeg")
