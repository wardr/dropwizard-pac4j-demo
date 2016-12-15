# dropwizard-pac4j-demo

This is a basic example of how to use pac4J with Dropwizard and a local database. This demo is currently under development.

The database schema is located in src/main/db/create.sql

To create a user, use the SQL: src/main/db/create_user.sql

To build the application: `mvn clean package`

To run it: `java -jar target/dropwizard-pac4j-demo-0.0.1-SNAPSHOT.jar server config/demo.yml`

