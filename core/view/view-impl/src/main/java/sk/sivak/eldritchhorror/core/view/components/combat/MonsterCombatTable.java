package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.combat.MonsterCombatTableData;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBoltAnimationActor;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.ToughnessBar;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

public class MonsterCombatTable extends VisTable {

    public static final int X_POSITION = 405;
    public static final int COLUMNS_WIDTH = 170;
    private TokenInFrameBar horrorBar;
    private TokenInFrameBar damageBar;
    private ToughnessBar toughnessBar;
    private MonsterCombatTableData data;

    private Integer remainingHorror;
    private Integer remainingDamage;

    private boolean centered = false;
    private Cell<TokenInFrameBar> damageBarCell;
    private Cell<TokenInFrameBar> horrorBarCell;
    private FireballService fireballService;

    public boolean isCentered() {
        return centered;
    }

    public void init(MonsterCombatTableData data) {
        this.data = data;
        Table iconAndNameTable = createIconAndNameTable(data);
        Table horrorDamageToughnessTable = createHorrorDamageToughnessTable(data);

        add(iconAndNameTable).pad(5).width(COLUMNS_WIDTH).height(horrorDamageToughnessTable.getHeight());
        addSeparator(true).padTop(0).padBottom(0);
        add(horrorDamageToughnessTable).pad(5).width(horrorDamageToughnessTable.getWidth()).height(horrorDamageToughnessTable.getHeight());


        setWidth(COLUMNS_WIDTH + horrorDamageToughnessTable.getWidth() + 20);
        setHeight(horrorDamageToughnessTable.getHeight() + 10);
        setBackground(CustomAssetManager.getTextureRegionDrawable(WHITE_BACKGROUND));

        fireballService = new FireballService(getStage());

        ButtonUtils.addClickListener(this, () -> {
            BigActorsManager.initMonsterCard(data.getMonsterInfo(), BigActorsManager::displayOrHideMonsterCard, () -> {});
            BigActorsManager.displayOrHideMonsterCard();
        });

    }

    public Completable destroySanity(List<Vector2> endPositions) {
        return Completable.create(onSub -> {
            addBoltsTargetingInvestigatorBar(horrorBar, endPositions, onSub);


        });
    }

    public Completable destroyHealth(List<Vector2> endPositions) {
        return Completable.create(onSub -> {
            addBoltsTargetingInvestigatorBar(damageBar, endPositions, onSub);
        });
    }

    public void addBoltsTargetingInvestigatorBar(TokenInFrameBar sourceBar, List<Vector2> endPositions, CompletableSubscriber onSub) {
        sourceBar.highlightRemainingTokens();
        addAction(new FastForwardAction<>(
                Actions.sequence(
                        Actions.run(()-> moveRight().subscribe()),
                        Actions.delay(1f),
                        Actions.run(() -> {
                            fireballService.reset();
                            fireballService.setTargetHealth(3);
                            List<Vector2> sources = new LinkedList<>();
                            Set<Vector2> landedFireballs = new HashSet<>();
                            for (Actor child : sourceBar.getDetachedList()) {
                                Vector2 source = new Vector2(
                                        child.getX() - (child.getScaleX() - 1) * child.getWidth() / 2 + child.getWidth() * child.getScaleX() / 2f,
                                        child.getY() - (child.getScaleY() - 1) * child.getHeight() / 2 + child.getHeight() * child.getScaleY() / 2f);
                                sources.add(source);
                                fireballService.setOnThrowAction(source, () -> {
                                    ShakeAction shakeAction = new ShakeAction();
                                    shakeAction.setInterpolation(Interpolation.sine);
                                    shakeAction.setIntensity(10);
                                    shakeAction.setDuration(0.25f);
                                    child.addAction(new FastForwardAction<>(shakeAction));
                                });

                                fireballService.setOnLastThrowActions(source, () -> {
                                    addAction(new FastForwardAction<>(
                                            Actions.delay(0.25f, Actions.run(() -> { // lets wait for shaking to finish
                                                sourceBar.returnTokenToPlace(child);
                                                moveLeft().subscribe();
                                            }))
                                    ));
                                });
                            }


                            for (Vector2 target : endPositions) {
                                fireballService.setOnLandAction(target, () -> {
                                    landedFireballs.add(target);
                                    if (landedFireballs.size() == endPositions.size()) {
                                        onSub.onCompleted();
                                    }
                                });
                            }
                            fireballService.setMidpointDisplacementDirection(1);
                            fireballService.launchFireballs(sources, endPositions);
                        })
                )
        ));
    }

    public Completable moveRight() {
        return Completable.create(onSub -> {
            centered = false;
            if (getX() > 700) { // just a stupid bug for bootlegger
                moveLeft().subscribe(onSub::onCompleted);
                return;
            }
            addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.moveTo(ViewProperties.VIEWPORT_WIDTH - COLUMNS_WIDTH - 10, getY(), 1f, Interpolation.sine),
                    Actions.run(onSub::onCompleted)
            )));
        });
    }

    private boolean movingLeft = false;
    public Completable moveLeft() {
        return Completable.create(onSub -> {
            if (movingLeft) {
                onSub.onCompleted();
                return;
            }
            movingLeft = true;
            addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.moveTo(X_POSITION, getY(), 1f, Interpolation.sine),
                    Actions.run(() -> {
                        centered = X_POSITION == getX();
                        movingLeft = false;
                        onSub.onCompleted();
                    })
            )));
        });
    }

    public Completable destroyHorror(List<Vector2> dicePositions) {
        return Completable.create(onSub -> {
            remainingHorror = data.getHorror() - dicePositions.size();
            destroyHorrorOrDamage(dicePositions, onSub, horrorBar);
        });
    }


    public Completable destroyDamage(List<Vector2> dicePositions) {
        return Completable.create(onSub -> {
            remainingDamage = data.getDamage() - dicePositions.size();
            destroyHorrorOrDamage(dicePositions, onSub, damageBar);
        });
    }

    private void destroyHorrorOrDamage(List<Vector2> dicePositions, CompletableSubscriber onSub, TokenInFrameBar tokenInFrameBar) {
        if (tokenInFrameBar.getChildren().size == 1 && tokenInFrameBar.getChildren().get(0) instanceof Container) {
            onSub.onCompleted();
            return;
        }
        tokenInFrameBar.setCoveredByDice(dicePositions.size());
        List<Vector2> targets = new LinkedList<>();
        int targetsCount;
        if (dicePositions.size() >= tokenInFrameBar.getChildren().size) {
            targetsCount = tokenInFrameBar.getChildren().size;
        } else {
            targetsCount = dicePositions.size();
        }
        Map<Vector2, Float> targetScaleMap = new HashMap<>();
        int targetHealth = 3;
        fireballService.setTargetHealth(targetHealth);
        List<Actor> detachedImages = new LinkedList<>();
        for (int i = tokenInFrameBar.getChildren().size-1; i >= tokenInFrameBar.getChildren().size - targetsCount; i--) {
            detachedImages.add(tokenInFrameBar.detachImage(i));
        }
        fireballService.reset();
        for (Actor child : detachedImages) {
            Vector2 childCoordinates = child.localToStageCoordinates(new Vector2());
            Vector2 target = new Vector2(childCoordinates.x + child.getWidth() / 2f, childCoordinates.y + child.getHeight() / 2f);
            targets.add(target);
            targetScaleMap.put(target, 1f);
            fireballService.setOnLandAction(target, () -> {
                Float previousScale = targetScaleMap.get(target);
                float newScale = Math.max(0, previousScale - 1/(float)targetHealth);
                targetScaleMap.put(target, newScale);
                child.setScale(newScale);
            });
            fireballService.setOnLandLastAction(target, child::remove);
        }
        fireballService.setOnLandLastAction(onSub::onCompleted);
        fireballService.setMidpointDisplacementDirection(-1);
        fireballService.launchFireballs(dicePositions, targets);
    }


    public Completable destroyMonsterHealth(List<Vector2> dicePositions) {
        return Completable.create(onSub -> {


            List<Vector2> targets = new LinkedList<>();
            int targetsCount = Math.min(dicePositions.size(), toughnessBar.getCurrentHealth());
            int blankHealth = toughnessBar.getChildren().size - toughnessBar.getCurrentHealth();

            int targetHealth = 3;
            fireballService.setTargetHealth(targetHealth);
            List<Actor> healthIcons = new LinkedList<>();
            int from = toughnessBar.getChildren().size - 1 - blankHealth;
            int to = from - targetsCount;
            for (int i = from; i > to; i--) {
                healthIcons.add(toughnessBar.getChildren().get(i));
            }

            fireballService.weakReset();
            for (Actor child : healthIcons) {
                Vector2 childCoordinates = child.localToStageCoordinates(new Vector2());
                Vector2 target = new Vector2(childCoordinates.x + child.getWidth() / 2f, childCoordinates.y + child.getHeight() / 2f);
                targets.add(target);
                fireballService.setOnLandAction(target, () -> {
                    float tokenWidth = 100;
                    float baseOffset = - (healthIcons.size() - 1) * tokenWidth / 2f;
                    int indexOfChild = healthIcons.indexOf(child);
                    float offsetX = baseOffset + indexOfChild * tokenWidth;
                    toughnessBar.loseToughnessNew(child, offsetX).subscribe();
                });
            }
            fireballService.setOnLandLastAction(onSub::onCompleted);
            fireballService.setMidpointDisplacementDirection(-1);
            fireballService.launchFireballs(dicePositions, targets);
        });
    }

    public void highlightHorror() {
        horrorBar.showBackground(1f);
        damageBar.hideBackground(1f);

        horrorBar.addAction(new FastForwardAction<>(Actions.alpha(1, 1f)));
        damageBar.addAction(new FastForwardAction<>(Actions.alpha(0.25f, 1f)));
        toughnessBar.addAction(new FastForwardAction<>(Actions.alpha(0.25f, 1f)));
    }

    public void highlightDamageAndToughness() {
        horrorBar.hideBackground(1f);
        damageBar.showBackground(1f);

        horrorBar.addAction(new FastForwardAction<>(Actions.alpha(0.25f, 1f)));
        damageBar.addAction(new FastForwardAction<>(Actions.alpha(1, 1f)));
        toughnessBar.addAction(new FastForwardAction<>(Actions.alpha(1f, 1f)));
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        Color colorBefore = new Color(getColor());
        setColor(new Color(0f, 0f, 0f, colorBefore.a));
        super.drawBackground(batch, parentAlpha * 0.8f, x, y);
        setColor(colorBefore);
    }

    private Table createIconAndNameTable(MonsterCombatTableData data) {
        Table iconAndNameTable = new Table();

        Image monsterImage;
        if (data.isMonsterEpic()) {
            monsterImage = new Image(CustomAssetManager.getEpicMonsterTexture(data.getMonsterClassName()));
        } else {
            monsterImage = new Image(CustomAssetManager.getNonEpicMonsterTexture(data.getMonsterClassName()));
        }
        monsterImage.setScaling(Scaling.fit);
        iconAndNameTable.add(monsterImage).padTop(5).grow().row();
        iconAndNameTable.add(createNameLabel(data.getMonsterName())).width(COLUMNS_WIDTH).row();

        iconAndNameTable.setBackground(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND));
        iconAndNameTable.pack();
        return iconAndNameTable;
    }

    private Table createHorrorDamageToughnessTable(MonsterCombatTableData data) {
        horrorBar = new TokenInFrameBar(data.getHorror()!=null?data.getHorror():0, CustomAssetManager.getTexture("combat/horror.png"));
        damageBar = new TokenInFrameBar(data.getDamage()!=null?data.getDamage():0, CustomAssetManager.getTexture("combat/damage.png"));

        float toughnessBarScale = 0.51f;
        toughnessBar = new ToughnessBar();
        toughnessBar.init(
                data.getToughness() == null ? 0 : data.getToughness(),
                data.getCurrentHealth() == null ? 0 : data.getCurrentHealth(), toughnessBarScale);

        Table horrorDamageToughnessTable = new Table();
        horrorDamageToughnessTable.align(Align.left);
        horrorBarCell = horrorDamageToughnessTable.add(horrorBar).align(Align.left);
        horrorBarCell.row();
        damageBarCell = horrorDamageToughnessTable.add(damageBar).align(Align.left);
        damageBarCell.row();
        horrorDamageToughnessTable.add(toughnessBar).height(53 * toughnessBarScale).padTop(5).align(Align.left);

        horrorDamageToughnessTable.pack();
        return horrorDamageToughnessTable;
    }

    private Label createNameLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), Color.LIGHT_GRAY);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.35f);
        return label;
    }

    public int getRemainingDamage() {
        return remainingDamage != null ? remainingDamage : data.getDamage();
    }

    public void setCentered() {
        this.centered = true;
    }

    public Completable updateDamage(Integer damage) {
        if (damage.equals(data.getDamage())) {
            return Completable.complete();
        }
        return Completable.create(onSub -> {
            data.setDamage(damage);
            damageBar.hideTokens(damage).subscribe(() -> {
                damageBar.remove();
                damageBar = new TokenInFrameBar(data.getDamage()!=null?data.getDamage():0, CustomAssetManager.getTexture("combat/damage.png"));
                damageBarCell.setActor(damageBar);
                onSub.onCompleted();
            });
        });
    }

    public Completable updateHorror(Integer horror) {
        if (horror.equals(data.getHorror())) {
            return Completable.complete();
        }
        return Completable.create(onSub -> {
            data.setHorror(horror);
            horrorBar.hideTokens(horror).subscribe(() -> {
                horrorBar.remove();
                horrorBar = new TokenInFrameBar(data.getHorror()!=null?data.getHorror():0, CustomAssetManager.getTexture("combat/horror.png"));
                horrorBarCell.setActor(horrorBar);
                onSub.onCompleted();
            });
        });
    }

    private boolean locked = false;
    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
