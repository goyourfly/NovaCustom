# seamless

This is a fork from 4th line seamless project https://github.com/4thline/seamless intended for Nova Video Player use only.

The goal of this fork is to be able to compile seamless-util seamless-http seamless-xml and seamless-swing jars for cling-core and cling-support usage with a minimum of dependencies and no link to 4thline repositories.

As a consequence all the changes made are potentially breaking seamless other modules.

Compilation requires java8 and can be achieved via:
```
JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 mvn clean install -Dmaven.source.skip -DskipTests -Dmaven.javadoc.skip=true
```
