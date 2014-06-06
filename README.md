Spring-integration project botstrapped with Spring-boot
=======================================================

SpringBoot application with spring-integration example with an MTOM enabled webservice endpoint.

It can handle large (hundreds of MBs) attachments, so far it only works with a CXF backed JAX-WS client.

To try out:
-----------

1. check out the project from GitHub
1. install JDK 1.7+ (Java7)
1. install Maven 3.0.3+
1. set JAVA_HOME environment variable to home of the installed JDK 1.7
1. go to the project directory and issue mvn clean spring-boot:run
1. open a browser and enter the following address: http://localhost:8080/contentStore.wsdl
1. use the WSDL to create a client of your preference

NOTE: When running the example (other than the test) don't forget to set *-Dsaaj.use.mimepull=true*
---------------------------------------------------------------------------------------------------
