# dropwizard-pac4j-demo

This is a basic example of how to use pac4J with Dropwizard and a local database. This demo is currently under development.

To build the application: `mvn clean package`

To initialize (or update) the database: `java -jar target/dropwizard-pac4j-demo-0.0.1-SNAPSHOT.jar db migrate config/demo.yml`

To run it: `java -jar target/dropwizard-pac4j-demo-0.0.1-SNAPSHOT.jar server config/demo.yml`

