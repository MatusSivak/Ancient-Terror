package sk.sivak.eldritchhorror.core.view.components.sheet.ancientone;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.game.*;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** A single sheet keeps the awakening counter and replacement rules visible together. */
public final class YigCard extends VisTable implements AncientOneCard {
    private final DisplayHide displayHide;
    private AncientOneInfo info;
    private Label power;
    private Action0 beforeDisplay;
    public YigCard() {
        displayHide=new DisplayHide(this,BigActorsManager.BigActorKey.ANCIENT_ONE);
        displayHide.setActorKey(OnScreenActors.ActorKey.ANCIENT_ONE_CARD);
        setTransform(true);
    }
    @Override public void init(AncientOneInfo info) {
        this.info=info; clear(); pad(36,40,36,40); defaults().width(600); setBackground(CustomAssetManager.getTextureRegionDrawable(ANCIENT_ONE_DIALOG_BACKGROUND));
        add(label(get(info.getName())+" — "+get(info.getAltName()),23)).height(32).row();
        Table body=new Table();body.defaults().width(584);
        Image art=new Image(CustomAssetManager.getTexture("ancient_one/YIG.png"));art.setScaling(Scaling.fit);
        body.add(art).height(150).row();
        body.add(label(get(info.getFlavorText()),16)).padTop(8).row();
        body.add(label(get(info.getSpecialText()),17)).padTop(12).row();
        body.add(label("Reckoning: "+get(info.getReckoningText()),17)).padTop(12).row();
        body.add(label(get(info.getWinText()),17)).padTop(12).row();
        if(!info.isAwaken())body.add(label(get(info.getMidnightText()),17)).padTop(8).row();
        ScrollPane scroll=new ScrollPane(body);scroll.setScrollingDisabled(true,false);scroll.setFadeScrollBars(false);
        add(scroll).height(info.isAwaken()?350:382).row();
        power=label("",20);updatePower(info.getPower());
        if(info.isAwaken())add(power).height(28).padTop(4).row();
        setSize(680,490);
        AncientOneStyles.addCloseButton(this);
    }
    private Label label(String text,int size) {
        Label label=new Label(text,new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4,size),Color.DARK_GRAY));
        label.setWrap(true);label.setAlignment(Align.left);return label;
    }
    private void updatePower(int value){if(power!=null)power.setText("Eldritch tokens: "+value+" / 8");}
    @Override public Actor getActor(){return this;}
    @Override public void setAfterHideAction(Action0 action){displayHide.setAfterHideAction(action);}
    @Override public void setAfterDisplayAction(Action0 action){displayHide.setAfterDisplayAction(action);}
    @Override public void setBeforeDisplayAction(Action0 action){beforeDisplay=action;}
    @Override public void displayOrHide() {
        updatePower(info.getPower());
        displayHide.setDisplayedY(VIEWPORT_HEIGHT/2f-getHeight()/2f);
        displayHide.setBeforeDisplayAction(beforeDisplay!=null?beforeDisplay:() -> {
            InfoStage.getInvestigatorHud().hide().subscribe();HudButtons.getTrackHud().hide().subscribe();
        });
        displayHide.setBeforeHideAction(() -> {
            InfoStage.getInvestigatorHud().show().subscribe();HudButtons.getTrackHud().show().subscribe();
        });
        displayHide.displayOrHide().subscribe();
    }
    @Override public Completable increaseAncientOnePower(int increment) {
        return Completable.create(subscriber -> {
            updatePower(Math.max(0,info.getPower()+increment));
            setAfterHideAction(subscriber::onCompleted);
        });
    }
}
