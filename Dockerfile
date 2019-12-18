FROM openjdk:8u201-jdk-alpine3.9
ADD target/e2d_cloudride-0.0.1-SNAPSHOT.jar .
EXPOSE 8082
CMD java -jar e2d_cloudride-0.0.1-SNAPSHOT.jar cloudride