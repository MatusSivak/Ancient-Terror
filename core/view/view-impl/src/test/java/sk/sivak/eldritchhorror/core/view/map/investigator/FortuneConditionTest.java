package sk.sivak.eldritchhorror.core.view.map.investigator;

import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import java.lang.reflect.Proxy;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.controller.GameController;
import static org.junit.Assert.*;

public class FortuneConditionTest {
    @Test public void followsConditionOwnershipOnLoadGainDiscardAndTransfer() {
        InvestigatorId[] blessed = {InvestigatorId.THE_SAILOR};
        InvestigatorId[] cursed = {null};
        GameController controller = (GameController) Proxy.newProxyInstance(GameController.class.getClassLoader(),
                new Class[]{GameController.class}, (proxy, method, args) -> {
                    if (method.getName().equals("isInvestigatorBlessed")) return args[0] == blessed[0];
                    if (method.getName().equals("isInvestigatorCursed")) return args[0] == cursed[0];
                    if (method.getReturnType() == boolean.class) return false;
                    return null;
                });
        InvestigatorImage sailor = stand(InvestigatorId.THE_SAILOR, controller);
        InvestigatorImage politician = stand(InvestigatorId.THE_POLITICIAN, controller);
        sailor.act(.1f); politician.act(.1f);
        assertTrue(sailor.isBlessedVisible());
        assertFalse(politician.isBlessedVisible());
        blessed[0] = InvestigatorId.THE_POLITICIAN;
        cursed[0] = InvestigatorId.THE_SAILOR;
        sailor.act(.1f); politician.act(.1f);
        assertFalse(sailor.isBlessedVisible());
        assertTrue(sailor.isCursedVisible());
        assertTrue(politician.isBlessedVisible());
        cursed[0] = null;
        sailor.act(.1f);
        assertFalse(sailor.isCursedVisible());
        politician.setDefeatedByHealth(true);
        assertFalse(politician.isBlessedVisible());
        politician.act(.1f);
        assertFalse(politician.isBlessedVisible());
    }

    private InvestigatorImage stand(InvestigatorId id, GameController controller) {
        return new InvestigatorImage(id, controller, new BaseDrawable(), new BaseDrawable());
    }
}
