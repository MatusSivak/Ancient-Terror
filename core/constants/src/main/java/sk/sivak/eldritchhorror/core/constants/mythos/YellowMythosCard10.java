package sk.sivak.eldritchhorror.core.constants.mythos;

public class YellowMythosCard10 extends YellowMythosCard {

    public YellowMythosCard10() {
        setTitle("The Hour of Falling Glass");
        setFlavor("Invisible fractures spread through the air, shedding edges sharp enough to cut skin. " +
                "Investigators able to abandon their immediate plans can wait out the disturbance under cover. " +
                "Those who press on, or are already delayed and cannot make a fresh retreat, suffer the wounds instead.");
        addInfo("Each investigator:\n" +
                "-[#BAD]Loses three Health\n" +
                "unless he becomes Delayed[]");
    }
}
