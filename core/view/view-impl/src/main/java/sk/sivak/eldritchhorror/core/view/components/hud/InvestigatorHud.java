package sk.sivak.eldritchhorror.core.view.components.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import rx.Completable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.after;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static sk.sivak.eldritchhorror.core.constants.GameProperties.MAX_FOCUS_TOKENS;
import static sk.sivak.eldritchhorror.core.constants.GameProperties.MAX_TRAVEL_TICKETS;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FAST_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

/**
 * @author msivak
 */
public class InvestigatorHud extends Table {

    private final ContainerBar healthBar;
    private final ContainerBar sanityBar;
    private final ContainerBar clueBar;
    private final ContainerBar focusBar;
    private final TicketContainerBar ticketBar;
    private static final float PADDING = VIEWPORT_WIDTH / 80f;

    private VerticalGroup healthSanityGroup;
    private VerticalGroup clueFocusGroup;
    private boolean displayed;
    private boolean moving;
    private boolean initialized;

    public InvestigatorHud() {
        super();
        align(Align.left);
        healthBar = createHealthBar();
        sanityBar = createSanityBar();
        clueBar = createClueBar();
        focusBar = createFocusBar();

        healthSanityGroup = createVerticalGroup(sanityBar, healthBar);
        clueFocusGroup = createVerticalGroup(clueBar, focusBar);
        ticketBar = createTicketBar();

        add(healthSanityGroup).align(Align.left);
        add(ticketBar).align(Align.left).padLeft(PADDING);
        add(clueFocusGroup).align(Align.left).padLeft(PADDING);

        padBottom(PADDING / 4);
        padLeft(PADDING / 4);
    }

    public float getHudPadding() {
        return PADDING;
    }

    public ContainerBar getHealthBar() {
        return healthBar;
    }

    public ContainerBar getSanityBar() {
        return sanityBar;
    }

    public ContainerBar getFocusBar() {
        return focusBar;
    }

    private VerticalGroup createVerticalGroup(Actor... actors) {
        VerticalGroup group = new VerticalGroup();
        group.align(Align.left);
        group.columnAlign(Align.left);
        for (Actor actor : actors) {
            group.addActor(actor);
        }
        return group;
    }

    public void recalculateSizes() {
        float previousHeight = getHeight();
        setHeight(0);
        setHeight(previousHeight);
        initialized = true;
    }

    public Completable show() {
        if (!initialized) {
            return Completable.complete();
        }
        return hideOrShow(true);
    }

    public Completable hide() {
        return hideOrShow(false);
    }

    private Completable hideOrShow(boolean show) {
        if (moving) {
            moving = false;
            displayed = show;
            clearActions();
            return Completable.complete();
        }
        if (displayed == show) {
            return Completable.complete();
        }
        return Completable.create(onSub -> {
            moving = true;
            MoveToAction action = new MoveToAction();
            if (show) {
                action.setPosition(0,0);
            } else {
                action.setPosition(0,-66);
            }
            action.setDuration(FAST_ACTION_DURATION);
            action.setTarget(this);
            addAction(after(sequence(new FastForwardAction(action), run(() -> {
                        displayed = show;
                        moving = false;
                        onSub.onCompleted();
                    })))
            );
        });
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        healthBar.setHeight(getHeight() / 2);
        sanityBar.setHeight(getHeight() / 2);
        clueBar.setHeight(getHeight() / 2);
        focusBar.setHeight(getHeight() / 2);
        ticketBar.setHeight(getHeight());
    }

    private ContainerBar createHealthBar() {
        ContainerBar bar = new ContainerBar(CustomAssetManager.getTexture(CustomAssetManager.HEALTH_ICON));
        bar.setTotalEmptyContainers(1);
        bar.setCurrentValue(0);
        return bar;
    }

    private ContainerBar createSanityBar() {
        ContainerBar bar = new ContainerBar(CustomAssetManager.getTexture(CustomAssetManager.SANITY_ICON));
        bar.setTotalEmptyContainers(1);
        bar.setCurrentValue(0);
        return bar;
    }

    private TicketContainerBar createTicketBar() {
        TicketContainerBar bar = new TicketContainerBar();
        bar.setTotalEmptyContainers(MAX_TRAVEL_TICKETS);
        return bar;
    }

    private ContainerBar createClueBar() {
        ContainerBar bar = new ContainerBar(CustomAssetManager.getTexture(CustomAssetManager.CLUE_TOKEN));
        bar.setCurrentValue(0);
        return bar;
    }

    private ContainerBar createFocusBar() {
        ContainerBar bar = new ContainerBar(CustomAssetManager.getTexture(CustomAssetManager.FOCUS_TOKEN));
        bar.setCurrentValue(0);
        bar.setTotalEmptyContainers(MAX_FOCUS_TOKENS);
        return bar;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float miniPadding = PADDING / 4;

        batch.setColor(new Color(0, 0, 0, 0.5f));
        Texture pureWhiteTexture = CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND);
        batch.draw(pureWhiteTexture,
                getX(), getY(),
                Math.max(healthBar.getPrefWidth() + 2 * miniPadding, sanityBar.getPrefWidth() + 2 * miniPadding),
                getPrefHeight());

        batch.draw(pureWhiteTexture,
                ticketBar.getX(), getY(),
                ticketBar.getPrefWidth(),
                getPrefHeight());

        batch.draw(pureWhiteTexture,
                clueFocusGroup.getX() - miniPadding, getY(),
                Math.max(focusBar.getPrefWidth() + 2 * miniPadding, clueBar.getPrefWidth() + 2 * miniPadding),
                getPrefHeight());

        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }

    public TicketContainerBar getTicketBar() {
        return ticketBar;
    }

    public ContainerBar getClueBar() {
        return clueBar;
    }
}
