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
import sk.sivak.eldritchhorror.core.eventlistener.monster.YigCultistMonsterListener;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.YigCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.MonsterCard;
import sk.sivak.eldritchhorror.core.view.initgame.SelectAncientOneTable;
import sk.sivak.eldritchhorror.core.view.initgame.FullGamePurchaseDialog;

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
            sk.sivak.eldritchhorror.core.view.assetmanager.MonsterArtwork.setAncientOneSupplier(AncientOneHelper::createYig);
            YigCard card=new YigCard();card.init(AncientOneHelper.createYig());render(card,"yig-sleeping");
            AncientOneInfo awake=AncientOneHelper.createAwakenYig();card.init(awake);render(card,"yig-awake");
            if(findScroll(card)!=null)throw new AssertionError("Yig rules should be visible without scrolling");
            String text=labels(card);
            if(text.contains("Eldritch") || text.contains("investigator") || !text.contains("Spawn one Cultist at a random space.")
                    || !text.contains("Solve three Mysteries,\nthen defeat Yig to win.")
                    || !text.contains("The temple stones split beneath"))throw new AssertionError(text);
            card.init(AncientOneHelper.createYig());render(card,"yig-sleeping-reused");
            for(boolean awakened : new boolean[]{false, true}) {
                CultistMonster cultist = new CultistMonster();
                YigCultistMonsterListener.configure(cultist, awakened, true);
                MonsterCard cultistCard = new MonsterCard();
                cultistCard.init(cultist, null);
                render(cultistCard, awakened ? "yig-cultist-awake" : "yig-cultist-sleeping");
            }
            for(AbstractMonsterInfo monster:new AbstractMonsterInfo[]{new YigMonster(),new ChildrenOfYigMonster(),new WingedSerpentMonster()}) {
                monster.setToughness(monster instanceof YigMonster?5:4);monster.setCurrentHealth(monster.getToughness());
                MonsterCard monsterCard=new MonsterCard();monsterCard.init(monster,null);render(monsterCard,monster.getClass().getSimpleName());
            }
            SelectAncientOneTable selection=new SelectAncientOneTable(AncientOneHelper.initAvailableAncientOnes(),true,true,true,null);
            render(selection,"selection-top");
            java.lang.reflect.Method select=SelectAncientOneTable.class.getDeclaredMethod("select",AncientOneId.class);
            select.setAccessible(true);select.invoke(selection,AncientOneId.YIG);
            stage.act(0);draw("selection-yig");
            SelectAncientOneTable lockedSelection=new SelectAncientOneTable(AncientOneHelper.initAvailableAncientOnes(),false,false,false,null);
            render(lockedSelection,"selection-locked");
            select.invoke(lockedSelection,AncientOneId.YIG);stage.act(0);draw("selection-yig-locked");
            for(int frame=0;frame<110;frame++)stage.act(1f/60f);
            draw("selection-yig-locked-pulse");
            stage.clear();
            FullGamePurchaseDialog.show(stage, () -> {});
            for(int frame=0;frame<60;frame++)stage.act(1f/60f);
            draw("full-game-promo");
            checkAwakeningClock();
            System.out.println("PASS: sleeping/awake Yig with new rules and artwork, three epic monsters, and single-row five-Ancient-One selector (unlocked and locked) rendered.");
        } catch(Throwable failure) {failure.printStackTrace();System.exit(1);} finally {
            sk.sivak.eldritchhorror.core.view.assetmanager.MonsterArtwork.setAncientOneSupplier(null);
            Gdx.app.exit();
        }
    }
    private void render(Table card,String name) throws Exception {
        stage.clear();stage.addActor(card);card.validate();
        if(card.getHeight()>540 || card.getWidth()>950)throw new AssertionError("Offscreen: "+name+" "+card.getWidth()+"x"+card.getHeight());
        card.setPosition((960-card.getWidth())/2,(540-card.getHeight())/2);stage.act(0);draw(name);
    }
    private ScrollPane findScroll(Group group) {
        for(Actor child:group.getChildren()) {
            if(child instanceof ScrollPane)return (ScrollPane)child;
            if(child instanceof Group){ScrollPane found=findScroll((Group)child);if(found!=null)return found;}
        }
        return null;
    }
    private String labels(Group group) {
        java.lang.StringBuilder text=new java.lang.StringBuilder();
        for(Actor child:group.getChildren()) {
            if(child instanceof Label)text.append(((Label)child).getText()).append('\n');
            if(child instanceof Group)text.append(labels((Group)child));
        }
        return text.toString();
    }
    private void checkAwakeningClock() throws Exception {
        Gdx.audio=(Audio)java.lang.reflect.Proxy.newProxyInstance(Audio.class.getClassLoader(),new Class[]{Audio.class},
                (p,m,a)->java.lang.reflect.Proxy.newProxyInstance(com.badlogic.gdx.audio.Sound.class.getClassLoader(),
                        new Class[]{com.badlogic.gdx.audio.Sound.class},(s,method,args)->{
                            if(method.getName().equals("hashCode"))return System.identityHashCode(s);
                            if(method.getName().equals("equals"))return s==args[0];
                            return method.getReturnType()==long.class?0L:null;
                        }));
        new sk.sivak.eldritchhorror.core.view.game.HudButtons(null);
        sk.sivak.eldritchhorror.core.view.components.track.DoomTrackWidget clock=
                sk.sivak.eldritchhorror.core.view.game.HudButtons.getDoomTrack();
        sk.sivak.eldritchhorror.core.view.doomomen.DoomOmenViewImpl view=
                new sk.sivak.eldritchhorror.core.view.doomomen.DoomOmenViewImpl();
        stage.clear();stage.addActor(clock);clock.setPosition(450,230);
        int[] completed={0};
        for(boolean fast:new boolean[]{false,true}) {
            if(fast)sk.sivak.eldritchhorror.core.view.utils.FastForwardAction.turnOn();
            else sk.sivak.eldritchhorror.core.view.utils.FastForwardAction.turnOff();
            clock.updateDoom(1);tickClock();
            view.advanceDoom(-1).subscribe(()->{completed[0]++;clock.updateDoom(8);});
            tickClock();
            if(completed[0]!=(fast?4:1) || clock.getCurrentDoom()!=8)
                throw new AssertionError("Awakening clock reset repeated an old completion");
            view.advanceDoom(-1).subscribe(()->completed[0]++);tickClock();
            if(clock.getCurrentDoom()!=7)throw new AssertionError("Doom did not move toward midnight");
            view.retreatDoom(1).subscribe(()->completed[0]++);tickClock();
            if(completed[0]!=(fast?6:3) || clock.getCurrentDoom()!=8)throw new AssertionError("Doom retreat failed");
        }
        sk.sivak.eldritchhorror.core.view.utils.FastForwardAction.turnOff();
        java.lang.reflect.Field hand=clock.getClass().getDeclaredField("minuteHand");hand.setAccessible(true);
        if(Math.abs(((Actor)hand.get(clock)).getRotation()-240)>0.01f)throw new AssertionError("Clock must show 40 minutes before midnight");
        System.out.println("PASS: awakening resets clock to 23:20, advance/retreat and callbacks complete once at both speeds.");
    }
    private void tickClock() { for(int frame=0;frame<1500;frame++)stage.act(1f/60f); }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.06f,.09f,.07f,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);stage.draw();
        Pixmap pixels=ScreenUtils.getFrameBufferPixmap(0,0,960,540);PixmapIO.PNG png=new PixmapIO.PNG();png.setFlipY(true);
        png.write(Gdx.files.local("../build/yig-preview/"+name+".png"),pixels);png.dispose();pixels.dispose();
    }
    @Override public void dispose(){if(stage!=null)stage.dispose();VisUI.dispose();}
}
