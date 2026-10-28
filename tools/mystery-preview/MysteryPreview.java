import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.mystery.MysteryCard;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.game.*;
import sk.sivak.eldritchhorror.core.view.utils.*;

/** Exercises the actual sheet open/close path with faded background controls and live card questions. */
public class MysteryPreview extends ApplicationAdapter {
    private Stage stage;
    private final GameViewImpl view = new GameViewImpl();

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width=960; config.height=540; config.x=-10000; config.y=-10000; config.forceExit=false;
        new LwjglApplication(new MysteryPreview(), config);
    }

    public void create() {
        try {
            Sound silent=(Sound)Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class[]{Sound.class},
                    (p,m,a)->m.getReturnType()==long.class?0L:null);
            Gdx.audio=(Audio)Proxy.newProxyInstance(Audio.class.getClassLoader(), new Class[]{Audio.class}, (p,m,a)->silent);
            VisUI.load(); stage=InfoStage.getStageSafe();
            GameController controller=(GameController)Proxy.newProxyInstance(GameController.class.getClassLoader(),
                    new Class[]{GameController.class},(p,m,a)->null);
            new HudButtons(controller).init();
            Properties bundle=new Properties();
            try(InputStreamReader reader=new InputStreamReader(getClass().getResourceAsStream("/i18n/ui.properties"), StandardCharsets.UTF_8)) {
                bundle.load(reader);
            }
            java.util.List<String> keys=new ArrayList<>();
            for(String key:bundle.stringPropertyNames()) if(key.startsWith("mystery.") && key.endsWith(".name"))
                keys.add(key.substring(0,key.length()-5));
            Collections.sort(keys); require(keys.size()==34,"Missing mystery coverage");
            String original=UiText.getLanguage();
            try {
                for(String language:new String[]{"en","sk"}) {
                    language(language);
                    for(String key:keys) checkCard(key,language);
                }
            } finally { language(original); }
            System.out.println("PASS: all 34 mysteries in EN/SK; real open/close, centered over hidden controls, readable lore, no hard breaks, question clearance, fit and recentering");
        } catch(Throwable error) { error.printStackTrace(); System.exit(1); }
        finally { FastForwardAction.turnOff(); Gdx.app.exit(); }
    }

    private void checkCard(String key,String language) throws Exception {
        MysteryCardInfo info=(MysteryCardInfo)Proxy.newProxyInstance(MysteryCardInfo.class.getClassLoader(),new Class[]{MysteryCardInfo.class},(p,m,a)-> {
            switch(m.getName()) {
                case "getName":return key+".name";
                case "getFlavorText":return key+".flavor";
                case "getMysteryText":return key+".text";
                case "getMysteryComplexity":return 8;
                case "getClueCost":return key.endsWith("CROWN_OF_THE_SERPENT")?8:null;
                case "getProgress":case "getClueCredit":return 0;
                default:return null;
            }
        });
        // These controls are still isVisible()==true after opening a sheet fades their parent layers.
        Button backgroundButton=ButtonBuilder.buildButton("Continue"); InfoStage.showButton(backgroundButton);
        InfoStage.setBottomHeight(120);
        LabelTable backgroundPrompt=LabelTable.createAndShowTable(0,"A background gameplay prompt should not move the opened mystery.");
        tick(90);
        AtomicInteger closed=new AtomicInteger(); view.showCurrentMysteryCard(info,false,1,3).subscribe(closed::incrementAndGet);
        tick(100);
        MysteryCard card=BigActorsManager.getMysteryCard();
        require(card.getStage()==stage && card.getTouchable()==Touchable.enabled,"Card did not open: "+key);
        require(InfoStage.getBottomPromptTop()==0,"Faded prompt still reserves space: "+key);
        centered(card,key); require(card.getHeight()<=516,"Card needs scaling without a question: "+key+" "+card.getHeight());
        checkLabels(card);
        require(UiText.get(key+".flavor").split(" ").length>=30,"Lore is still just a one-liner: "+key);
        draw(language+"-"+key);

        Button answer=ButtonBuilder.buildButton("OK"); answer.setPosition((960-answer.getWidth())/2,5);
        InfoStage.addBigActor(null,answer,null,null);
        InfoStage.setBottomHeight(answer.getTop()+5);
        LabelTable question=LabelTable.createAndShowTable(0,"Resolve this mystery's effect now? Read the card, then choose your answer.");
        InfoStage.addBigActor(null,question,null,null); tick(90);
        float bottom=card.localToStageCoordinates(new Vector2(0,0)).y;
        float top=card.localToStageCoordinates(new Vector2(0,card.getHeight())).y;
        require(bottom>=question.getTop()+9.9f && top<=528.1f,"Question overlaps/clips mystery: "+key+" "+bottom+".."+top);
        if(key.contains("CROWN_OF_THE_SERPENT") || key.contains("voice")) draw(language+"-question-"+key);
        question.remove(); answer.remove(); tick(1); centered(card,key);
        // Dismiss by clicking the card, rather than directly removing the actor.
        Vector2 point=card.localToStageCoordinates(new Vector2(card.getWidth()/2,card.getHeight()/2)); stage.stageToScreenCoordinates(point);
        stage.touchDown((int)point.x,(int)point.y,0,0); stage.touchUp((int)point.x,(int)point.y,0,0); tick(100);
        require(card.getStage()==null && closed.get()==1,"Click failed to dismiss exactly once: "+key);
        backgroundButton.remove(); backgroundPrompt.remove(); InfoStage.setBottomHeight(0);
    }

    private void checkLabels(Group parent) {
        for(Actor actor:parent.getChildren()) {
            if(actor instanceof Label) {
                Label label=(Label)actor;
                require(label.getText().indexOf("\n")<0,"Hard line break remained");
                for(int i=0;i<label.getText().length;i++) {
                    char letter=label.getText().charAt(i);
                    require(Character.isWhitespace(letter) || label.getStyle().font.getData().getGlyph(letter)!=null,
                            "Missing font glyph: "+letter+" in "+label.getText());
                }
            }
            if(actor instanceof Group) checkLabels((Group)actor);
        }
    }
    private void centered(MysteryCard card,String key) {
        Vector2 center=card.localToStageCoordinates(new Vector2(card.getWidth()/2,card.getHeight()/2));
        require(center.dst(480,270)<.01f && card.getScaleX()==1,"Mystery is not centered: "+key+" "+center);
    }
    private void language(String value) {
        Application app=Gdx.app;
        try {Gdx.app=null;UiText.setLanguage(value);} finally {Gdx.app=app;}
    }
    private void tick(int frames) {for(int i=0;i<frames;i++)stage.act(1f/60);}
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.08f,.11f,.10f,1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels=ScreenUtils.getFrameBufferPixmap(0,0,960,540);
        PixmapIO.PNG png=new PixmapIO.PNG();png.setFlipY(true);
        png.write(Gdx.files.local("../build/mystery-preview/"+name+".png"),pixels);png.dispose();pixels.dispose();
    }
    private static void require(boolean ok,String message) {if(!ok)throw new AssertionError(message);}
}
