package sk.sivak.eldritchhorror.core.view.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class UiText {

    private static final String BUNDLE_NAME = "i18n.ui";
    private static final Locale LOCALE = resolveLocale();
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, LOCALE, new Utf8Control());

    private UiText() {
    }

    public static String get(String key, Object... args) {
        try {
            String value = BUNDLE.getString(key);
            if (args == null || args.length == 0) {
                return value;
            }
            return MessageFormat.format(value, args);
        } catch (MissingResourceException ex) {
            return "!" + key + "!";
        }
    }

    private static Locale resolveLocale() {
        String uiLanguage = System.getProperty("ui.language");
        if (uiLanguage != null && !uiLanguage.trim().isEmpty()) {
            return Locale.forLanguageTag(uiLanguage.trim());
        }
        return Locale.getDefault();
    }

    private static final class Utf8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            InputStream stream = loader.getResourceAsStream(resourceName);
            if (stream == null) {
                return null;
            }
            try (Reader reader = new InputStreamReader(stream, "UTF-8")) {
                return new PropertyResourceBundle(reader);
            }
        }
    }
}
