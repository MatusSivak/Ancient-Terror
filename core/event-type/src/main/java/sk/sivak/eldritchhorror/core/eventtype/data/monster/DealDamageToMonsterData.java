package sk.sivak.eldritchhorror.core.eventtype.data.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class DealDamageToMonsterData {
    private MonsterInfo monsterInfo;
    private int amount;
    private boolean inCombat;
    private boolean displayOrHideMonsterCard;

    public DealDamageToMonsterData(MonsterInfo monsterInfo, int amount) {
        this.monsterInfo = monsterInfo;
        this.amount = amount;
        this.inCombat = false;
    }

    public void setDisplayOrHideMonsterCard(boolean displayOrHideMonsterCard) {
        this.displayOrHideMonsterCard = displayOrHideMonsterCard;
    }

    public boolean isDisplayOrHideMonsterCard() {
        return displayOrHideMonsterCard;
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setInCombat() {
        this.inCombat = true;
    }

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "DealDamageToMonsterData{" +
                "monsterInfo=" + monsterInfo +
                ", amount=" + amount +
                '}';
    }
}
