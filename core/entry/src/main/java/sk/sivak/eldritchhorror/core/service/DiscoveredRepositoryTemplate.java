package sk.sivak.eldritchhorror.core.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Base64Coder;

import java.util.LinkedList;
import java.util.List;

class DiscoveredRepositoryTemplate<E extends Enum<E>, I extends HasManyValues<E>> {
    private final Preferences preferences;
    private final I isEnum;
    private String keyName;
    private List<E> discovered;

    DiscoveredRepositoryTemplate(I isEnum) {
        this.isEnum = isEnum;
        preferences = Gdx.app.getPreferences("AncientTerror.xml");
    }

    private String getKeyName() {
        return keyName;
    }

    void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    void saveData(List<E> discovered) {
        this.discovered = discovered;
        byte[] indexesOfDiscovered = new byte[discovered.size()];
        for (int i = 0; i < discovered.size(); i++) {
            indexesOfDiscovered[i] = (byte)discovered.get(i).ordinal();
        }
        preferences.putString(getKeyName(), new String(Base64Coder.encode(indexesOfDiscovered)));
        preferences.flush();
    }

    private List<E> loadData() {
        List<E> loaded = new LinkedList<>();
        E[] values = isEnum.values();
        for (byte index : Base64Coder.decode(preferences.getString(getKeyName()))) {
            loaded.add(values[index]);
        }
        this.discovered = loaded;
        return loaded;
    }

    List<E> getDiscovered() {
        if (discovered == null) {
            loadData();
        }
        return new LinkedList<>(discovered);
    }
}
