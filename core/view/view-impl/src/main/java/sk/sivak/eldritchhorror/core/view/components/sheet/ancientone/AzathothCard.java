package sk.sivak.eldritchhorror.core.view.components.sheet.ancientone;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.ToughnessBar;
import sk.sivak.eldritchhorror.core.view.components.track.DoomTrackWidget;
import sk.sivak.eldritchhorror.core.view.components.track.OmenTrack;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ANCIENT_ONE_AZATHOTH_LABEL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ANCIENT_ONE_LABEL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SPECIAL_ELITE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ANCIENT_ONE_DIALOG_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class AzathothCard extends VisTable implements AncientOneCard{

    public static final float SCALE = 0.72f;
    private final DisplayHide displayHide;
    private AncientOneInfo ancientOneInfo;
    private Image hitImage;
    private Action0 afterHideAction;
    private Action0 beforeDisplayAction;

    public AzathothCard() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.ANCIENT_ONE);
        displayHide.setActorKey(OnScreenActors.ActorKey.ANCIENT_ONE_CARD);
        setTransform(true);
    }

    public void init(AncientOneInfo ancientOneInfo) {
        if (this.ancientOneInfo == ancientOneInfo) {
            return;
        }
        clear();
        align(Align.bottom);
        setBackground((Drawable) null);
        getColor().a = 1f;
        this.ancientOneInfo = ancientOneInfo;
        pad(50 * SCALE, 50 * SCALE, 25 * SCALE, 50 * SCALE);


        Table topPart = createTopPart(ancientOneInfo);
        Table leftPart = createLeftPart(ancientOneInfo);
        Table rightPart = createRightPart(ancientOneInfo);

        add(topPart).colspan(2).growY();
        row();
        add(leftPart).align(Align.topLeft);
        add(rightPart).align(Align.topRight);

        pack();
        setHeight(748 * SCALE);
        setWidth(1067 * SCALE);
        CustomAssetManager.getTextureAsync(ANCIENT_ONE_DIALOG_BACKGROUND).subscribe(ok -> {
            TextureRegionDrawable background = CustomAssetManager.getTextureRegionDrawable(ANCIENT_ONE_DIALOG_BACKGROUND);
            setBackground(background);
        });
        addHitImage();
        AncientOneStyles.addCloseButton(this);
    }

    @Override
    public Actor getActor() {
        return this;
    }

    private Table createTopPart(AncientOneInfo ancientOneInfo) {
        Table table = new Table();
        CustomAssetManager.getTextureAsync("ancient_one/" + ancientOneInfo.getAncientOneId() + ".png").subscribe(ok -> {
            Image image = new Image(CustomAssetManager.getTexture("ancient_one/" + ancientOneInfo.getAncientOneId() + ".png"));
            image.setScaling(Scaling.fit);
            table.add(image).grow();
        });

        return table;
    }

    private Table createLeftPart(AncientOneInfo ancientOneInfo) {
        VisTable table = new VisTable();

        Label altNameLabel = createNiceLabel(get(ancientOneInfo.getAltName()));
        Label flavorLabel = createFlavorLabel(get(ancientOneInfo.getFlavorText()));
        Container<Label> flavorContainer = new Container<>(flavorLabel);
        flavorContainer.fill();
        flavorContainer.top();
        flavorContainer.setClip(true);

        Label ancientOneLabel = AncientOneStyles.title(get(ancientOneInfo.getName()));

        table.add(ancientOneLabel).height(80 * SCALE).align(Align.bottom);
        table.row();
        table.add(altNameLabel).width(490 * SCALE);
        table.row();
        table.add(AncientOneStyles.divider()).height(1f).growX().padTop(12f).padBottom(14f).row();
        table.add(flavorContainer).width(490 * SCALE).height(220f * SCALE).top();
        table.padLeft(20).padRight(10);
        return table;
    }

    private Table createRightPart(AncientOneInfo info) {
        Table table = new Table();
        DoomTrackWidget clock = new DoomTrackWidget();
        clock.updateDoom(0);
        clock.setScale(48 / 593f);
        OmenTrack omen = new OmenTrack();
        omen.updateOmen(OmenId.NORTH);
        omen.setScale(48 / 700f);

        addRuleRow(table, get("ancientOne.setup"), null, get(info.getSetupText()), false);
        addRuleRow(table, get("ancientOne.midnightLabel"), clock, get(info.getMidnightText()), true);
        addRuleRow(table, get("ancientOne.omenLabel"), omen, get(info.getSpecialText()), false);
        addRuleRow(table, get("ancientOne.victory"), null, get(info.getWinText()), false);
        return table;
    }

    private void addRuleRow(Table table, String heading, Actor icon, String text, boolean danger) {
        Table marker = new Table();
        if (icon != null) marker.add(icon).width(48).height(48).row();
        Label label = createTextLabel(heading);
        label.setFontScale(0.4f);
        label.setColor(AncientOneStyles.BRONZE);
        marker.add(label).width(76).padTop(icon == null ? 0 : 3);
        Label value = createValue(NEW_FONT_SOURCE_SERIF_4, 0.5f);
        value.setText((danger ? "[#8C292D]" : "[#30271E]") + text + "[]");
        value.setWrap(true);
        table.add(marker).width(76).minHeight(60).padBottom(6);
        table.add(value).width(226).padLeft(8).padBottom(6).align(Align.left);
        table.row();
    }
    private Label createTextLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, AncientOneStyles.RULE_FONT_SIZE), Color.BLACK);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.5f);
        return label;
    }

    private Label createValue(String fontName, float fontScale) {
        Label.LabelStyle style = new Label.LabelStyle(getBitmapFontNew(fontName, AncientOneStyles.RULE_FONT_SIZE), Color.WHITE);
        style.background = AncientOneStyles.ruleBackground();

        Label label = new Label("0", style);
        style.font.getData().markupEnabled = true;
        label.setAlignment(Align.left);
        label.setFontScale(fontScale);
        return label;
    }

    private Label createNameLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, AncientOneStyles.RULE_FONT_SIZE), Color.BLACK);
        Color color = new Color(1f, 1f, 1f, 0.58f);
        labelStyle.background = new TextureRegionDrawable(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND)) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.setColor(color);
                super.draw(batch, x, y, width, height);
            }
        };
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.52f);
        return label;
    }

    private Label createNiceLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 44), Color.DARK_GRAY);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setWrap(true);
        label.setFontScale(0.4f);
        return label;
    }

    private Label createFlavorLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 44), Color.DARK_GRAY);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.top | Align.center);
        label.setWrap(true);
        label.setFontScale(0.32f);
        return label;
    }


    @Override
    protected void positionChanged() {
        super.positionChanged();
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    private void addHitImage() {
        hitImage = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        addActor(hitImage);
        hitImage.getColor().a = 0.0f;
    }

    public void setAfterHideAction(Action0 afterHideAction) {
        displayHide.setAfterHideAction(afterHideAction);
    }

    public void displayOrHide() {
        displayHide.setDisplayedY(VIEWPORT_HEIGHT / 2f - getHeight() / 2);

        if (beforeDisplayAction == null) {
            displayHide.setBeforeDisplayAction(() -> {
                InfoStage.getInvestigatorHud().hide().subscribe();
                HudButtons.getTrackHud().hide().subscribe();
            });
        } else {
            displayHide.setBeforeDisplayAction(beforeDisplayAction);
        }

        displayHide.setBeforeHideAction(() -> {
            InfoStage.getInvestigatorHud().show().subscribe();
            HudButtons.getTrackHud().show().subscribe();
        });
        displayHide.displayOrHide().subscribe();
    }

    public void setBeforeDisplayAction(Action0 beforeDisplayAction) {
        this.beforeDisplayAction = beforeDisplayAction;
    }

    @Override
    public Completable increaseAncientOnePower(int increment) {
        return null;
    }

    @Override
    public void setAfterDisplayAction(Action0 input) {

    }
}
