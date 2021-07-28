from radio import create_app

config = 'config.py'
app = create_app(config)

if __name__ == '__main__':
    app.run()
