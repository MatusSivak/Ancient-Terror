package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard5 extends YellowMythosCard {

    public YellowMythosCard5() {
        setTitle("The Ground Folds Shut");
        setFlavor("The expedition site sinks as though an enormous book were closing beneath it. " +
                "The place being explored is crushed beyond use. " +
                "Across the surrounding approaches, investigators are battered by the collapse and stranded among broken routes.");
        addInfo("-[#BAD]Expedition location is destroyed[]\n"+
                "Each investigator on the Active Expedition\n" +
                "or an adjacent space:\n" +
                "-[#BAD]Loses two Health[]\n" +
                "-[#BAD]Becomes Delayed[]\n");
    }
}
