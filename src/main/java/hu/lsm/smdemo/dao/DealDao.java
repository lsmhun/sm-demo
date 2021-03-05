package hu.lsm.smdemo.dao;

import hu.lsm.smdemo.entity.Deal;

import java.util.Collection;
import java.util.Collections;

public interface DealDao {
    void save(final Deal deal);
    Collection<Deal> findAll();
}

