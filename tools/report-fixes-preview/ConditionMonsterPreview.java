import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.Proxy;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.MonsterCard;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;

/** Offscreen production rendering; never reads or writes a game save. */
public class ConditionMonsterPreview extends ApplicationAdapter {
    private Stage stage;
    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000; config.forceExit = false;
        new LwjglApplication(new ConditionMonsterPreview(), config);
    }
    @Override public void create() {
        try {
            VisUI.load(); stage = new Stage(new FitViewport(960, 540));
            for (int i = 0; i < 4; i++) {
                final int condition = i;
                GameController controller = (GameController) Proxy.newProxyInstance(GameController.class.getClassLoader(),
                        new Class[]{GameController.class}, (p, m, a) -> {
                            if (m.getName().equals("isInvestigatorBlessed")) return condition == 0 || condition == 2;
                            if (m.getName().equals("isInvestigatorCursed")) return condition == 1 || condition == 3;
                            if (m.getName().equals("isInvestigatorPoisoned")) return condition >= 2;
                            return m.getReturnType() == boolean.class ? false : null;
                        });
                InvestigatorImage stand = new InvestigatorImage(InvestigatorId.THE_SAILOR, controller);
                stand.setPosition(95 + i * 220, 180); stand.setScale(1.8f); stage.addActor(stand);
            }
            for (int frame = 0; frame < 240; frame++) {
                stage.act(1f / 60f);
                if (frame == 30 || frame == 120 || frame == 216) draw("fortune-" + frame);
            }
            stage.clear();
            for (AbstractMonsterInfo info : new AbstractMonsterInfo[]{new ManiacMonster(), new DholeMonster(),
                    new ColourOutOfSpaceMonster(), new VampireMonster(), new RiotMonster()}) {
                MonsterCard card = new MonsterCard(); card.init(info, null);
                card.setPosition((960 - card.getWidth()) / 2, (540 - card.getHeight()) / 2);
                stage.addActor(card); stage.act(.1f); draw(info.getClass().getSimpleName()); stage.clear();
            }
            System.out.println("PASS: condition animation frames, poison coexistence and monster glyph sheets rendered");
        } catch (Throwable error) { error.printStackTrace(); System.exit(1); }
        finally { Gdx.app.exit(); }
    }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.12f, .16f, .13f, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/condition-monster-preview/" + name + ".png"), pixels);
        png.dispose(); pixels.dispose();
    }
    @Override public void dispose() { if (stage != null) stage.dispose(); }
}
