import os
from shutil import copy2
from time import strftime, gmtime

import click
from flask import Blueprint, current_app
from mutagen.mp3 import MP3, HeaderNotFoundError
from sqlalchemy.exc import IntegrityError
from magic import from_file
from mutagen.easyid3 import EasyID3
from mutagen.id3 import ID3NoHeaderError

from radio import db
from radio.playlists.models import Playlist
from radio.tracks.models import Track

blueprint = Blueprint('admin', __name__)


@blueprint.cli.command("create-db")
def create_db():
    db.drop_all()
    db.create_all()
    db.session.commit()

    print('Database tables were successfully created')


@blueprint.cli.command('read-playlists')
def read_playlists():
    print("Playlists:")

    print("\n".join(get_playlists()))


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
def delete_playlist():
    playlist = get_selected_playlist(action='delete')

    db.session.delete(playlist)
    db.session.commit()

    print(f'Successfully removed playlist "{playlist.to_str()}"')


@blueprint.cli.command('read-tracks')
def read_tracks_in_playlist():
    playlist = get_selected_playlist(action='read')
    tracks = get_tracks(playlist)

    print("Tracks:")
    print("\n".join(tracks))


@blueprint.cli.command('add-track')
@click.argument('audio_path', nargs=1)
def add_track_to_playlist(audio_path):
    mimetype = from_file(audio_path, mime=True).split('/')

    if mimetype[0] != 'audio':
        print('Not an audio file')
        return

    try:
        audio = MP3(audio_path)
        audio_metadata = EasyID3(audio_path)
    except (HeaderNotFoundError, ID3NoHeaderError):
        print('Only ".mp3" files with ID3 metadata are currently supported')
        return

    playlist = get_selected_playlist(action='modify')

    # Assuming that audio length is less than 1 hour
    def convert_length(length):
        return strftime("%M:%S", gmtime(length))

    try:
        track = Track(artist=audio_metadata['artist'][0],
                      title=audio_metadata['title'][0],
                      length=convert_length(audio.info.length))

        playlist.tracks.append(track)
        db.session.flush()
        db.session.refresh(track)
        copy2(audio_path, os.path.join(current_app.config['UPLOAD_FOLDER'], track.get_uri()))
        db.session.commit()

        print(f'Successfully added track "{track.to_str()}" to playlist "{playlist.title}"')
    except KeyError:
        print('Provided audio is missing required metadata: artist or title')


@blueprint.cli.command('delete-track')
def delete_track_from_playlist():
    playlist = get_selected_playlist(action='modify')
    track = get_selected_track(playlist, action='delete')

    db.session.delete(track)
    db.session.commit()

    print(f'Successfully removed track "{track.to_str()}" from playlist "{playlist.title}"')


def get_playlists():
    return list(map(lambda playlist: playlist.to_str(), Playlist.query.all()))


def get_tracks(playlist):
    return list(map(lambda track: track.to_str(), playlist.tracks))


def get_selected_playlist(action):
    playlists = get_playlists()

    print("Playlists:")
    for (i, item) in enumerate(playlists, start=1):
        print(i, item)

    print(f'Enter the number of the playlist you want to {action}: ', end='')
    num = int(input())
    if num > len(playlists) or num < 1:
        raise ValueError("Incorrect number")

    return Playlist.query.all()[num - 1]


def get_selected_track(playlist, action='delete'):
    tracks = get_tracks(playlist)

    print("Tracks:")
    for (i, item) in enumerate(tracks, start=1):
        print(i, item)

    print(f'Enter the number of the track you want to {action}: ', end='')
    num = int(input())
    if num > len(tracks) or num < 1:
        raise ValueError("Incorrect number")

    return Track.query.all()[num - 1]
