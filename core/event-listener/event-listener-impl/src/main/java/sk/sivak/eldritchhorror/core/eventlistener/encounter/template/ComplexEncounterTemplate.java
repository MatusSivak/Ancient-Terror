package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.ComplexEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.ExpeditionEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public abstract class ComplexEncounterTemplate extends AbstractEncounterTemplate{

    protected final Stat testStat;
    protected final int modifier;
    private final EncounterTemplate passTemplate;
    private final EncounterTemplate failTemplate;


    public ComplexEncounterTemplate(EncounterTextBuilder textBuilder, Stat testStat, int modifier, EncounterTemplate passTemplate, EncounterTemplate failTemplate) {
        super(textBuilder);
        this.testStat = testStat;
        this.modifier = modifier;
        this.passTemplate = passTemplate;
        this.failTemplate = failTemplate;
    }

    protected void onSuccess() {
        ServicePlatform.get().getService().hold();
        ((ComplexEncounterTextBuilder) getTextBuilder()).addPassPrefix();
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFlavor().build());
        passTemplate.execute();
        ServicePlatform.get().getService().release();
    }

    protected void onFail() {
        ServicePlatform.get().getService().hold();
        ((ComplexEncounterTextBuilder) getTextBuilder()).addFailPrefix();
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFlavor().build());
        failTemplate.execute();
        ServicePlatform.get().getService().release();
    }
}
