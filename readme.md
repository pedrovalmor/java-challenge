# Java Challenge

Just a java challenge for avenuecode

### Requirements

##### MySQL
If you don't have a MySQL server working, install it:<br /> https://dev.mysql.com/downloads/installer/

Then create a database with the name `spring` or modify the file  `src/main/resources/application.properties` as you want.

##### Git
If you don't have it, install it:<br />
https://git-scm.com/downloads

##### Maven
If you don't have it, install it:<br />
https://maven.apache.org/install.html

##### cURL
If you don't have it, install it:<br />
https://curl.haxx.se/download.html

### How to work

Go to in your root directory, clone the repository and start the server:

```
$> git clone https://gitlab.com/pedrovalmor/java-challenge-ac001.git
$> cd java-challenge-ac001
$> mvn spring-boot:run
```

Then you can call apis like that if your host is `localhost` and your port is `8080`:

```
$> curl -X POST -H "Content-Type: application/json" -d @data.json http://localhost:8080/graph

$> curl -X GET http://localhost:8080/graph/1

$> curl -X POST http://localhost:8080/routes/1/from/A/to/C?maxStops=3

$> curl -X POST -H "Content-Type: application/json" -d @path.json http://localhost:8080/distance/1

$> curl -X POST http://localhost:8080/distance/1/from/A/to/C
```

That's it ;)
