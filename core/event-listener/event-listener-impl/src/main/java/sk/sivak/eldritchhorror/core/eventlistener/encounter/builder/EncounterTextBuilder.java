package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class EncounterTextBuilder {

    private static final Logger logger = LogManager.getLogger(EncounterTextBuilder.class);

    protected Properties properties;

    private String resourceKey = "";

    protected static Map<String, Properties> propertiesMap = new HashMap<>();
    protected int page;

    protected void resetKey() {
        resourceKey = "";
        withPage(page);
    }

    protected void withPage(int page) {
        appendToResourceKey(String.valueOf(page));
    }

    public EncounterTextBuilder withFlavor() {
        appendToResourceKey(".flavor");
        return this;
    }

    public EncounterTextBuilder withFlavor(int flavorNr) {
        appendToResourceKey(".flavor." + flavorNr);
        return this;
    }

    public EncounterTextBuilder withPass() {
        appendToResourceKey(".pass");
        return this;
    }

    public EncounterTextBuilder withFail() {
        appendToResourceKey(".fail");
        return this;
    }

    public EncounterTextBuilder withOption(int optionNr) {
        appendToResourceKey(".option."+optionNr);
        return this;
    }

    public EncounterTextBuilder withInfo() {
        appendToResourceKey(".info");
        return this;
    }

    public EncounterTextBuilder withInfo(int infoNr) {
        appendToResourceKey(".info."+infoNr);
        return this;
    }

    public EncounterTextBuilder withQuestion() {
        appendToResourceKey(".question");
        return this;
    }

    void appendToResourceKey(String value) {
        resourceKey = resourceKey + value;
    }

    public String build() {
        String resourceKey = getResourceKey();
        String property = properties.getProperty(resourceKey);
        if (property == null) {
            logger.error("Property not found for key: '"+resourceKey+"'");
        }
        resetKey();
        return property;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    protected static Properties loadPropertiesWithLocale(String fileName) {
        URL resource = getLocalizedResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("Resource was null for file: " + fileName);
        }
        Properties loadedProperties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8)) {
            loadedProperties.load(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return loadedProperties;
    }

    private static URL getLocalizedResource(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String language = Locale.getDefault().getLanguage();
        if (language != null && !language.trim().isEmpty()) {
            int extension = fileName.lastIndexOf('.');
            String localizedName = extension >= 0
                    ? fileName.substring(0, extension) + "_" + language + fileName.substring(extension)
                    : fileName + "_" + language;
            URL localizedResource = classLoader.getResource(localizedName);
            if (localizedResource != null) {
                return localizedResource;
            }
        }
        return classLoader.getResource(fileName);
    }
}
