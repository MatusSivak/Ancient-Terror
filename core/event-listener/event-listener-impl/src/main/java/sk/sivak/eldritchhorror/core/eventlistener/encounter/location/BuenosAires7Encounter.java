package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class BuenosAires7Encounter extends AbstractLocationEncounter{

    public BuenosAires7Encounter() {
        super(7, LocationEncounter.LocationEncounterType.BUENOS_AIRES);
    }

    @Override
    protected void execute() {
        new BecomeDelayedPassFlavorInfoTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getGameService().gainSpell();
            ServicePlatform.get().getGameService().gainSpell();
        }).execute();
    }

}
