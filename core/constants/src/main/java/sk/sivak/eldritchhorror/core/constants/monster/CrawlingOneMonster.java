package sk.sivak.eldritchhorror.core.constants.monster;

public class CrawlingOneMonster extends AbstractMonsterInfo {

    public CrawlingOneMonster() {
        super("Crawling One",
                -1, 2, 0, 3, 3);
        setSpecialText("Damage is equal to your Strength.\nMagical Resistance");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Crawling\nOne";
    }
}
