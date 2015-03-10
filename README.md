# Bookery
The name "bookery" is based on the german word "Buecherei", which can be translated to "library".

## Features

## Installation
* build with maven
* connect to jboss cli and add datasource via data-source add --name=bookeryDS --driver-name=h2 --jndi-name=java:jboss/datasources/bookeryDS --connection-url="jdbc:h2:${jboss.server.data.dir}/bookery;DB_CLOSE_DELAY=-1" --user-name=sa --password=sa --use-ccm=true --enabled=true


* set max-post-size in undertow subsystem to
<http-listener name="default" socket-binding="http" max-post-size="104857600"/> which means 100MB per request...