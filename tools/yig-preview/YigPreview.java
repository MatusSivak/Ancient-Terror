import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import sk.sivak.eldritchhorror.core.constants.ancientone.*;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.constants.monster.epic.*;
import sk.sivak.eldritchhorror.core.model.util.AncientOneHelper;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.YigCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.MonsterCard;
import sk.sivak.eldritchhorror.core.view.initgame.SelectAncientOneTable;

/** Offscreen production widgets; never opens or writes a saved game. */
public class YigPreview extends ApplicationAdapter {
    private Stage stage;
    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio=true;
        LwjglApplicationConfiguration config=new LwjglApplicationConfiguration();
        config.width=960;config.height=540;config.x=-10000;config.y=-10000;config.forceExit=false;
        new LwjglApplication(new YigPreview(),config);
    }
    @Override public void create() {
        try {
            VisUI.load();stage=new Stage(new FitViewport(960,540));
            YigCard card=new YigCard();card.init(AncientOneHelper.createYig());render(card,"yig-sleeping");
            AncientOneInfo awake=AncientOneHelper.createAwakenYig();awake.setPower(5);card.init(awake);render(card,"yig-awake");
            ScrollPane scroll=findScroll(card);scroll.setScrollPercentY(1);scroll.updateVisualScroll();draw("yig-awake-bottom");
            for(AbstractMonsterInfo monster:new AbstractMonsterInfo[]{new YigMonster(),new ChildrenOfYigMonster(),new WingedSerpentMonster()}) {
                monster.setToughness(monster instanceof YigMonster?5:4);monster.setCurrentHealth(monster.getToughness());
                MonsterCard monsterCard=new MonsterCard();monsterCard.init(monster,null);render(monsterCard,monster.getClass().getSimpleName());
            }
            SelectAncientOneTable selection=new SelectAncientOneTable(AncientOneHelper.initAvailableAncientOnes(),true,true,true,null);
            render(selection,"selection-top");
            java.lang.reflect.Method select=SelectAncientOneTable.class.getDeclaredMethod("select",AncientOneId.class);
            select.setAccessible(true);select.invoke(selection,AncientOneId.YIG);
            scroll=findScroll(selection);scroll.setScrollPercentY(1);scroll.updateVisualScroll();stage.act(0);draw("selection-yig");
            System.out.println("PASS: sleeping/awake Yig, counter, three epic monsters, and scrollable five-Ancient-One selector rendered.");
        } catch(Throwable failure) {failure.printStackTrace();System.exit(1);} finally {Gdx.app.exit();}
    }
    private void render(Table card,String name) throws Exception {
        stage.clear();stage.addActor(card);card.validate();
        if(card.getHeight()>520 || card.getWidth()>950)throw new AssertionError("Offscreen: "+name+" "+card.getWidth()+"x"+card.getHeight());
        card.setPosition((960-card.getWidth())/2,(540-card.getHeight())/2);stage.act(0);draw(name);
    }
    private ScrollPane findScroll(Group group) {
        for(Actor child:group.getChildren()) {
            if(child instanceof ScrollPane)return (ScrollPane)child;
            if(child instanceof Group){ScrollPane found=findScroll((Group)child);if(found!=null)return found;}
        }
        return null;
    }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.06f,.09f,.07f,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);stage.draw();
        Pixmap pixels=ScreenUtils.getFrameBufferPixmap(0,0,960,540);PixmapIO.PNG png=new PixmapIO.PNG();png.setFlipY(true);
        png.write(Gdx.files.local("../build/yig-preview/"+name+".png"),pixels);png.dispose();pixels.dispose();
    }
    @Override public void dispose(){if(stage!=null)stage.dispose();VisUI.dispose();}
}
