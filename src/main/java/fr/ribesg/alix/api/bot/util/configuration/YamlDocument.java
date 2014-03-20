package fr.ribesg.alix.api.bot.util.configuration;
import java.util.Map;

/** @author Ribesg */
public class YamlDocument extends ConfigurationSection {

	public YamlDocument() {
		super();
	}

	public YamlDocument(final Map<String, Object> contentMap) {
		super(contentMap);
	}

	public Map<String, Object> asMap() {
		return this.contentMap;
	}
}
