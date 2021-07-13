import os

from flask import Blueprint, current_app, Response, stream_with_context

from radio.utils import base_prefix

blueprint = Blueprint('tracks', __name__, url_prefix=(base_prefix + '/tracks'))


@blueprint.route("/<track_uri>")
def stream_audio(track_uri):
    chunk_size = 1024 * 1024 // 2  # 512Kb

    def generate():
        with open(os.path.join(current_app.config['UPLOAD_FOLDER'], track_uri), "rb") as audio:
            data = audio.read(chunk_size)
            while data:
                yield data
                data = audio.read(chunk_size)

    return Response(stream_with_context(generate()), mimetype="audio/mpeg")
