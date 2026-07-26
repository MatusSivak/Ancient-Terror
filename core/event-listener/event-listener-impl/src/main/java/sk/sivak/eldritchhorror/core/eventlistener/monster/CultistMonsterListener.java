package sk.sivak.eldritchhorror.core.eventlistener.monster;

import java8.features.function.Consumer;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

public class CultistMonsterListener extends AbstractMonsterListener {

    private AbstractMonsterListener wrappedMonsterListener = null;

    @Override
    public void beforeSpawnMonsterInit(MonsterInfo monsterInfo) {
        x(wrappedMonsterListener -> wrappedMonsterListener.beforeSpawnMonsterInit(monsterInfo));
    }

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        x(wrappedMonsterListener -> wrappedMonsterListener.register(monsterInfo));
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        x(wrappedMonsterListener -> wrappedMonsterListener.justRegisterListeners(monsterInfo));
    }

    private void x(Consumer<AbstractMonsterListener> registerFunction) {
        AncientOneInfo ancientOneInfo = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo();
        if (AncientOneId.AZATHOTH == ancientOneInfo.getAncientOneId()) {
            wrappedMonsterListener = new AzathothCultistMonsterListener();
            registerFunction.accept(wrappedMonsterListener);
        } else if (AncientOneId.CTHULHU == ancientOneInfo.getAncientOneId()) {
            if (!ancientOneInfo.isAwaken()) {
                wrappedMonsterListener = new CthulhuCultistMonsterListener();
                registerFunction.accept(wrappedMonsterListener);
            } else {
                wrappedMonsterListener = new AwakenCthulhuCultistMonsterListener();
                registerFunction.accept(wrappedMonsterListener);
            }
        } else if (AncientOneId.SHUB_NIGGURATH == ancientOneInfo.getAncientOneId()) {
            if (!ancientOneInfo.isAwaken()) {
                wrappedMonsterListener = new ShubNiggurathCultistMonsterListener();
                registerFunction.accept(wrappedMonsterListener);
            } else {
                wrappedMonsterListener = new AwakenShubNiggurathCultistMonsterListener();
                registerFunction.accept(wrappedMonsterListener);
            }
        } else if (AncientOneId.YOG_SOTHOTH == ancientOneInfo.getAncientOneId()) {
            if (!ancientOneInfo.isAwaken()) {
                wrappedMonsterListener = new YogSothothCultistMonsterListener();
                registerFunction.accept(wrappedMonsterListener);
            } else {
                wrappedMonsterListener = new AwakenYogSothothCultistMonsterListener();
                registerFunction.accept(wrappedMonsterListener);
            }
        }
    }

    @Override
    public void unregister() {
        wrappedMonsterListener.unregister();
    }
}
