package sk.sivak.eldritchhorror.core.constants.monster;

public class DeepOneHybridMonster extends AbstractMonsterInfo {

    public DeepOneHybridMonster() {
        super("Deep One Hybrid",
                0, 1, -1, 1, 1);
        setSpecialText("When you defeat this Monster during a Combat Encounter, lose 1 Sanity and gain 1 random Item Asset from the deck.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Deep One\nHybrid";
    }
}
