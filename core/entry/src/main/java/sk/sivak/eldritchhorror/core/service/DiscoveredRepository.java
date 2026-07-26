package sk.sivak.eldritchhorror.core.service;

import java.util.List;

public interface DiscoveredRepository<T> {

    void saveDiscovered(List<T> discovered);

    List<T> getDiscovered();
}
