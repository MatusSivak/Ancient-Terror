package sk.sivak.eldritchhorror.core.constants.mythos;

import java.util.LinkedList;
import java.util.List;

public class AbstractMythosCard implements MythosCard{

    private final MythosColor mythosColor;
    private final List<BasicMythosAction> basicMythosActions;
    private String title;
    private String flavor;
    private List<String> infos = new LinkedList<>();
    private int id;
    private MythosDifficulty mythosDifficulty = MythosDifficulty.NORMAL;

    public AbstractMythosCard(MythosColor mythosColor, List<BasicMythosAction> basicMythosActions) {
        this.mythosColor = mythosColor;
        this.basicMythosActions = basicMythosActions;
        String[] splittedClassName = getClass().getSimpleName().split("Card");
        if (splittedClassName.length == 2) {
            setId(Integer.valueOf(splittedClassName [1]));
        }
        setTitle("-");
        setFlavor("-");
    }

    @Override
    public MythosColor getMythosColor() {
        return mythosColor;
    }

    @Override
    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    @Override
    public List<BasicMythosAction> getBasicMythosActions() {
        return basicMythosActions;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    @Override
    public String[] getInfos() {
        if (infos.isEmpty()) {
            return new String[]{"-"};
        }
        return infos.toArray(new String[0]);
    }

    public void addInfo(String info) {
        this.infos.add(info);
    }

    @Override
    public MythosDifficulty getMythosDifficulty() {
        return mythosDifficulty;
    }

    public void setMythosDifficulty(MythosDifficulty mythosDifficulty) {
        this.mythosDifficulty = mythosDifficulty;
    }
}
