FROM openjdk:8-alpine

ENV SPRING_PROFILES_ACTIVE production

EXPOSE 9000

CMD java -jar /data/app.jar

ADD build/libs/case-management-api.jar /data/app.jar
