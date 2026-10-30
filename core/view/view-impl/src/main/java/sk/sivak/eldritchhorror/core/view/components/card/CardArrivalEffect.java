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

/** A card unfolds from an illuminated edge, settles, and sheds a few drifting sparks. */
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
        card.setRotation(0); card.getColor().a = 0;
        card.addAction(new FastForwardAction<>(new TemporalAction(1.05f * NORMAL_ACTION_DURATION) {
            @Override protected void update(float percent) {
                effect.progress = percent;
                float open = Interpolation.pow3Out.apply(MathUtils.clamp((percent - .08f) / .58f, 0, 1));
                float settle = Interpolation.sine.apply(MathUtils.clamp((percent - .58f) / .42f, 0, 1));
                card.setScale(scale * (.10f + .96f * open - .06f * settle), scale * (.86f + .18f * open - .04f * settle));
                card.setY(y - 24 * (1 - open) + 5 * (open - settle));
                card.getColor().a = MathUtils.clamp(percent / .22f, 0, 1);
            }
            @Override protected void end() {
                card.setPosition(x, y); card.setScale(scale); card.getColor().a = 1;
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
        float scanY = center.y - height/2 + height * Math.min(1,p/.72f);
        batch.setColor(accent.r,accent.g,accent.b,light*.65f);
        batch.draw(white,center.x-width/2,scanY,width,1.8f);
        for (int i=0;i<24;i++) {
            float side = i%2==0 ? -1 : 1;
            float x = center.x + side*(width/2 + 4 + 18*p*p);
            float y = center.y - height/2 + height*((i*.618034f)%1) + 24*p;
            float size = 2 + (i%3)*1.2f;
            float pulse = Math.max(0,MathUtils.sin(p*5+i*.7f));
            batch.setColor(accent.r,accent.g,accent.b,light*fade*pulse);
            batch.draw(glow,x-size,y-size,size*2,size*2);
        }
        batch.setColor(saved);
    }
}
