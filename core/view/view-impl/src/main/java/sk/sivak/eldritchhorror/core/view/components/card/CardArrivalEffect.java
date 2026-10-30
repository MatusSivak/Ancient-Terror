package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Gathering motes ignite a card's edge; it unfolds, lifts, and settles into a readable reveal. */
public final class CardArrivalEffect extends Actor {
    private final Actor card;
    private final Color accent;
    private final Texture glow = getTexture("dice_shadow.png"), white = getTexture(PURE_WHITE_BACKGROUND);
    private final Vector2 center = new Vector2();
    private float progress;

    public static void play(Actor card, CardInfo info, float x, float y, float scale) {
        CardArrivalEffect effect = new CardArrivalEffect(card, info);
        card.getParent().addActor(effect);
        card.setPosition(x, y - 24); card.setScale(scale * .10f, scale * .86f);
        card.setRotation(-9); card.getColor().a = 0;
        card.addAction(new FastForwardAction<>(new TemporalAction(1.05f * NORMAL_ACTION_DURATION) {
            @Override protected void update(float percent) {
                effect.progress = percent;
                float open = Interpolation.pow3Out.apply(MathUtils.clamp((percent - .08f) / .58f, 0, 1));
                float settle = Interpolation.sine.apply(MathUtils.clamp((percent - .58f) / .42f, 0, 1));
                card.setScale(scale * (.10f + .96f * open - .06f * settle), scale * (.86f + .18f * open - .04f * settle));
                card.setY(y - 24 * (1 - open) + 5 * (open - settle));
                card.setRotation(-9f * (1f - open) + 2f * (open - settle));
                card.getColor().a = MathUtils.clamp(percent / .22f, 0, 1);
            }
            @Override protected void end() {
                card.setPosition(x, y); card.setScale(scale); card.setRotation(0); card.getColor().a = 1;
                effect.remove();
            }
        }));
    }

    private CardArrivalEffect(Actor card, CardInfo info) {
        this.card = card;
        accent = Color.valueOf(info instanceof SpellInfo ? "A4B5FF" : info instanceof ConditionInfo ? "BA83B5"
                : info instanceof ArtifactInfo ? "F1D17B" : "C2D5AC");
        setName("card-arrival-light"); setTouchable(Touchable.disabled);
    }

    @Override public void act(float delta) {
        super.act(delta);
        if (card.getParent() != getParent()) remove();
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        float saved = batch.getPackedColor();
        card.localToParentCoordinates(center.set(CardTemplate.CARD_WIDTH / 2f, CardTemplate.CARD_HEIGHT / 2f));
        float width = CardTemplate.CARD_WIDTH * card.getScaleX(), height = CardTemplate.CARD_HEIGHT * card.getScaleY();
        float p = progress, fade = MathUtils.clamp((1-p)/.38f,0,1);
        float light = MathUtils.sin(MathUtils.PI * p) * parentAlpha;
        batch.setColor(accent.r,accent.g,accent.b,light*.16f);
        batch.draw(glow,center.x-width*.7f,center.y-height*.6f,width*1.4f,height*1.2f);
        // A brief edge flash, followed by corner highlights that clear before reading.
        float flash = Math.max(0f, 1f - Math.abs(p - .28f) / .12f);
        batch.setColor(accent.r,accent.g,accent.b,flash * .6f * parentAlpha);
        batch.draw(glow,center.x-width*.65f,center.y-height*.65f,width*1.3f,height*1.3f);
        batch.setColor(accent.r,accent.g,accent.b,light*fade*.8f);
        float corner = Math.min(width, height) * .13f;
        for (int side = -1; side <= 1; side += 2) {
            for (int top = -1; top <= 1; top += 2) {
                float x = center.x + side * (width / 2 + 3);
                float y = center.y + top * (height / 2 + 3);
                batch.draw(white, side < 0 ? x : x-corner, y, corner, 1.2f);
                batch.draw(white, x, top < 0 ? y : y-corner, 1.2f, corner);
            }
        }
        for (int i=0;i<32;i++) {
            float angle = i * 137.508f + p * 60f;
            float gather = 1f - Interpolation.pow2Out.apply(Math.min(p / .35f, 1f));
            float drift = Math.max(0f, (p - .35f) / .65f);
            float radius = 1f + gather * .65f + drift * drift * .3f;
            float x = center.x + MathUtils.cosDeg(angle) * (width/2 + 8) * radius;
            float y = center.y + MathUtils.sinDeg(angle) * (height/2 + 8) * radius + 18*drift;
            float size = 1.6f + (i%3)*.7f;
            float pulse = .55f + .45f*MathUtils.sin(p*9+i*.7f);
            batch.setColor(accent.r,accent.g,accent.b,light*fade*pulse*.85f);
            batch.draw(glow,x-size*2,y-size*2,size*4,size*4);
            batch.draw(white,x-size*.25f,y-size*.25f,size*.5f,size*.5f);
        }
        batch.setColor(saved);
    }
}
