import time

from flask import Blueprint, jsonify, request
from .models import Playlist
from radio.utils import base_prefix

blueprint = Blueprint('playlists', __name__, url_prefix=(base_prefix + '/playlists'))


@blueprint.route("/")
def get_playlists():
    time.sleep(1)
    return jsonify([playlist.to_dict() for playlist in Playlist.query.order_by(Playlist.title.asc()).all()])


@blueprint.route("/<playlist_id>")
def get_playlist_tracks(playlist_id):
    time.sleep(1)
    return jsonify([track.to_dict(url_root=request.url_root) for track in
                    Playlist.query.filter_by(id=playlist_id).one().tracks])
