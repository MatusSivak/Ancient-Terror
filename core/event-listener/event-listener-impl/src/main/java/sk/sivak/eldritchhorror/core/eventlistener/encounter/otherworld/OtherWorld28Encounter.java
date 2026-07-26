package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class OtherWorld28Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld28Encounter() {
        super(28);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.INFLUENCE),
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, -1,
                        this::closeThisGateAndEnd,
                        () -> gainCondition(ConditionId.PARANOIA))
                        .withOtherWorldFlavor());

        EncounterTemplate failTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    loseSanity(1);
                    for (CardInfo ally : findAllies()) {
                        EncounterUtils.onSelectCardToDiscard(ally);
                    }
                    ServicePlatform.get().getService().release();
                }
                ).withTwoFailInfos().withOtherWorldFlavor();
        spendOneClueToResolvePassTemplate(passTemplate, failTemplate).execute();
    }

    private List<? extends CardInfo> findAllies() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ALLY);
    }
}
