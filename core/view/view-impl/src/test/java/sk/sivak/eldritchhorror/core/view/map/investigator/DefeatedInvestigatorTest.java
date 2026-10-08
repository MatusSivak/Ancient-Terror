package sk.sivak.eldritchhorror.core.view.map.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import java.lang.reflect.Proxy;
import java.util.*;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import static org.junit.Assert.*;

public class DefeatedInvestigatorTest {
    @Test public void defeatedStandsUseOnlyExistingDrawablesAndPreserveActorColor() {
        for (boolean health : new boolean[]{true, false}) {
            List<Color> draws = new ArrayList<>();
            Color batchColor = new Color();
            Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(), new Class[]{Batch.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("setColor")) {
                            if (args.length == 1) batchColor.set((Color) args[0]);
                            else batchColor.set((Float) args[0], (Float) args[1], (Float) args[2], (Float) args[3]);
                        }
                        if (method.getName().equals("getColor")) return batchColor;
                        return null;
                    });
            BaseDrawable drawable = new BaseDrawable() {
                @Override public void draw(Batch b, float x, float y, float width, float height) {
                    draws.add(new Color(batchColor));
                }
            };
            InvestigatorImage stand = new InvestigatorImage(InvestigatorId.THE_SAILOR, null, drawable, drawable);
            Group group = new Group(); group.addActor(stand);
            stand.setColor(.8f, .8f, .8f, .6f);
            Color original = new Color(stand.getColor());
            stand.setDefeatedByHealth(health);
            for (int frame = 0; frame < 120; frame++) {
                group.act(1f / 60f);
                stand.draw(batch, .5f);
                assertEquals(original, stand.getColor());
            }
            assertEquals(240, draws.size()); // One border and one portrait per frame.
            Color portrait = draws.get(1);
            assertEquals(.3f, portrait.a, .0001f);
            assertTrue(health ? portrait.r > portrait.b : portrait.b > portrait.r);
            assertEquals(1, group.getChildren().size);
            assertEquals(0, stand.getActions().size);
        }
    }

    @Test public void removingDefeatedStandCompletesOnceAndRemovesActor() {
        InvestigatorImage stand = new InvestigatorImage(InvestigatorId.THE_SAILOR, null,
                new BaseDrawable(), new BaseDrawable());
        Group group = new Group(); group.addActor(stand);
        stand.setDefeatedByHealth(true);
        int[] completions = {0};
        stand.fadeOutDefeated().subscribe(() -> completions[0]++);
        assertEquals(0, completions[0]);
        for (int frame = 0; frame < 30; frame++) group.act(1f / 60f);
        assertEquals(1, completions[0]);
        assertNull(stand.getParent());
        assertEquals(0, group.getChildren().size);
    }
}
