import os

from flask import current_app
from flask_sqlalchemy import event

from radio import db


class Track(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    artist = db.Column(db.String, nullable=False)
    title = db.Column(db.String, nullable=False)
    length = db.Column(db.String, nullable=False)
    playlist_id = db.Column(db.Integer, db.ForeignKey('playlist.id', ondelete='CASCADE'), nullable=False)

    def get_uri(self):
        return f'{self.artist}_{self.title}_{self.id}'.replace(" ", "_")

    def to_dict(self, url_root):
        return {'id': self.id,
                'artist': self.artist,
                'title': self.title,
                'length': self.length,
                'url': url_root + "api/tracks/" + self.get_uri()}

    def to_str(self):
        return f'{self.artist} – {self.title}, {self.length}.'


@event.listens_for(Track, 'before_delete')
def execute_before_track_deletion(mapper, connection, track):
    os.remove(os.path.join(current_app.config['UPLOAD_FOLDER'], track.get_uri()))
