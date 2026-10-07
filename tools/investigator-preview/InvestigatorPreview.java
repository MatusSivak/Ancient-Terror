import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.view.components.investigator.InvestigatorSketch;
import sk.sivak.eldritchhorror.core.view.initgame.FullGamePurchaseDialog;
import sk.sivak.eldritchhorror.core.view.utils.UiText;

/** Production widgets rendered offscreen; never loads a save or buys anything. */
public class InvestigatorPreview extends ApplicationAdapter {
    private Stage stage;
    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio=true;
        LwjglApplicationConfiguration c=new LwjglApplicationConfiguration();
        c.width=960;c.height=540;c.x=-10000;c.y=-10000;c.forceExit=false;
        new LwjglApplication(new InvestigatorPreview(),c);
    }
    @Override public void create() {
        try {
            VisUI.load();stage=new Stage(new FitViewport(960,540));
            Table row=new Table();row.setFillParent(true);
            for(InvestigatorId id:new InvestigatorId[]{InvestigatorId.THE_BUTLER,InvestigatorId.THE_PRIEST,InvestigatorId.THE_NUN,InvestigatorId.THE_EXPLORER}){
                Table column=new Table();
                InvestigatorSketch locked=new InvestigatorSketch(id);locked.showLocked();
                column.add(locked).size(148,180).padBottom(8).row();
                InvestigatorSketch unlocked=new InvestigatorSketch(id);
                column.add(unlocked).size(148,180);
                row.add(column).pad(10);
                String title=UiText.get("investigator.profession."+id.name().toLowerCase(java.util.Locale.ROOT));
                if(title.contains("investigator.profession"))throw new AssertionError("Missing title "+id);
            }
            stage.addActor(row);stage.act(0);draw("locked-and-unlocked");
            stage.clear();FullGamePurchaseDialog.show(stage,()->{});
            for(int i=0;i<60;i++)stage.act(1f/60f);
            draw("full-game-offer");
            System.out.println("PASS: production portraits, lock overlays, labels and eight-investigator offer rendered.");
        } catch(Throwable failure){failure.printStackTrace();System.exit(1);}
        finally{Gdx.app.exit();}
    }
    private void draw(String name)throws Exception{
        Gdx.gl.glClearColor(.06f,.09f,.07f,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);stage.draw();
        Pixmap pixels=ScreenUtils.getFrameBufferPixmap(0,0,960,540);
        PixmapIO.PNG png=new PixmapIO.PNG();png.setFlipY(true);
        png.write(Gdx.files.local("../build/investigator-preview/"+name+".png"),pixels);
        png.dispose();pixels.dispose();
    }
    @Override public void dispose(){if(stage!=null)stage.dispose();VisUI.dispose();}
}

