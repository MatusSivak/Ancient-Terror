package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class BuenosAires14Encounter extends AbstractLocationEncounter{

    public BuenosAires14Encounter() {
        super(14, LocationEncounter.LocationEncounterType.BUENOS_AIRES);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                () -> {
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                    ServicePlatform.get().getGameService().gainSpell(SpellTrait.RITUAL);
                },
                () -> {
                    ServicePlatform.get().getTokenService().loseSanity(1);
                    discardOneSpells();
                })
                .withoutPassFlavor()
                .withTwoPassInfos()
                .withTwoFailInfos()
                .execute();
    }

    private void discardOneSpells() {
        InvestigatorId activeInvestigatorId = getActiveInvestigatorId();
        List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(activeInvestigatorId);
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(spells);
        selectCardData.setHideText("Display Spells?");
        selectCardData.setTitleText("Select Spell to discard");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }

}
