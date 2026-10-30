import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AxeAsset;
import sk.sivak.eldritchhorror.core.constants.spell.fleshward.FleshWardSpell;
import sk.sivak.eldritchhorror.core.constants.condition.blessed.BlessedCondition;
import sk.sivak.eldritchhorror.core.constants.artifact.TheSilverKeyArtifact;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.model.util.InvestigatorsHelper;
import sk.sivak.eldritchhorror.core.view.asset.AssetView;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.investigator.SelectMultipleInvestigators;
import sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage;
import sk.sivak.eldritchhorror.core.view.question.QuestionView;
import sk.sivak.eldritchhorror.core.view.test.TestResultTable;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.utils.*;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Render the requested UI changes without loading a save or modifying purchase preferences. */
public class PresentationPreview extends ApplicationAdapter {
    private Stage stage;
    private LwjglApplication application;
    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio=true;
        LwjglApplicationConfiguration c=new LwjglApplicationConfiguration();
        c.width=960;c.height=540;c.x=-10000;c.y=-10000;c.forceExit=false;
        new LwjglApplication(new PresentationPreview(),c);
    }
    public void create() {
        try {
            Application real=Gdx.app;
            application=(LwjglApplication)real;
            Preferences prefs=(Preferences)Proxy.newProxyInstance(Preferences.class.getClassLoader(),new Class[]{Preferences.class},(p,m,a)-> {
                if(m.getName().startsWith("get")) {
                    if(a!=null&&a.length==2)return a[1];
                    if(m.getReturnType()==boolean.class)return false;
                    if(m.getReturnType()==int.class)return 0;
                    if(m.getReturnType()==String.class)return "";
                }
                if(m.getReturnType()==boolean.class)return false;
                return m.getReturnType()==Preferences.class?p:null;
            });
            Gdx.app=(Application)Proxy.newProxyInstance(Application.class.getClassLoader(),new Class[]{Application.class},
                    (p,m,a)->m.getName().equals("getPreferences")?prefs:m.invoke(real,a));
            Sound silent=(Sound)Proxy.newProxyInstance(Sound.class.getClassLoader(),new Class[]{Sound.class},(p,m,a)->m.getReturnType()==long.class?0L:null);
            Gdx.audio=(Audio)Proxy.newProxyInstance(Audio.class.getClassLoader(),new Class[]{Audio.class},(p,m,a)->silent);
            VisUI.load();stage=InfoStage.getStageSafe();
            GameController controller=(GameController)Proxy.newProxyInstance(GameController.class.getClassLoader(),new Class[]{GameController.class},(p,m,a)->null);
            new HudButtons(controller).init();
            Image bg=new Image(getTexture("encounter/background/mythos.jpg"));bg.setBounds(0,0,960,540);bg.setColor(.4f,.4f,.4f,1);
            stage.getRoot().addActorAt(0,bg);
            for(CardInfo card:new CardInfo[]{new AxeAsset(),new FleshWardSpell(),new BlessedCondition(),new TheSilverKeyArtifact()})
                for(boolean fast:new boolean[]{false,true})checkGain(card,fast);
            checkPrompt(false);checkPrompt(true);
            checkSelection();
            System.out.println("PASS: four card gain types; no full spin; glow cleanup; normal/fast; defensive prompts hide and restore dice/results; investigator action glyphs");
        } catch(Throwable t){t.printStackTrace();System.exit(1);}
        finally{FastForwardAction.turnOff();Gdx.app.exit();}
    }
    private void checkGain(CardInfo info,boolean fast)throws Exception {
        FastForwardAction.turnOff();InfoStage.setBottomHeight(5);
        AssetView view=new AssetView();AtomicInteger done=new AtomicInteger();
        view.gainCard(info).subscribe(done::incrementAndGet);
        Actor card=(Actor)field(view,"cardTemplate");
        // create() does not advance the desktop frame ID; pump async artwork without advancing the reveal.
        Method get=CustomAssetManager.class.getDeclaredMethod("get");get.setAccessible(true);
        com.badlogic.gdx.assets.AssetManager assets=(com.badlogic.gdx.assets.AssetManager)get.invoke(null);
        for(int i=0;i<180;i++){Thread.sleep(4);assets.update(16);application.executeRunnables();}
        require(((sk.sivak.eldritchhorror.core.view.components.card.CardTemplate)card).getForeground()!=null,"Gain artwork missing");
        require(card.getRotation()==0&&card.getScaleX()>0,"Card starts as a spinning point");
        if(fast)FastForwardAction.turnOn();
        for(int i=0;i<95;i++) {
            stage.act(1f/60);
            require(card.getRotation()==0,"Gain spun the card");
            com.badlogic.gdx.math.Vector2 top=card.localToStageCoordinates(new com.badlogic.gdx.math.Vector2(0,
                    sk.sivak.eldritchhorror.core.view.components.card.CardTemplate.CARD_HEIGHT));
            require(top.y<=540,"Card reveal clips the top edge");
            if(!fast&&(i==12||i==35||i==80))draw("gain-"+info.getClass().getSimpleName()+"-"+i);
        }
        require(stage.getRoot().findActor("card-arrival-light")==null,"Gain light leaked");
        require(Math.abs(card.getScaleX()-card.getScaleY())<.001f&&card.getColor().a==1,"Card did not settle");
        click(((Button[])field(view,"buttons"))[0]);tick(100);
        require(done.get()==1&&card.getStage()==null,"Gain confirmation or cleanup failed");
    }
    private void checkPrompt(boolean yes)throws Exception {
        FastForwardAction.turnOff();InfoStage.setBottomHeight(5);
        DiceImage die=new DiceImage(getTexture("dice_sheet.png"));die.setDiceValue(3);die.setPosition(440,190);stage.addActor(die);
        TestResultTable result=TestResultTable.createFailedTable();result.setPosition(360,80);stage.addActor(result);
        Question<Boolean> q=new Question<>();q.setTitle("Cast Flesh Ward?");q.setPortraitBeforeTitle(InvestigatorId.THE_PSYCHIC);
        q.setHideTestUi(true);q.setOptions(Arrays.asList(new Question.Option<>("Yes",true),new Question.Option<>("No",false)));
        QuestionView view=new QuestionView();AtomicInteger answered=new AtomicInteger();
        view.ask(q).subscribe(a->{require(Boolean.valueOf(yes).equals(a.getResponseData()),"Wrong answer");answered.incrementAndGet();});
        tick(65);require(!die.isVisible()&&!result.isVisible(),"Dice or outcome table covers the portrait prompt");
        draw("flesh-ward-prompt");
        click(((List<Button>)field(view,"buttons")).get(yes?0:1));tick(55);
        require(answered.get()==1&&die.isVisible()&&result.isVisible(),"Pending roll was not restored");
        die.remove();result.remove();
    }
    private void checkSelection()throws Exception {
        // Keep this isolated preview from queueing the entire gameplay catalog.
        Method get=CustomAssetManager.class.getDeclaredMethod("get");get.setAccessible(true);
        Object assets=get.invoke(null);Field queued=CustomAssetManager.class.getDeclaredField("textures2Queued");queued.setAccessible(true);queued.set(assets,true);
        List<InvestigatorInfo> roster=InvestigatorsHelper.initAvailableInvestigators(true);
        SelectMultipleInvestigators selection=new SelectMultipleInvestigators(roster,new SingleSubscriber<InvestigatorInfo[]>() {
            public void onSuccess(InvestigatorInfo[] r){} public void onError(Throwable e){throw new RuntimeException(e);}
        },1,stage);selection.show();
        Object component=field(selection,"selectInvestigatorComponent");
        Field id=component.getClass().getSuperclass().getDeclaredField("selectedInvestigatorId");id.setAccessible(true);
        Method show=component.getClass().getDeclaredMethod("showOkButton");show.setAccessible(true);
        Label detail=(Label)field(selection,"abilityLabel");
        for(InvestigatorInfo info:roster) {
            id.set(component,info.getInvestigatorId());show.invoke(component);tick(2);
            String action=UiText.get(info.getActionText());
            require(detail.getText().toString().contains(MarkupText.replaceColoredGlyphKeywords(action)),"Missing action: "+info.getInvestigatorId());
            if(info.getInvestigatorId()==InvestigatorId.THE_NUN||info.getInvestigatorId()==InvestigatorId.THE_MUSICIAN)
                draw("selection-"+info.getInvestigatorId());
        }
        selection.remove();
    }
    private void click(Button button) {
        InputEvent event=new InputEvent();event.setType(InputEvent.Type.touchUp);event.setTarget(button);
        for(com.badlogic.gdx.scenes.scene2d.EventListener listener:button.getListeners())if(listener instanceof ClickListener)((ClickListener)listener).clicked(event,1,1);
    }
    private static Object field(Object owner,String name)throws Exception{Field f=owner.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(owner);}
    private void tick(int n){for(int i=0;i<n;i++)stage.act(1f/60);}
    private void draw(String name)throws Exception {
        Gdx.gl.glClearColor(.04f,.04f,.06f,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);stage.draw();
        Pixmap pixels=ScreenUtils.getFrameBufferPixmap(0,0,960,540);PixmapIO.PNG png=new PixmapIO.PNG();png.setFlipY(true);
        png.write(Gdx.files.local("../build/presentation-preview/"+name+".png"),pixels);png.dispose();pixels.dispose();
    }
    private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
}
