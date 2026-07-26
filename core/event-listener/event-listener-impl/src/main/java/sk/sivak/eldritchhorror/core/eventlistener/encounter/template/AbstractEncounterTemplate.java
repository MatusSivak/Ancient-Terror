package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;

public abstract class AbstractEncounterTemplate implements EncounterTemplate {

    private final EncounterTextBuilder textBuilder;
    public AbstractEncounterTemplate(EncounterTextBuilder textBuilder) {
        this.textBuilder = textBuilder;
    }

    EncounterTextBuilder getTextBuilder() {
        return textBuilder;
    }
}
