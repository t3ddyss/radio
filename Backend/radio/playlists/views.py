from flask import Blueprint, jsonify
from .models import Playlist
from radio.utils import base_prefix

blueprint = Blueprint('playlists', __name__, url_prefix=(base_prefix + '/playlists'))


@blueprint.route("/")
def get_playlists():
    return jsonify([playlist.to_dict() for playlist in Playlist.query.all()])


@blueprint.route("/<playlist_title>")
def get_playlist_tracks(playlist_title):
    return jsonify([track.to_dict() for track in Playlist.query.filter_by(title=playlist_title).one().tracks])

