FROM adoptopenjdk/openjdk15:ubi
COPY /build/libs/tourGuide-1.0.0.jar tourguideTour.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "tourguideTour.jar"]