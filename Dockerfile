FROM gradle:jdk17
COPY ./server/ssh/id_rsa /root/.ssh/id_rsa
WORKDIR /terminal-bot
COPY build.gradle .
COPY ./src ./src
ENTRYPOINT ["gradle", "run"]
