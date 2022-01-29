FROM openjdk:latest
COPY ./target/seMethods-release-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "seMethods-release-jar-with-dependencies.jar"]

