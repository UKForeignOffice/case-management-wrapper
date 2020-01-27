FROM openjdk:8-alpine

ENV SPRING_PROFILES_ACTIVE production

CMD java -jar /data/app.jar

ADD build/libs/case-management-worker.jar /data/app.jar
