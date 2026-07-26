package sk.sivak.eldritchhorror.core.constants.test;

import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

/**
 * @author msivak
 */
public class UsableAsset {

    private CardInfo cardInfo;
    private int dicePoolBonus;
    private int statBonus;
    private boolean upByDefault;
    private Action0 onUseAction;

    public void setOnUseAction(Action0 onUseAction) {
        this.onUseAction = onUseAction;
    }

    public void runOnUseAction() {
        if (onUseAction != null) {
            onUseAction.call();
        }
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public int getDicePoolBonus() {
        return dicePoolBonus;
    }

    public void setDicePoolBonus(int dicePoolBonus) {
        this.dicePoolBonus = dicePoolBonus;
    }

    public int getStatBonus() {
        return statBonus;
    }

    public void setStatBonus(int statBonus) {
        this.statBonus = statBonus;
    }

    public boolean isUpByDefault() {
        return upByDefault;
    }

    public void setUpByDefault(boolean upByDefault) {
        this.upByDefault = upByDefault;
    }
}
