package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.graphics.g2d.Batch;
import sk.sivak.eldritchhorror.core.view.components.tutorial.SafeTypingLabel;

class BoldSafeTypingLabel extends SafeTypingLabel {

    private static final float BOLD_OFFSET_X = 0.55f;
    private static final float SHADOW_OFFSET_X = 1.2f;
    private static final float SHADOW_OFFSET_Y = -1.2f;
    private static final float SHADOW_ALPHA = 0.62f;

    BoldSafeTypingLabel(CharSequence text, LabelStyle style) {
        super(text, style);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float originalX = getX();
        float originalY = getY();
        float originalR = batch.getColor().r;
        float originalG = batch.getColor().g;
        float originalB = batch.getColor().b;
        float originalA = batch.getColor().a;

        batch.setColor(0f, 0f, 0f, originalA * SHADOW_ALPHA);
        setPosition(originalX + SHADOW_OFFSET_X, originalY + SHADOW_OFFSET_Y);
        super.draw(batch, parentAlpha);
        setPosition(originalX - SHADOW_OFFSET_X, originalY + SHADOW_OFFSET_Y);
        super.draw(batch, parentAlpha);
        setPosition(originalX + SHADOW_OFFSET_X, originalY);
        super.draw(batch, parentAlpha);
        setPosition(originalX - SHADOW_OFFSET_X, originalY);
        super.draw(batch, parentAlpha);

        batch.setColor(originalR, originalG, originalB, originalA);
        setPosition(originalX, originalY);
        super.draw(batch, parentAlpha);

        setPosition(originalX + BOLD_OFFSET_X, originalY);
        super.draw(batch, parentAlpha);
        setPosition(originalX, originalY);
    }
}
