import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.*;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.game.*;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import sk.sivak.eldritchhorror.core.view.map.gate.*;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;
import sk.sivak.eldritchhorror.core.view.map.monster.*;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Production render checks; uses fixtures/downloaded reports, never the player's save. */
public class OctoberReportsPreview extends ApplicationAdapter {
    private Stage stage;
    private final Vector2 location = LocationPositionResolver.resolve(LocationId.ARKHAM);
    private boolean detained;
    private GameController controller;

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration c = new LwjglApplicationConfiguration();
        c.width=960; c.height=540; c.x=-10000; c.y=-10000; c.forceExit=false;
        new LwjglApplication(new OctoberReportsPreview(), c);
    }

    public void create() {
        try {
            Sound silent = (Sound) Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class[]{Sound.class},
                    (p,m,a) -> m.getReturnType()==long.class ? 0L : null);
            Gdx.audio=(Audio)Proxy.newProxyInstance(Audio.class.getClassLoader(),new Class[]{Audio.class},(p,m,a)->silent);
            VisUI.load(); InfoStage.getStageSafe(); MapStage.resize(960,540); stage=MapStage.getStage();
            controller=(GameController)Proxy.newProxyInstance(GameController.class.getClassLoader(),new Class[]{GameController.class},
                    (p,m,a)->m.getName().equals("isInvestigatorDetained") ? detained : null);
            for(int i=-1;i<=1;i++) {
                Image map=new Image(getTexture(MAP)); map.setX(i*MAP_WIDTH); MapStage.getMapLayer().addActor(map);
            }
            verifyReportedConditions();
            for(boolean fast:new boolean[]{false,true}) {
                if(fast)FastForwardAction.turnOn();else FastForwardAction.turnOff();
                checkGate(fast);
                checkDiscard(false,fast); checkDiscard(true,fast);
                checkDetained(fast);
            }
            System.out.println("PASS: reported duplicate Curse and exhausted deck; gate below monsters; normal/epic discard; Detained gain/load/discard/defeat; normal and fast-forward");
        } catch(Throwable t) { t.printStackTrace();System.exit(1); }
        finally { FastForwardAction.turnOff();Gdx.app.exit(); }
    }

    private void checkGate(boolean fast)throws Exception {
        camera(location.x,location.y,.75f);
        Group layer=MapStage.getGateLayer();
        GateUtils.GateIdLayerResolver resolver=new GateUtils.GateIdLayerResolver(LocationId.ARKHAM);
        for(int i=-1;i<=1;i++) {
            NewGateAnimatedImage gate=new NewGateAnimatedImage(GateColor.BLUE,OmenColor.BLUE);
            gate.setBounds(location.x+i*MAP_WIDTH-67.5f,location.y-67.5f,135,135);
            MapStage.addToLayer(gate,resolver);
        }
        MonsterImage monster=monster(); monster.setPosition(location.x-35,location.y-35);
        MapStage.getMonsterLayer().addActor(monster);tick(40);
        AtomicInteger done=new AtomicInteger();
        new PrepareBoardView(controller).closeGate(LocationId.ARKHAM).subscribe(done::incrementAndGet);
        boolean sawEffect=false;
        for(int i=0;i<240;i++) {
            tick(1);
            Actor effect=layer.findActor("gate-close");
            if(effect!=null) {
                sawEffect=true;
                require(effect.getParent()==layer,"Closing gate left its layer");
                require(layer.getZIndex()<MapStage.getMonsterLayer().getZIndex(),"Gate covers monster");
                require(InfoStage.getStageSafe().getRoot().findActor("gate-close")==null,"Gate moved into UI");
                if(!fast && i==70)draw("gate-below-monster");
            }
        }
        require(sawEffect && done.get()==1 && layer.getChildren().size==0,"Gate cleanup/callback");
        require(MapStage.getActor(resolver).isEmpty(),"Gate registry leaked");monster.remove();
    }

    private MonsterImage monster() {
        return new MonsterImage(new GhoulMonster(),null,true,false,LocationId.ARKHAM,getTexture("monster/Ghoul.png"));
    }

    private void checkDiscard(boolean epic,boolean fast)throws Exception {
        camera(location.x,location.y,.65f);
        AbstractMonsterInfo info=epic ? new sk.sivak.eldritchhorror.core.constants.monster.epic.WingedSerpentMonster() : new GhoulMonster();
        info.setCurrentLocation(LocationId.ARKHAM);
        MonsterUtils.MonsterIdLayerResolver resolver=new MonsterUtils.MonsterIdLayerResolver(info);
        List<Actor> copies=new ArrayList<>();Group layer=resolver.getLayer();
        for(int i=-1;i<=1;i++) {
            Actor actor=epic ? new EpicMonsterImage(info,null,false,location,getTexture("monster/epic/WingedSerpent.png")) : monster();
            actor.setPosition(location.x+i*MAP_WIDTH-actor.getWidth()/2,location.y-actor.getHeight()/2);
            MapStage.addToLayer(actor,resolver);copies.add(actor);
        }
        AtomicInteger done=new AtomicInteger();
        MonsterUtils.discardMonster(info).subscribe(done::incrementAndGet);
        for(int i=0;i<120;i++) {
            tick(1);
            if(!fast && (i==5||i==27||i==48))draw("discard-"+(epic?"epic":"normal")+"-"+i);
            for(Actor actor:copies) {
                require(actor.getScaleX()>=0 && actor.getScaleY()>=0,"Discard flips monster");
                require(actor.getTouchable()==Touchable.disabled,"Discard remains interactive");
            }
        }
        require(done.get()==1 && layer.getChildren().size==0 && MapStage.getActor(resolver).isEmpty(),"Discard cleanup/callback");
        MonsterUtils.discardMonster(info).subscribe(done::incrementAndGet);
        require(done.get()==2,"Missing token stalls discard");
    }

    private void checkDetained(boolean fast)throws Exception {
        camera(location.x,location.y,.45f);detained=false;
        List<InvestigatorImage> copies=new ArrayList<>();
        for(int i=-1;i<=1;i++) {
            InvestigatorImage actor=new InvestigatorImage(InvestigatorId.THE_SAILOR,controller);
            actor.setPosition(location.x+i*MAP_WIDTH-actor.getWidth()/2,location.y-actor.getHeight()/2);
            MapStage.getInvestigatorLayer().addActor(actor);copies.add(actor);
        }
        tick(1);detained=true;tick(8);
        for(InvestigatorImage actor:copies)require(actor.isDetainedVisible(),"Detained gain missing");
        if(!fast)draw("detained-rattle");tick(60);
        if(!fast)draw("detained-settled");
        for(InvestigatorImage actor:copies) {actor.moveBy(24,0);actor.setScale(.85f);}
        tick(1);if(!fast)draw("detained-moved");
        detained=false;tick(1);
        for(InvestigatorImage actor:copies)require(!actor.isDetainedVisible(),"Discard left bars");
        detained=true;
        InvestigatorImage loaded=new InvestigatorImage(InvestigatorId.THE_SAILOR,controller);
        loaded.act(.2f);require(loaded.isDetainedVisible(),"Loaded Detained missing");
        for(InvestigatorImage actor:copies) {actor.setDefeatedByHealth(true);actor.act(.1f);require(!actor.isDetainedVisible(),"Defeated token has bars");actor.remove();}
        detained=false;
    }

    private void verifyReportedConditions()throws Exception {
        sk.sivak.eldritchhorror.core.model.ConditionsDeck deck=new sk.sivak.eldritchhorror.core.model.ConditionsDeck();
        com.badlogic.gdx.files.FileHandle file=Gdx.files.local("../build/report-review-oct31/c81fe575-4f4e-444d-bae9-92d051f4f6d1/save.json");
        if(file.exists()) {
            Object helper=new sk.sivak.eldritchhorror.core.view.initgame.SaveLoadGameHelper();
            Field field=helper.getClass().getDeclaredField("json");field.setAccessible(true);
            Json json=(Json)field.get(helper);
            sk.sivak.eldritchhorror.core.model.save.SaveData save=json.fromJson(sk.sivak.eldritchhorror.core.model.save.SaveData.class,file.readString());
            deck.load(save.getConditionsDeck());
        } else {
            deck.createDeck();deck.gainCondition(ConditionId.CURSED,InvestigatorId.THE_MUSICIAN);
        }
        sk.sivak.eldritchhorror.core.action.ServicePlatform platform=sk.sivak.eldritchhorror.core.action.ServicePlatform.get();
        platform.setConditionsDeck(deck);
        ConditionInfo original=deck.getCondition(InvestigatorId.THE_MUSICIAN,ConditionId.CURSED);
        gainCurse(InvestigatorId.THE_MUSICIAN);
        require(deck.getCondition(InvestigatorId.THE_MUSICIAN,ConditionId.CURSED)==original,"Duplicate curse replaced card");
        // Exhaust the physical copies, then verify another queued gain is a safe no-op.
        InvestigatorId exhausted=null;
        for(InvestigatorId id:InvestigatorId.values()) {
            if(deck.hasCondition(id,ConditionId.CURSED))continue;
            if(!deck.canGetConditionId(id,ConditionId.CURSED)) {exhausted=id;break;}
            deck.gainCondition(ConditionId.CURSED,id);
        }
        require(exhausted!=null,"Failed to exhaust Curse deck");gainCurse(exhausted);
        require(!deck.hasCondition(exhausted,ConditionId.CURSED),"Unavailable Curse was created");
        sk.sivak.eldritchhorror.core.action.ServicePlatform.nullifyInstance();
    }

    private void gainCurse(InvestigatorId id) {
        sk.sivak.eldritchhorror.core.action.impl.investigator.GainConditionFromDeckAction action=new sk.sivak.eldritchhorror.core.action.impl.investigator.GainConditionFromDeckAction();
        action.setInput(new sk.sivak.eldritchhorror.core.eventtype.data.GainConditionFromDeckData(id,ConditionId.CURSED));
        action.execute().toBlocking().value();
    }
    private void tick(int frames) {for(int i=0;i<frames;i++) {stage.act(1f/60);InfoStage.getStageSafe().act(1f/60);}}
    private void camera(float x,float y,float zoom) {MapStage.getCamera().position.set(x,y,0);MapStage.getCamera().zoom=zoom;MapStage.getCamera().update();}
    private void draw(String name)throws Exception {
        Gdx.gl.glClearColor(.04f,.04f,.06f,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);stage.draw();
        Pixmap pixels=ScreenUtils.getFrameBufferPixmap(0,0,960,540);PixmapIO.PNG png=new PixmapIO.PNG();png.setFlipY(true);
        png.write(Gdx.files.local("../build/report-review-oct31/"+name+".png"),pixels);png.dispose();pixels.dispose();
    }
    private static void require(boolean condition,String message) {if(!condition)throw new AssertionError(message);}
}
