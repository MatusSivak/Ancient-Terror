package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

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
    private static final String PREFERENCES_NAME = "AncientTerror.xml";
    private static final String PREFERENCE_UI_LANGUAGE = "ui.language";
    private static final String DEFAULT_LANGUAGE = "en";
    private static volatile Locale locale = resolveLocale();
    private static volatile ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, new Utf8Control());

    private UiText() {
    }

    public static String get(String key, Object... args) {
        try {
            String value = bundle.getString(key);
            if (args == null || args.length == 0) {
                return value;
            }
            return MessageFormat.format(value, args);
        } catch (MissingResourceException ex) {
            return "!" + key + "!";
        }
    }

    public static synchronized void setLanguage(String languageTag) {
        if (languageTag == null || languageTag.trim().isEmpty()) {
            return;
        }
        Locale requestedLocale = Locale.forLanguageTag(languageTag.trim());
        locale = requestedLocale;
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, new Utf8Control());
        System.setProperty("ui.language", locale.toLanguageTag());
        if (Gdx.app != null) {
            Preferences preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
            preferences.putString(PREFERENCE_UI_LANGUAGE, locale.getLanguage());
            preferences.flush();
        }
    }

    public static String getLanguage() {
        return locale.getLanguage();
    }

    private static Locale resolveLocale() {
        String uiLanguage = System.getProperty("ui.language");
        if (uiLanguage != null && !uiLanguage.trim().isEmpty()) {
            return Locale.forLanguageTag(uiLanguage.trim());
        }
        if (Gdx.app != null) {
            Preferences preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
            String preferenceLanguage = preferences.getString(PREFERENCE_UI_LANGUAGE, DEFAULT_LANGUAGE);
            if (preferenceLanguage != null && !preferenceLanguage.trim().isEmpty()) {
                return Locale.forLanguageTag(preferenceLanguage.trim());
            }
        }
        return Locale.forLanguageTag(DEFAULT_LANGUAGE);
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
