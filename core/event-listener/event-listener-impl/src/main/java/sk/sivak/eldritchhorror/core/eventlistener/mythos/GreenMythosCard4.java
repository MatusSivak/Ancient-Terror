package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

import java.util.List;

public class GreenMythosCard4 implements MythosCardEventListener{

    @Override
    public void execute() {
        GateColor gateColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor().toGateColor();
        List<LocationId> spawnedGates = ServicePlatform.get().getGateStackRead().getSpawnedGates(gateColor);

        if (spawnedGates.isEmpty()) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("No Gates representing current Omen.");
            ServicePlatform.get().getGameService().ask(question).subscribe();
        } else {
            ServicePlatform.get().getService().hold();
            for (LocationId spawnedGate : spawnedGates) {
                ServicePlatform.get().getService().discardGate(spawnedGate);
            }
            for (LocationId ignored : spawnedGates) {
                ServicePlatform.get().getDoomOmenService().advanceDoom();
            }
            ServicePlatform.get().getService().release();
        }
    }
}
