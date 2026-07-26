package sk.sivak.eldritchhorror.core.eventqueue;

/**
 * @author msivak
 */
public interface EventListener<DATA> {

    void onNotify(DATA eventData);

    Class<DATA> getDataClass();
}
