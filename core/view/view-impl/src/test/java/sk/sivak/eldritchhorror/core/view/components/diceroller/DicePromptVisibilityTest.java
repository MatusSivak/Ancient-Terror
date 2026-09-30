package sk.sivak.eldritchhorror.core.view.components.diceroller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

import static org.junit.Assert.*;

public class DicePromptVisibilityTest {
    private GL20 previousGl;
    private Texture texture;

    @Before
    public void setUp() {
        previousGl = Gdx.gl;
        Gdx.gl = (GL20) Proxy.newProxyInstance(GL20.class.getClassLoader(),
                new Class<?>[]{GL20.class}, (proxy, method, args) ->
                        method.getReturnType() == int.class ? 1 : null);
        texture = new Texture(new TextureData() {
            public TextureDataType getType() { return TextureDataType.Custom; }
            public boolean isPrepared() { return true; }
            public void prepare() { }
            public Pixmap consumePixmap() { throw new UnsupportedOperationException(); }
            public boolean disposePixmap() { return false; }
            public void consumeCustomData(int target) { }
            public int getWidth() { return 160; }
            public int getHeight() { return 90; }
            public Pixmap.Format getFormat() { return Pixmap.Format.RGBA8888; }
            public boolean useMipMaps() { return false; }
            public boolean isManaged() { return false; }
        });
    }

    @After
    public void tearDown() {
        Gdx.gl = previousGl;
    }

    @Test
    public void promptHidesZeroSuccessRollWithoutLosingDiceOrPendingMovement() throws Exception {
        DiceRoller roller = new DiceRoller();
        Group layer = new Group();
        for (int i = 0; i < 3; i++) {
            DiceImage die = addDie(roller, layer, i, DiceRoll.Score.BAD);
            die.setPosition(0, 0);
            die.addAction(Actions.moveTo(100, 0, 1f));
        }

        roller.setDiceVisible(false);
        layer.act(0.5f);
        for (DiceImage die : dice(roller)) {
            assertFalse(die.isVisible());
            assertSame(layer, die.getParent());
            assertEquals(DiceRoll.Score.BAD, die.getScore());
            assertEquals(50f, die.getX(), 0.001f);
        }

        roller.setDiceVisible(true);
        layer.act(0.5f);
        for (DiceImage die : dice(roller)) {
            assertTrue(die.isVisible());
            assertEquals(100f, die.getX(), 0.001f);
            assertEquals(0, die.getActions().size);
            assertEquals(2, die.getDiceValue());
        }
    }

    @Test
    public void promptHidesAllScoresInCurrentRollAndLeavesOtherRollsAlone() throws Exception {
        DiceRoller roller = new DiceRoller();
        DiceRoller otherRoller = new DiceRoller();
        Group layer = new Group();
        for (DiceRoll.Score score : DiceRoll.Score.values()) {
            addDie(roller, layer, score.ordinal(), score);
        }
        DiceImage otherDie = addDie(otherRoller, layer, 0, DiceRoll.Score.GOOD);

        roller.setDiceVisible(false);
        for (DiceImage die : dice(roller)) assertFalse(die.isVisible());
        assertTrue(otherDie.isVisible());
        assertEquals(4, layer.getChildren().size);

        roller.setDiceVisible(true);
        for (DiceImage die : dice(roller)) assertTrue(die.isVisible());
    }

    private DiceImage addDie(DiceRoller roller, Group layer, int number, DiceRoll.Score score) throws Exception {
        DiceImage die = new DiceImage(texture, 2);
        die.setDiceNumber(number);
        die.setScore(score);
        layer.addActor(die);
        dice(roller).add(die);
        return die;
    }

    @SuppressWarnings("unchecked")
    private List<DiceImage> dice(DiceRoller roller) throws Exception {
        Field field = DiceRoller.class.getDeclaredField("dices");
        field.setAccessible(true);
        return (List<DiceImage>) field.get(roller);
    }
}
