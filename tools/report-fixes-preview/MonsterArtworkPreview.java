import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.Proxy;
import sk.sivak.eldritchhorror.core.constants.ancientone.*;
import sk.sivak.eldritchhorror.core.constants.combat.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.constants.monster.epic.YigMonster;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.eventlistener.monster.YigCultistMonsterListener;
import sk.sivak.eldritchhorror.core.model.util.AncientOneHelper;
import sk.sivak.eldritchhorror.core.view.assetmanager.*;
import sk.sivak.eldritchhorror.core.view.components.combat.*;
import sk.sivak.eldritchhorror.core.view.components.select.SelectMonsterComponent;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.MonsterCard;
import sk.sivak.eldritchhorror.core.view.game.*;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterImage;

/** Production artwork paths and widgets, with no saves, preferences or network access. */
public class MonsterArtworkPreview extends ApplicationAdapter {
    private Stage stage;
    private AncientOneInfo current;
    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width=960; config.height=540; config.x=-10000; config.y=-10000; config.forceExit=false;
        new LwjglApplication(new MonsterArtworkPreview(), config);
    }
    @Override public void create() {
        try {
            VisUI.load(); stage=InfoStage.getStageSafe();
            GameController controller=(GameController)Proxy.newProxyInstance(GameController.class.getClassLoader(),
                    new Class[]{GameController.class}, (p,m,a) -> current);
            new GameViewImpl().setController(controller);
            Texture generic=CustomAssetManager.getNonEpicMonsterTexture("CultistMonster");
            current=AncientOneHelper.createYig();
            Texture custom=CustomAssetManager.getNonEpicMonsterTexture("CultistMonster");
            require(custom != generic, "Yig still uses the generic cultist");
            require(custom == CustomAssetManager.getTexture("monster/cultist/YIG.png"), "Wrong Yig texture");
            CustomAssetManager.getTextureAsync(MonsterArtwork.path("CultistMonster",false))
                    .subscribe(texture -> require(texture == custom,"Map and synchronous artwork differ"));
            for (AncientOneInfo info : AncientOneHelper.initAvailableAncientOnes()) {
                current=info;
                require(CustomAssetManager.getNonEpicMonsterTexture("CultistMonster")
                        == (info.getAncientOneId()==AncientOneId.YIG ? custom : generic),"Fallback failed");
            }
            for (boolean awake : new boolean[]{false,true}) {
                current=awake ? AncientOneHelper.createAwakenYig() : AncientOneHelper.createYig();
                CultistMonster monster=new CultistMonster();
                YigCultistMonsterListener.configure(monster,awake,true);
                showMonster(monster,custom,awake ? "cultist-yig-awake" : "cultist-yig-sleeping",controller);
            }
            YigMonster yig=new YigMonster(); yig.setToughness(5); yig.setCurrentHealth(5);
            showMonster(yig,CustomAssetManager.getEpicMonsterTexture("YigMonster"),"yig-epic",controller);
            current=AncientOneHelper.createAzathoth();
            require(CustomAssetManager.getNonEpicMonsterTexture("CultistMonster")==generic,"Yig texture leaked into next game");
            System.out.println("PASS: per-Ancient-One fallback, new/load/awake context, async map artwork, cards, selection and both combat views");
        } catch(Throwable failure) { failure.printStackTrace(); System.exit(1); }
        finally { MonsterArtwork.setAncientOneSupplier(null); Gdx.app.exit(); }
    }
    private void showMonster(AbstractMonsterInfo monster,Texture texture,String name,GameController controller) throws Exception {
        MonsterCard card=new MonsterCard(); card.init(monster,null); showTable(card,name+"-card",texture);
        SelectMonsterComponent selection=new SelectMonsterComponent(monster); selection.pack();
        showTable(selection,name+"-selection",texture);
        MonsterImage token=new MonsterImage(monster,controller,true,false,LocationId.LONDON,texture);
        token.setPosition(430,240); stage.addActor(token); draw(name+"-map"); token.remove();
        MonsterCombatTableData data=new MonsterCombatTableData();
        data.setMonsterClassName(monster.getClass().getSimpleName()); data.setMonsterName(monster.getName());
        data.setMonsterEpic(monster.isEpic()); data.setMonsterInfo(monster);
        data.setHorror(monster.getHorror()); data.setDamage(monster.getDamage());
        data.setToughness(monster.getToughness()); data.setCurrentHealth(monster.getCurrentHealth());
        MonsterCombatTable combat=new MonsterCombatTable(); stage.addActor(combat); combat.init(data);
        for(int i=0;i<60;i++) stage.act(1f/60f);
        require(usesTexture(InfoStage.getCreatureLayer(),texture),"Combat texture mismatch");
        draw(name+"-combat"); combat.remove();
        for(int i=0;i<60;i++) stage.act(1f/60f);
        CombatOverviewTableData overviewData=new CombatOverviewTableData();
        overviewData.setInvestigatorId(InvestigatorId.THE_POLITICIAN);
        overviewData.setHorrorRowData(new CombatOverviewTableData.StatRowData(Stat.WILL,3));
        overviewData.setDamageRowData(new CombatOverviewTableData.StatRowData(Stat.STRENGTH,3));
        overviewData.setMonsterClassName(data.getMonsterClassName()); overviewData.setMonsterName(data.getMonsterName());
        overviewData.setMonsterEpic(data.isMonsterEpic()); overviewData.setHorror(data.getHorror()); overviewData.setDamage(data.getDamage());
        overviewData.setToughness(data.getToughness()); overviewData.setCurrentHealth(data.getCurrentHealth());
        CombatOverviewTable overview=new CombatOverviewTable(); overview.init(overviewData); overview.pack();
        showTable(overview,name+"-overview",texture);
    }
    private void showTable(Table table,String name,Texture texture) throws Exception {
        stage.addActor(table); table.validate(); table.setPosition(480-table.getWidth()/2,270-table.getHeight()/2);
        require(usesTexture(table,texture),"Wrong artwork in "+name);
        draw(name); table.remove();
    }
    private boolean usesTexture(Actor actor,Texture texture) {
        if(actor instanceof Image && ((Image)actor).getDrawable() instanceof TextureRegionDrawable
                && ((TextureRegionDrawable)((Image)actor).getDrawable()).getRegion().getTexture()==texture) return true;
        if(actor instanceof Group) for(Actor child:((Group)actor).getChildren()) if(usesTexture(child,texture))return true;
        return false;
    }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.08f,.12f,.1f,1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels=ScreenUtils.getFrameBufferPixmap(0,0,960,540); PixmapIO.PNG png=new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/report-fixes-preview/"+name+".png"),pixels); png.dispose(); pixels.dispose();
    }
    private static void require(boolean ok,String message) { if(!ok)throw new AssertionError(message); }
}
