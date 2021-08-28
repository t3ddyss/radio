from flask import Blueprint, jsonify, request

from radio.utils import base_prefix
from .models import Playlist

blueprint = Blueprint('playlists', __name__, url_prefix=(base_prefix + '/playlists'))


@blueprint.route("/")
def get_playlists():
    return jsonify([playlist.to_dict() for playlist in Playlist.query.order_by(Playlist.title.asc()).all()])


@blueprint.route("/<playlist_id>")
def get_playlist_tracks(playlist_id):
    return jsonify([track.to_dict(url_root=request.url_root) for track in
                    Playlist.query.filter_by(id=playlist_id).one().tracks])
