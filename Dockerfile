FROM nginx
RUN echo $PWD
COPY /var/jenkins_home/workspace/watchlist-service/target/scala-2.12/watchlist-service-assembly-0.1.0-SNAPSHOT.jar ~/
RUN echo $PWD
RUN java -cp watchlist-service-assembly-0.1.0-SNAPSHOT.jar com.mod.watchlist.server.Bootstrap
