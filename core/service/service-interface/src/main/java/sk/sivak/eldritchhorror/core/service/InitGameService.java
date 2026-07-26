package sk.sivak.eldritchhorror.core.service;

/**
 * @author msivak
 */
public interface InitGameService extends CommonService {

    void initGame();

    void registerBasicActions();

    void loadGame();

    void registerCurrentMysteryCard();

    void unregisterCurrentMysteryCard();

    void saveGame();

    void initAncientOne();

}
