FROM openjdk:17-jdk-slim

LABEL maintainer="Sistema de Tráfico Inteligente"
LABEL description="Simulador de tráfico urbano con JavaFX"
LABEL version="1.0"

ENV DEBIAN_FRONTEND=noninteractive
ENV DISPLAY=:0

RUN apt-get update && apt-get install -y \
    openjfx \
    libopenjfx-java \
    x11-apps \
    libgtk-3-0 \
    libgl1-mesa-glx \
    libgl1-mesa-dri \
    wget \
    maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY TraficoInteligente/src /app/src
COPY pom.xml /app/pom.xml

RUN mvn clean compile

EXPOSE 8080

CMD ["mvn", "javafx:run"]

