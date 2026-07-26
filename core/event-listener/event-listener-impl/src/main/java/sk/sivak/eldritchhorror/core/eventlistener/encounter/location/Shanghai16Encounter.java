package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Shanghai16Encounter extends AbstractLocationEncounter{

    public Shanghai16Encounter() {
        super(16, LocationEncounter.LocationEncounterType.SHANGHAI);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), this::onNo, this::onYes);
    }

    private void onNo() {
        ServicePlatform.get().getEncounterService().hold();
        TypewriterUtils.confirmInfos(
                getTextBuilder().withFail().withInfo(1).build(),
                getTextBuilder().withFail().withInfo(2).build()).subscribe(() -> {
                    ServicePlatform.get().getEncounterService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.POISONED);
                    ServicePlatform.get().getEncounterService().release();
        });
        ServicePlatform.get().getEncounterService().release();
    }


    private void onYes() {
        List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId());
        if (spells.isEmpty()) {
            TypewriterUtils.confirmInfos("Investigator is without a Spell.").subscribe(this::onNo);
        } else {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            discardOneSpells();
        }
    }

    private void discardOneSpells() {
        InvestigatorId activeInvestigatorId = getActiveInvestigatorId();
        List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(activeInvestigatorId);
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(spells);
        selectCardData.setHideText("Display Spells?");
        selectCardData.setTitleText("Select Spell to discard");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscardByChoice);
    }
}
