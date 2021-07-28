# syntax=docker/dockerfile:1

FROM python:3.9.5-slim-buster

WORKDIR /usr/src/app
COPY . /usr/src/app
RUN mkdir ./instance/audios

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

RUN apt-get update && apt-get install -y \
    curl \
    libmagic1 \
    netcat \
    && apt-get -y autoclean

EXPOSE 5000

ENTRYPOINT ["./entrypoint.sh"]