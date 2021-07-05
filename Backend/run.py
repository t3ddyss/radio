from radio import create_app

config = 'config.py'

if __name__ == '__main__':
    app = create_app(config)
    app.run()
