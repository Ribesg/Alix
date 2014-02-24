Alix
====

A Java IRC API

Documentation on IRCv3 for future: http://ircv3.atheme.org/

How to maven:
```xml
	...
	<repositories>
		<repository>
			<id>ribesg-releases</id>
			<name>Ribesg's Release Repository</name>
			<url>http://repo.ribesg.fr/content/repositories/releases</url>
		</repository>
	</repositories>

	<dependencies>
		<dependency>
			<groupId>fr.ribesg.alix</groupId>
			<artifactId>alix</artifactId>
			<version>0.2</version>
		</dependency>
	</dependencies>
	...
```

Then just read the [fr.ribesg.alix.api.Client](https://github.com/Ribesg/Alix/blob/master/src/main/java/fr/ribesg/alix/api/Client.java) class, and extend it !  
There's an example client: [fr.ribesg.alix.TestClient](https://github.com/Ribesg/Alix/blob/master/src/main/java/fr/ribesg/alix/TestClient.java)
