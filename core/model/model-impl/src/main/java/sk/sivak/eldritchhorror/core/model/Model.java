package sk.sivak.eldritchhorror.core.model;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.reference.ReferenceInfo;
import sk.sivak.eldritchhorror.core.model.save.ModelSaveData;
import sk.sivak.eldritchhorror.core.model.save.ModelSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveData;
import sk.sivak.eldritchhorror.core.model.util.AncientOneHelper;
import sk.sivak.eldritchhorror.core.model.util.ReferenceCardHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class Model implements ModelWrite {

    private static final Logger logger = LogManager.getLogger(Model.class);

    private AncientOne activeAncientOne = new AncientOne();
    private List<AncientOneInfo> availableAncientOnes;
    private ReferenceInfo referenceCard;
    private BackgroundModel backgroundModel = new BackgroundModel();
    private int currentRound;
    private boolean reckoningTriggered;
    private DiceRollSimulator diceRollSimulator;
    private boolean mythosCardTextEffect;

    @Override
    public void init() {
        currentRound = 1;
        reckoningTriggered = false;
    }

    @Override
    public void setDiceRollSimulator(DiceRollSimulator diceRollSimulator) {
        this.diceRollSimulator = diceRollSimulator;
    }

    @Override
    public int rollDie() {
        return diceRollSimulator.rollDie();
    }

    @Override
    public List<AncientOneInfo> getAvailableAncientOnes() {
        return new LinkedList<>(availableAncientOnes);
    }

    @Override
    public void initAvailableAncientOnes() {
        availableAncientOnes = AncientOneHelper.initAvailableAncientOnes();
    }

    @Override
    public AncientOneWrite getAncientOne() {
        return activeAncientOne;
    }

    @Override
    public int getCurrentRound() {
        return currentRound;
    }

    @Override
    public void initReferenceCard(int players) {
        referenceCard = new ReferenceCardHelper().initReferenceCard(players);
        logger.debug("Reference initialized");

    }

    public ReferenceInfo getReferenceCard() {
        return referenceCard;
    }

    @Override
    public void setActiveAncientOne(AncientOneInfo ancientOneInfo) {
        activeAncientOne.setAncientOneInfo(ancientOneInfo);
    }

    @Override
    public void newRound() {
        currentRound++;
    }

    @Override
    public BackgroundModel getBackgroundModel() {
        return backgroundModel;
    }

    @Override
    public void setReckoningTriggered(boolean reckoningTriggered) {
        this.reckoningTriggered = reckoningTriggered;
    }

    @Override
    public boolean isReckoningTriggered() {
        return reckoningTriggered;
    }

    @Override
    public ModelSaveData save() {
        ModelSaveData modelSaveData = new ModelSaveData();
        modelSaveData.setCurrentRound(getCurrentRound());
        modelSaveData.setInvestigatorCount(referenceCard.getPlayers());
        modelSaveData.setDifficulty(diceRollSimulator.getDifficulty());
        return modelSaveData;
    }


    @Override
    public void load(ModelSaveDataRead saveData) {
        currentRound = saveData.getCurrentRound();
        initReferenceCard(saveData.getInvestigatorCount());
        if (saveData.getDifficulty() != null) {
            switch (saveData.getDifficulty()) {
                case EASY:
                    diceRollSimulator = new DiceRollSimulator.EasyDiceRoller();
                    break;
                case NORMAL:
                    diceRollSimulator = new DiceRollSimulator.NormalDiceRoller();
                    break;
            }
        } else {
            diceRollSimulator = new DiceRollSimulator.NormalDiceRoller();
        }

    }

    @Override
    public SaveData createSaveData() {
        return new SaveData();
    }

    @Override
    public void setMythosCardTextEffect(boolean mythosCardTextEffect) {
        this.mythosCardTextEffect = mythosCardTextEffect;
    }

    @Override
    public boolean isMythosCardTextEffect() {
        return mythosCardTextEffect;
    }

    @Override
    public String getDifficulty() {
        return diceRollSimulator.getDifficulty().name();
    }
}
