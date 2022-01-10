FROM adoptopenjdk/openjdk8:ubuntu

RUN apt update && \
  apt -y upgrade && \
  apt install -y wget unzip maven
RUN useradd -u 1000 -m -U flower

USER flower

COPY --chown=1000:flower . /home/flower
WORKDIR /home/flower

RUN mkdir -p ~/.m2/repository/com/sedmelluq/lavaplayer-natives-extra/1.3.13 && \
  wget "https://github.com/sedmelluq/lavaplayer/files/6563671/lavaplayer-natives-extra-1.3.13.zip" -O ~/.m2/repository/com/sedmelluq/lavaplayer-natives-extra/1.3.13/tmp.zip && \
  unzip ~/.m2/repository/com/sedmelluq/lavaplayer-natives-extra/1.3.13/tmp.zip -d ~/.m2/repository/com/sedmelluq/lavaplayer-natives-extra/1.3.13/ && \
  rm ~/.m2/repository/com/sedmelluq/lavaplayer-natives-extra/1.3.13/tmp.zip && \
  mvn clean package

ENTRYPOINT java -Dnogui=true -jar $(find ./target -name "Flowermoon-*-All.jar")