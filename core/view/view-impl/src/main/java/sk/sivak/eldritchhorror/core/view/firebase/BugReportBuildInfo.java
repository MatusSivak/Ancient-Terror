package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/** Platform build identity wins over the legacy shared Firebase configuration. */
final class BugReportBuildInfo {
    private BugReportBuildInfo() { }

    static String appVersion() {
        String platformVersion = System.getProperty("ancientterror.appVersion");
        if (platformVersion != null && !platformVersion.trim().isEmpty()) return platformVersion;
        return new JsonReader().parse(Gdx.files.internal("bug-report-firebase.json"))
                .getString("appVersion", "unknown");
    }

    static String sourceRevision() {
        try (InputStream stream = BugReportBuildInfo.class.getResourceAsStream("/ancient-terror-build.properties")) {
            if (stream == null) return "unknown";
            Properties properties = new Properties();
            properties.load(stream);
            return properties.getProperty("sourceRevision", "unknown");
        } catch (IOException error) { return "unknown"; }
    }
}
