# Java Challenge

Just a java challenge for avenuecode

### Requirements

##### MySQL
If you don't have a MySQL server working, install it:<br /> https://dev.mysql.com/downloads/installer/

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

Go to in your root directory and clone the repository:

```
$> git clone https://gitlab.com/pedrovalmor/java-challenge-ac001.git
$> cd java-challenge-ac001
```

Then create a MySQL database and setup your host, user and password in the file  `src/main/resources/application.properties`.

```
How to create a MySQL database and user:
https://www.lanexa.net/2011/08/create-a-mysql-database-username-password-and-permissions-from-the-command-line/
```

Finally, run!

```
$> mvn spring-boot:run
```

Now you can call apis like that if your host is `localhost` and your port is `8080`:

```
$> curl -X POST -H "Content-Type: application/json" -d @data.json http://localhost:8080/graph

$> curl -X GET http://localhost:8080/graph/1

$> curl -X POST http://localhost:8080/routes/1/from/A/to/C?maxStops=3

$> curl -X POST -H "Content-Type: application/json" -d @path.json http://localhost:8080/distance/1

$> curl -X POST http://localhost:8080/distance/1/from/A/to/C
```
