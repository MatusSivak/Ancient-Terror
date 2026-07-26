package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ExpeditionEncounterTemplate extends ComplexEncounterTemplate {

    public ExpeditionEncounterTemplate(EncounterTextBuilder textBuilder, Stat testStat, int modifier, EncounterTemplate passTemplate, EncounterTemplate failTemplate) {
        super(textBuilder, testStat, modifier, passTemplate, failTemplate);
    }

    @Override
    public void execute() {
        TypewriterUtils.displayTestExpeditionButton(testStat, modifier, this::onSuccess, this::onFail);
    }
}
