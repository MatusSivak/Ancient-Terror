package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SpendCluePassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class BuenosAires3Encounter extends AbstractLocationEncounter{

    public BuenosAires3Encounter() {
        super(3, LocationEncounter.LocationEncounterType.BUENOS_AIRES);
    }

    @Override
    protected void execute() {
        new SpendCluePassFlavorInfoTemplate(getTextBuilder(), () -> ServicePlatform.get().getGameService().gainSpell(SpellTrait.RITUAL))
                .withOnNoAction(() -> finishAfterFlavor(getTextBuilder().withFail().withFlavor().build()))
                .withOnMissingClue(() -> finishAfterFlavor(getTextBuilder().withFail().withFlavor(2).build())).execute();
    }
}
