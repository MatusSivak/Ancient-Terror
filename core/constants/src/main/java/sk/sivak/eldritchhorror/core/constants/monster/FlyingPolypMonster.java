package sk.sivak.eldritchhorror.core.constants.monster;

public class FlyingPolypMonster extends AbstractMonsterInfo {

    public FlyingPolypMonster() {
        super("Flying Polyp",
                -1, 2, -2, 2, 4);
        setReckoning(true);
        setReckoningText("Move this Monster to the nearest space containing a Gate.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Flying\nPolyp";
    }
}
