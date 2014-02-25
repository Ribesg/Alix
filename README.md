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
And if you want to play with Snapshot versions, you can just use the Snapshot repo. It's convenient to keep both if you're in this case:
```xml
	...
	<repositories>
		<repository>
			<id>ribesg-releases</id>
			<name>Ribesg's Release Repository</name>
			<url>http://repo.ribesg.fr/content/repositories/releases</url>
		</repository>
		<repository>
			<id>ribesg-snapshots</id>
			<name>Ribesg's Snapshot Repository</name>
			<url>http://repo.ribesg.fr/content/repositories/snapshots</url>
		</repository>
	</repositories>

	<dependencies>
		<dependency>
			<groupId>fr.ribesg.alix</groupId>
			<artifactId>alix</artifactId>
			<version>0.3-SNAPSHOT</version>
		</dependency>
	</dependencies>
	...
```

Then just read the [fr.ribesg.alix.api.Client](https://github.com/Ribesg/Alix/blob/master/src/main/java/fr/ribesg/alix/api/Client.java) class, and extend it !  
There's an example client: [fr.ribesg.alix.TestClient](https://github.com/Ribesg/Alix/blob/master/src/main/java/fr/ribesg/alix/TestClient.java)

FAQ
===

Hey something is not in the API, what do I do?
----------------------------------------------
There are 2 solutions:
  1. Fork the repository on Github, do it yourself in a cool way and create a Pull Request! Also add some links to documentation/specifications about what you implemented.
  2. Create an Issue with tons of details about what you want, why you want it, links to RFCs or other documentations, etc. Then wait for somebody to implement it in the previously stated cool way!

In both cases, you can make a workaround using [Client#onRawIrcMessage(Server, Message)](https://github.com/Ribesg/Alix/blob/master/src/main/java/fr/ribesg/alix/api/Client.java#L178) while waiting for your feature to be merged/implemented in the project!
