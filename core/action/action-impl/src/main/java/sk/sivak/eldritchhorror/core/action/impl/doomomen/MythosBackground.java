package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead.BackgroundDataRead;

import java.util.Locale;

import static sk.sivak.eldritchhorror.core.model.BackgroundModelRead.BackgroundType.CUSTOM;

final class MythosBackground extends BackgroundData {

    private MythosBackground(String backgroundId) {
        super(CUSTOM, backgroundId, "encounter/background/" + backgroundId + ".jpg");
    }

    static void show() {
        display(backgroundId(ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor()));
        ServicePlatform.get().getGameController().showWholeWorld().subscribe();
    }

    static void updateIfVisible(OmenColor omenColor) {
        BackgroundDataRead current = ServicePlatform.get().getModel().getBackgroundModel().getCurrentBackground();
        String backgroundId = backgroundId(omenColor);
        if (current instanceof MythosBackground && !backgroundId.equals(current.getAdditionalData())) {
            display(backgroundId);
        }
    }

    private static String backgroundId(OmenColor omenColor) {
        return "omen_" + omenColor.name().toLowerCase(Locale.ROOT);
    }

    private static void display(String backgroundId) {
        ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(new MythosBackground(backgroundId));
        ServicePlatform.get().getGameController().showCustomBackground(backgroundId).subscribe();
    }
}
