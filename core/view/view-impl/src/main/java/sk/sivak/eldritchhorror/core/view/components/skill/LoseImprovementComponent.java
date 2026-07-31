package sk.sivak.eldritchhorror.core.view.components.skill;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.CompletableSubscriber;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.components.sheet.investigator.StatsTable;
import sk.sivak.eldritchhorror.core.view.components.sheet.investigator.StatsTableData;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class LoseImprovementComponent extends VisTable {

    private LoseImprovementTable loseImprovementTable;
    private Stat onlyAvailableSkill = null;
    private int amountToLose;
    private final DisplayHide displayHide;

    private List<ImageButton> imageButtons = new LinkedList<>();

    private Map<Label, ImageButton> labelImageButtonMap = new HashMap<>();

    private SingleSubscriber<? super Stat> onSub;
    private CompletableSubscriber onSub2;

    public LoseImprovementComponent() {
        setTransform(true);
        displayHide = new DisplayHide(this, null);
    }

    public void init(StatsTableData statsTableData, Stat skill, int amountToLose) {
        this.onlyAvailableSkill = skill;
        this.amountToLose = amountToLose;
        init(statsTableData);

    }

    public void justShow(StatsTableData statsTableData) {
        init(statsTableData);
        for (ImageButton imageButton : labelImageButtonMap.values()) {
            imageButton.remove();
        }
    }

    public void justHide() {
        hide();
    }

    private void init(StatsTableData statsTableData) {
        imageButtons.clear();
        labelImageButtonMap.clear();
        clear();
        this.loseImprovementTable = new LoseImprovementTable();
        loseImprovementTable.init(statsTableData);
        TextureRegionDrawable textureRegionDrawable = CustomAssetManager.getTextureRegionDrawable("wooden_background.png");
        textureRegionDrawable.getRegion().flip(true, false);
        setBackground(textureRegionDrawable);
        add(createLabel()).width(200).row();
        add(loseImprovementTable);
        setSize(280,350);
        show();
    }


    private Label createLabel() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), Color.LIGHT_GRAY);
        labelStyle.background = CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND);
        Label label = new Label(get("skill.loseImprovement"), labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.35f);
        return label;
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        Color color = getColor();
        float intensity = 0.33f;
        batch.setColor(color.r * intensity, color.g * intensity, color.b * intensity, color.a * parentAlpha);
        ((TextureRegionDrawable) getBackground()).draw(batch, x + getWidth(), y, 0, 0, getHeight(), getWidth(), 1f, 1f, 90);
    }

    private class LoseImprovementTable extends StatsTable {

        protected void addStatPair(Label label, Label value) {
            add(label).align(Align.right).padRight(5).padBottom(5);
            add(value).align(Align.left).width(65).padBottom(5);

            String skillName = label.getText().toString().split(":")[0];
            if (onlyAvailableSkill == null || onlyAvailableSkill.prettyString().equals(skillName)) {
                ImageButton imageButton = createPlusButton(value, skillName);
                add(imageButton).align(Align.left).padLeft(5).width(50).height(50).padBottom(5);
                labelImageButtonMap.put(value, imageButton);
            } else {
                add().padLeft(5).size(50).padBottom(5);
            }
            row();
        }

        @Override
        protected void fillStatValue(Label value, int base, int bonus) {
            super.fillStatValue(value, base, bonus);
            if (bonus == 0 && labelImageButtonMap.get(value) != null) {
                labelImageButtonMap.get(value).remove();
            }

        }

        private ImageButton createPlusButton(Label value, String skillName) {
            ImageButton imageButton = new ImageButton(
                    CustomAssetManager.getTextureRegionDrawable("icon/minus_normal.png"),
                    CustomAssetManager.getTextureRegionDrawable("icon/minus_checked.png"),
                    CustomAssetManager.getTextureRegionDrawable("icon/minus_checked.png")
            );
            imageButton.setTransform(true);
            imageButton.setOrigin(25, 25);
            RepeatAction repeatAction = Actions.repeat(RepeatAction.FOREVER, Actions.parallel(
                    Actions.sequence(
                            Actions.scaleTo(0.9f, 0.9f, 0.5f, Interpolation.sine),
                            Actions.scaleTo(1f, 1f, 0.5f, Interpolation.sine)
                    )
            ));
            imageButton.addAction(repeatAction);
            ButtonUtils.addClickListener(imageButton, () -> {
                for (ImageButton button : imageButtons) {
                    if (button == imageButton) {
                        button.clearListeners();
                        repeatAction.finish();
                        float remainingScale = 1 - button.getScaleX();
                        Vector2 buttonPosition = button.localToParentCoordinates(new Vector2(-10, 44));
                        button.addAction(Actions.sequence(
                                Actions.parallel(
                                        Actions.rotateTo(90, 0.25f),
                                        Actions.scaleTo(1, 1, 0.25f* remainingScale/0.1f)
                                ),
                                Actions.run(() -> showLight(buttonPosition, value, skillName)),
                                Actions.scaleTo(0,0,0.25f),
                                Actions.run(button::remove)
                        ));
                        continue;
                    }
                    button.clearListeners();
                    button.getActions().clear();
                    button.addAction(Actions.scaleTo(0,0,0.25f));
                }
            });

            imageButtons.add(imageButton);
            return imageButton;
        }
    }

    private void showLight(Vector2 position, Label value, String skillName) {
        Image image = new Image(CustomAssetManager.getTexture("icon/light.png"));
        image.setSize(280,280);
        image.setOrigin(140,140);
        image.setColor(new Color(1f, 0.33f, 0.33f, 1f));
        image.setPosition(position.x - 140, position.y - 140);
        addActor(image);
        image.setScale(0);

        float duration = 0.75f;

        image.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.parallel(
                        Actions.scaleTo(0.5f,0.5f,duration, Interpolation.sine),
                        Actions.rotateBy(-360,duration)
                ),
                Actions.run(() -> updateLabelValue(value)),
                Actions.parallel(
                        Actions.scaleTo(0.0f,0.0f,duration, Interpolation.sine),
                        Actions.rotateBy(-360,duration)
                ),
                Actions.run(() -> {
                    hide();
                    onSub.onSuccess(Stat.valueOf(skillName.toUpperCase()));
                })
        )));
    }

    private void updateLabelValue(Label value) {
        if (amountToLose == 1) {
            if (value.getText().toString().contains("+1")) { // +1 -> 0
                value.setText("[BLACK]" + value.getText().substring(7,8) + "[]");
            } else { // +2 -> +1
                value.setText("[BLACK]" + value.getText().substring(7,8) + "[#229B3C] +1[]");
            }
        } else {
            value.setText("[BLACK]" + value.getText().substring(7,8) + "[]");
        }
    }


    public void setOnSub(SingleSubscriber<? super Stat> onSub) {
        this.onSub = onSub;
    }

    public void setOnSub(CompletableSubscriber onSub) {
        this.onSub2 = onSub;
    }

    private void show() {
        displayHide.setDisplayedY(VIEWPORT_HEIGHT/2 - getHeight()/2);
        displayHide.displayOrHide().subscribe();
    }

    private void hide() {
        displayHide.displayOrHide().subscribe();
    }
}
