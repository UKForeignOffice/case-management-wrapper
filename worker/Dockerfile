FROM java:8

ENV SPRING_PROFILES_ACTIVE production

CMD java -jar /data/app.jar

ADD build/libs/case-management-worker.jar /data/app.jar
