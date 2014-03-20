package fr.ribesg.alix.api.bot.util.configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** @author Ribesg */
public class YamlFile {

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	private static Yaml yaml;

	private static Yaml getYaml() {
		if (yaml == null) {
			final DumperOptions options = new DumperOptions();
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
			yaml = new Yaml(options);
		}
		return yaml;
	}

	private final List<YamlDocument> documents;

	public YamlFile() {
		this.documents = new ArrayList<>();
	}

	public List<YamlDocument> getDocuments() {
		return documents;
	}

	public void load() throws IOException {
		this.load("alix.yml");
	}

	protected void load(final String filePath) throws IOException {
		final Path path = Paths.get(filePath);
		if (!Files.exists(path)) {
			Files.createFile(path);
		} else {
			try (BufferedReader reader = Files.newBufferedReader(path, CHARSET)) {
				final StringBuilder s = new StringBuilder();
				while (reader.ready()) {
					s.append(reader.readLine()).append('\n');
				}
				this.loadFromString(s.toString());
			}
		}
	}

	protected void loadFromString(final String yamlFileContent) {
		final Iterable<Object> documents = getYaml().loadAll(yamlFileContent);
		for (final Object o : documents) {
			@SuppressWarnings("unchecked")
			final Map<String, Object> documentMap = (Map<String, Object>) o;
			this.documents.add(new YamlDocument(documentMap));
		}
	}

	public void save() throws IOException {
		this.save("alix.yml");
	}

	protected void save(final String filePath) throws IOException {
		final Path path = Paths.get(filePath);
		try (BufferedWriter writer = Files.newBufferedWriter(path, CHARSET, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			writer.write(saveToString());
		}
	}

	protected String saveToString() {
		final List<Map<String, Object>> rawContentMap = new ArrayList<>();
		for (final YamlDocument document : this.documents) {
			rawContentMap.add(document.asMap());
		}
		return getYaml().dumpAll(rawContentMap.iterator());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof YamlFile)) {
			return false;
		}

		final YamlFile yamlFile = (YamlFile) o;

		if (!documents.equals(yamlFile.documents)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return documents.hashCode();
	}
}
