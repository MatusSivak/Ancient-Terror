package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** Keyed text for event-listener prompts; localized keys override the English base file. */
public final class LocalizedText extends EncounterTextBuilder {

    private static final Logger logger = LogManager.getLogger(LocalizedText.class);
    private static final Map<String, Properties> CACHE = new HashMap<>();

    private LocalizedText() {
    }

    public static synchronized String get(String fileName, String key, Object... args) {
        String language = currentLanguage();
        String cacheKey = fileName + ":" + language;
        Properties properties = CACHE.get(cacheKey);
        if (properties == null) {
            properties = load(fileName);
            if (properties == null) {
                properties = new Properties();
            }
            int extension = fileName.lastIndexOf('.');
            if (!language.isEmpty() && extension >= 0) {
                Properties localized = load(fileName.substring(0, extension) + "_" + language + fileName.substring(extension));
                if (localized != null) {
                    properties.putAll(localized);
                }
            }
            CACHE.put(cacheKey, properties);
        }
        String value = properties.getProperty(key);
        if (value == null) {
            logger.error("Property not found for key: '" + key + "' in " + fileName);
            return key;
        }
        // Plain placeholder substitution: MessageFormat would treat apostrophes (K'n-yan) as quotes.
        for (int i = 0; args != null && i < args.length; i++) {
            value = value.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return value;
    }

    private static Properties load(String fileName) {
        Properties properties = new Properties();
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if (stream == null) {
            return null;
        }
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return properties;
    }
}
