package fr.ribesg.alix.internal.network;

import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;
import fr.ribesg.alix.api.message.IrcPacket;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestPrefixParser {

	private static final Server DUMMY_SERVER = new Server(null, null, 0);

	private final Source awaitedSource;
	private final String prefix;

	public TestPrefixParser(final Source awaitedSource, final String prefix) {
		this.awaitedSource = awaitedSource;
		this.prefix = prefix;
	}

	@Test
	public void testParseMessage() {
		final Source result = IrcPacket.parsePrefix(DUMMY_SERVER, this.prefix);
		Assert.assertEquals("Name doesn't match", awaitedSource.getName(), result.getName());
		Assert.assertEquals("Username doesn't match", awaitedSource.getUserName(), result.getUserName());
		Assert.assertEquals("Hostname doesn't match", awaitedSource.getHostName(), result.getHostName());
		Assert.assertEquals("isUser doesn't match", awaitedSource.isUser(), result.isUser());
	}

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		final Collection<Object[]> data = new ArrayList<>();
		data.add(new Object[] {
				new Source(DUMMY_SERVER, "Test", null, null, true),
				"Test"
		});
		data.add(new Object[] {
				new Source(DUMMY_SERVER, "irc.esper.net", null, "irc.esper.net", false),
				"irc.esper.net"
		});
		data.add(new Object[] {
				new Source(DUMMY_SERVER, "Ribesg", "Ribesg", "ribesg.fr", true),
				"Ribesg!Ribesg@ribesg.fr"
		});
		return data;
	}
}