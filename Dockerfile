FROM nginx

COPY target/watchlist-service-assembly-0.1.0-SNAPSHOT.jar /

RUN java -cp watchlist-service-assembly-0.1.0-SNAPSHOT.jar com.mod.watchlist.server.Bootstrap
