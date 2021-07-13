from os.path import join, dirname, realpath

SQLALCHEMY_TRACK_MODIFICATIONS = False
UPLOAD_FOLDER = join(dirname(realpath(__file__)), 'instance/audios')
