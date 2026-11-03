package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.lang.reflect.Proxy;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.model.Investigators;
import sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveData;
import static org.junit.Assert.*;

public class SaveLoadCompatibilityTest {
    @Test public void olderSavesIgnoreRetiredFieldsAndRewriteOnlyCurrentState() {
        Files previous = Gdx.files;
        String[] contents = {null};
        FileHandle file = new FileHandle("memory-save.json") {
            @Override public boolean exists() { return contents[0] != null; }
            @Override public String readString() { return contents[0]; }
            @Override public void writeString(String text, boolean append) { contents[0] = text; }
        };
        Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(), new Class[]{Files.class},
                (proxy, method, args) -> file);
        try {
            Investigators investigators = new Investigators();
            investigators.initAvailableInvestigators(true);
            investigators.initWithPlayers(1);
            InvestigatorInfo info = investigators.getAvailableInvestigators().stream()
                    .filter(i -> i.getInvestigatorId() == InvestigatorId.THE_SPY).findFirst().get();
            investigators.initSelectedInvestigators(new InvestigatorInfo[]{info});
            SaveLoadGameHelper helper = new SaveLoadGameHelper();
            helper.save(investigators.save());
            contents[0] = contents[0].replace("health:", "retiredRoleFlag:true,health:");
            assertTrue(contents[0].contains("retiredRoleFlag"));
            InvestigatorsSaveData saved = helper.load(InvestigatorsSaveData.class);
            assertNotNull(saved);
            Investigators restored = new Investigators();
            restored.load(saved, true);
            restored.initActiveInvestigator();
            assertEquals(InvestigatorId.THE_SPY, restored.getActiveInvestigatorId());
            assertEquals(investigators.getInvestigatorIdsInTurnOrder(), restored.getInvestigatorIdsInTurnOrder());
            helper.save(restored.save());
            assertFalse(contents[0].contains("retiredRoleFlag"));
        } finally { Gdx.files = previous; }
    }
}
