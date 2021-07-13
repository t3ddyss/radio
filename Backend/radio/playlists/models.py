import os

from flask import current_app
from sqlalchemy import event

from radio import db


class Playlist(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String, unique=True, nullable=False)
    tracks = db.relationship('Track', backref='playlist', passive_deletes=True)

    def to_dict(self):
        return {'id': self.id,
                'title': self.title,
                'tracks': [track.to_dict() for track in self.tracks]}

    def to_str(self):
        return self.title


@event.listens_for(Playlist, 'before_delete')
def execute_before_playlist_deletion(mapper, connection, playlist):
    for track in playlist.tracks:
        os.remove(os.path.join(current_app.config['UPLOAD_FOLDER'], track.get_uri()))
