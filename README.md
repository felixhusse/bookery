[![Build Status](https://travis-ci.org/felixhusse/bookery.svg?branch=master)](https://travis-ci.org/felixhusse/bookery)

# Bookery 
The name "bookery" is based on the german word "Buecherei", which can be translated to "library" but would result in a terrible grade in school.

## Features
* import books from your calibri library to a web app
* browse your books online
* download/transfer your books to your favourite reader

## Installation
* build with maven

### Setup Solr on Wildfly
* add a system property /system-property=solr.solr.home:add(value=e:/server/solr-home)
* Deploy Solr.war

### Setup JavaMail on Wildfly
* Define outbound-socket-binding named BookeryMailSMTP via jboss cli: /socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=BookeryMailSMTP:add(host=smtp.gmail.com,port=465)
* Define JavaMail session named BookeryMail via jboss cli: /subsystem=mail/mail-session=BookeryMail:add(jndi-name="java:/mail/bookeryMail", from="example.user@gmail.com", debug=true)
* Add reference from BookeryMail to BookeryMailSMTP socket: /subsystem=mail/mail-session=BookeryMail/server=smtp:add(outbound-socket-binding-ref=BookeryMailSMTP,ssl=true,username=example.user@gmail.com,password=***)

### Setup Datasource
* connect to jboss cli and add datasource via data-source add --name=bookeryDS --driver-name=h2 --jndi-name=java:jboss/datasources/bookeryDS --connection-url="jdbc:h2:${jboss.server.data.dir}/bookery;DB_CLOSE_DELAY=-1" --user-name=sa --password=sa --use-ccm=true --enabled=true


* set max-post-size in undertow subsystem to
<http-listener name="default" socket-binding="http" max-post-size="104857600"/> which means 100MB per request...
