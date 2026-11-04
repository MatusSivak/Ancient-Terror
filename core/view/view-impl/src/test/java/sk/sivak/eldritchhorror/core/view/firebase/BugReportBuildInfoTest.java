package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import java.lang.reflect.Proxy;
import org.junit.Test;
import static org.junit.Assert.*;

public class BugReportBuildInfoTest {
    @Test public void platformVersionIncludesCiSuffixInsteadOfSharedAssetVersion() {
        String previous = System.getProperty("ancientterror.appVersion");
        Files files = Gdx.files;
        try {
            Gdx.files = null; // Actual platform version must not need the old asset.
            System.setProperty("ancientterror.appVersion", "1.7.17-ci.123.abcdef0");
            assertEquals("1.7.17-ci.123.abcdef0", BugReportBuildInfo.appVersion());
        } finally {
            Gdx.files = files;
            if (previous == null) System.clearProperty("ancientterror.appVersion");
            else System.setProperty("ancientterror.appVersion", previous);
        }
    }

    @Test public void desktopFallsBackToConfiguredVersionAndPackagesRevision() {
        String previous = System.getProperty("ancientterror.appVersion");
        Files files = Gdx.files;
        try {
            System.clearProperty("ancientterror.appVersion");
            Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(), new Class[]{Files.class},
                    (p, m, a) -> new FileHandle("unused") {
                        @Override public java.io.InputStream read() {
                            return new java.io.ByteArrayInputStream("{appVersion:1.7.17}".getBytes(java.nio.charset.StandardCharsets.UTF_8));
                        }
                    });
            assertEquals("1.7.17", BugReportBuildInfo.appVersion());
            assertNotNull(BugReportBuildInfo.class.getResource("/ancient-terror-build.properties"));
            assertFalse(BugReportBuildInfo.sourceRevision().isEmpty());
        } finally {
            Gdx.files = files;
            if (previous == null) System.clearProperty("ancientterror.appVersion");
            else System.setProperty("ancientterror.appVersion", previous);
        }
    }
}
