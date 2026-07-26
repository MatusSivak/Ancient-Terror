package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBoltAnimationActor;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static sk.sivak.eldritchhorror.core.view.components.combat.TokenInFrame.ACTUAL_TOKEN_SIZE;

public class TokenInFrameBar extends HorizontalGroup {

    private final int count;
    private final Texture tokenTexture;
    private final List<TokenInFrame> tokenInFrameList = new LinkedList<>();
    private int coveredByDice;
    private List<Actor> detachedList = new LinkedList<>();
    private Map<Actor, Vector2> previousPositions = new HashMap<>();

    public TokenInFrameBar(int count, Texture tokenTexture) {
        align(Align.left);
        this.count = count;
        this.tokenTexture = tokenTexture;
        init();
    }

    public void setCoveredByDice(int coveredByDice) {
        this.coveredByDice = Math.min(coveredByDice, count);
    }

    public void hideBackground(float duration) {
        for (Actor child : getChildren()) {
            if (child instanceof TokenInFrame) {
                ((TokenInFrame) child).getBackground().addAction(new FastForwardAction<>(Actions.alpha(0, duration)));
            }
        }
    }

    public void showBackground(float duration) {
        for (Actor child : getChildren()) {
            if (child instanceof TokenInFrame) {
                ((TokenInFrame) child).getBackground().addAction(new FastForwardAction<>(Actions.alpha(1, duration)));
            }
        }
    }

    private void init() {
        if (count == 0) {
            addActor(new Container<>().size(ACTUAL_TOKEN_SIZE));
        }
        for (int i = 0; i < count; i++) {
            TokenInFrame tokenInFrame = new TokenInFrame(tokenTexture);
            tokenInFrameList.add(tokenInFrame);
            addActor(tokenInFrame);
        }
    }

    public Completable hideTokens(int newTotalCount) {
        return Completable.create(onSub -> {
            float duration = 1f;
            addAction(new FastForwardAction<>(
                    Actions.sequence(
                            Actions.run(() -> {
                                for (int i = newTotalCount; i < count; i++) {
                                    Actor actor = getChildren().get(i);
                                    actor.addAction(new FastForwardAction<>(Actions.scaleTo(0,0, duration)));
                                }
                            }),
                            Actions.delay(duration),
                            Actions.run(onSub::onCompleted)
                    )
            ));
        });

    }


    public int getNotCovered() {
        return count - coveredByDice;
    }

    public void highlightRemainingTokens() {
        float duration = 1f;
        float scale = 3.4f;
        int notCovered = count - coveredByDice;
        float shakeDuration = notCovered * 1.25f;
        detachedList.clear();
        for (int i = 0; i < notCovered; i++) {
            Image image = detachImage(i);
            float previousX = image.getX();
            float previousY = image.getY();
            previousPositions.put(image, new Vector2(previousX, previousY));
            ShakeAction shakeAction = new ShakeAction();
            shakeAction.setInterpolation(Interpolation.sine);
            shakeAction.setIntensity(5);
            shakeAction.setDuration(shakeDuration);

            float scaleHeightDifference = image.getHeight() * (scale-1);
            float scaleWidthDifference = image.getWidth() * (scale-1);

            float lengthOfTokens = notCovered * image.getWidth() * scale - (notCovered-1) *0.25f * image.getWidth() * scale ;

            Vector2 localCoordinates = new Vector2(
                    (ViewProperties.VIEWPORT_WIDTH - lengthOfTokens)/2 + i * image.getWidth() * 0.75f * scale + scaleWidthDifference/2,
                    ViewProperties.VIEWPORT_HEIGHT - image.getHeight() - scaleHeightDifference/2 - 5
            );

            image.addAction(new FastForwardAction<>(
                    Actions.parallel(
                            Actions.alpha(1, duration),
                            Actions.moveTo(localCoordinates.x,
                                    localCoordinates.y, duration, Interpolation.sine),
                            Actions.scaleTo(scale, scale, duration, Interpolation.sine)
                    )
            ));
        }
    }

    public void returnTokenToPlace(Actor image) {
        float duration = 1f;
        image.addAction(new FastForwardAction<>(
                Actions.sequence(
                        Actions.parallel(
                                Actions.alpha(0.8f, duration),
                                Actions.moveTo(previousPositions.get(image).x, previousPositions.get(image).y, duration, Interpolation.sine),
                                Actions.scaleTo(1, 1, duration, Interpolation.sine)
                        ),
                        Actions.run(() -> {
                            image.remove();
                            detachedList.remove(image);
                            detachedOriginalMap.get(image).setVisible(true);
                        })
                )
        ));
    }

    private Map<Actor, Image> detachedOriginalMap = new HashMap<>();

    public Image detachImage(int i) {
        Image image = tokenInFrameList.get(i).getImage();
        image.setVisible(false);
        Vector2 position = image.localToStageCoordinates(new Vector2());
        Image duplicate = new Image(image.getDrawable());
        duplicate.setWidth(image.getWidth());
        duplicate.setHeight(image.getHeight());
        duplicate.setScaling(Scaling.fit);
        duplicate.setScale(image.getScaleX(), image.getScaleY());
        duplicate.setPosition(position.x, position.y);
        getStage().addActor(duplicate);
        duplicate.setOrigin(Align.center);
        detachedList.add(duplicate);
        detachedOriginalMap.put(duplicate, image);
        return duplicate;
    }

    public List<Actor> getDetachedList() {
        return detachedList;
    }

    public List<CompositeBoltAnimationActor> createBoltsFromHighlighted(List<Vector2> endPositions, Color color) {

        List<Vector2> sources = new LinkedList<>();
        for (int i = 0; i < endPositions.size(); i++) {
            Actor detached = detachedList.get(i);

            sources.add(new Vector2(
                    detached.getX() - (detached.getScaleX()-1)*detached.getWidth()/2 + detached.getWidth()*detached.getScaleX()/2f,
                    detached.getY() - (detached.getScaleY()-1)*detached.getHeight()/2 + detached.getHeight()*detached.getScaleY()/2f));
        }

        return CombatEffects.createBoltAnimations(sources, endPositions, color);
    }
}
