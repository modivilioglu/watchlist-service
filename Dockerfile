FROM nginx

COPY target/scala-2.12/watchlist-service-assembly-0.1.0-SNAPSHOT.jar /

RUN java -cp watchlist-service-assembly-0.1.0-SNAPSHOT.jar com.mod.watchlist.server.Bootstrap
