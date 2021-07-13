import os

from flask import current_app
from flask_sqlalchemy import event

from radio import db


class Playlist(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String, unique=True, nullable=False)
    tracks = db.relationship('Track', backref='playlist', passive_deletes=True)

    def to_dict(self):
        return {'id': self.id,
                'name': self.name}


class Track(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String, nullable=False)
    artist = db.Column(db.String, nullable=False)
    length = db.Column(db.String, nullable=False)
    playlist_id = db.Column(db.Integer, db.ForeignKey('playlist.id', ondelete='CASCADE'), nullable=False)

    def generate_uri(self):
        return f'{self.artist}_{self.title}_{self.id}.mp3'.replace(" ", "_")

    def to_dict(self):
        return {'id': self.id,
                'name': self.name,
                'artist': self.artist}


@event.listens_for(Playlist, 'after_delete')
def execute_after_playlist_deletion(mapper, connection, playlist):
    print("Executing after playlist deletion")
    print(playlist.tracks)

    for track in playlist.tracks:
        os.remove(os.path.join(current_app.config['UPLOAD_FOLDER'], track.generate_uri()))
