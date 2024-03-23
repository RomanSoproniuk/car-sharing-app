FROM ubuntu:latest
LABEL authors="anteu"

ENTRYPOINT ["top", "-b"]