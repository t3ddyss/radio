import os
from time import strftime, gmtime

import click
from shutil import copy2
from flask import Blueprint, current_app
from mutagen.easyid3 import EasyID3
from mutagen.mp3 import MP3
from sqlalchemy.exc import IntegrityError

from radio import db
from radio.audios.models import Playlist, Track

blueprint = Blueprint('admin', __name__)


@blueprint.cli.command('get-playlists')
def get_playlists():
    playlists = Playlist.query.all()
    print("\n".join(playlists))


@blueprint.cli.command('create-playlist')
@click.argument('title')
def create_playlist(title):
    playlist = Playlist(title=title)

    try:
        db.session.add(playlist)
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
        print(f'Playlist "{title}" already exists')
        return

    print(f'Successfully created playlist "{title}"')


@blueprint.cli.command('delete-playlist')
@click.argument('title')
def delete_playlist(title):
    playlist = Playlist.query.filter_by(title=title).first()

    if not playlist:
        print(f'Playlist "{title}" doesn' + "'t exist")
        return

    db.session.delete(playlist)
    db.session.commit()
    print(f'Successfully removed playlist "{title}"')


@blueprint.cli.command('add-track')
@click.argument('playlist_title', nargs=1)
@click.argument('audio_path', nargs=1)
def add_track(playlist_title, audio_path):
    filename, file_extension = os.path.splitext(audio_path)
    if file_extension != '.mp3':
        print('Only .mp3 files are supported')
        return

    playlist = Playlist.query.filter_by(title=playlist_title).first()
    if not playlist:
        print(f'Playlist "{playlist_title}" doesn' + "'t exist")
        return

    # Assuming that audio length is less than 1 hour
    def convert_length(length):
        return strftime("%M:%S", gmtime(length))

    audio = EasyID3(audio_path)
    try:
        track = Track(artist=audio['artist'][0],
                      title=audio['title'][0],
                      length=convert_length(MP3(audio_path).info.length))

        playlist.tracks.append(track)
        db.session.flush()
        db.session.refresh(track)
        copy2(audio_path, os.path.join(current_app.config['UPLOAD_FOLDER'], track.generate_uri()))
        db.session.commit()
    except KeyError:
        print('Provided audio is missing required metadata (artist and/or title)')
        return

    print(f'Successfully added track to playlist "{playlist_title}"')
