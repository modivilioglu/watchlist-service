FROM nginx
WORKDIR /var/jenkins_home/workspace/watchlist-service/target/scala-2.12
COPY watchlist-service-assembly-0.1.0-SNAPSHOT.jar /
RUN pwd
RUN java -cp watchlist-service-assembly-0.1.0-SNAPSHOT.jar com.mod.watchlist.server.Bootstrap
