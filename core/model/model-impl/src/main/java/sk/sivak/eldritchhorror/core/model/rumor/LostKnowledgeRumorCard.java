package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class LostKnowledgeRumorCard extends AbstractRumorCardInfo {

    public LostKnowledgeRumorCard() {
        setId("LostKnowledge");
        setTitleText("rumor.card.lostKnowledge.title");
        setFlavorText("rumor.card.lostKnowledge.flavor");
        setObjectiveText("rumor.card.lostKnowledge.objective");
        setFailureText("rumor.card.lostKnowledge.failure");
        setReckoningText("rumor.card.lostKnowledge.reckoning");
        setTimeRemaining(3);
        setStormSpawned(false);
        setRumorLocation(LocationId.SPACE_21);
    }
}
