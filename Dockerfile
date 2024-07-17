FROM eclipse-temurin:22-jdk-jammy
RUN apt-get update && apt-get install -y maven
RUN mkdir /opt/app
WORKDIR /opt/app
COPY . /opt/app/source
RUN cd source && mvn clean compile package -DskipTests -Dton_branch=testnet && mv target/MyLocalTon.jar /opt/app/mylocalton.jar
RUN rm -rf source
RUN mkdir /opt/app/myLocalTon
VOLUME /opt/app/myLocalTon

CMD ["java", "-jar", "/opt/app/mylocalton.jar", "nogui"]
