package hu.lsm.smdemo.dao;

import hu.lsm.smdemo.entity.Deal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This can be replaced if we can use JpaRepository
 */
@Slf4j
@Component
public class DealDaoImpl implements DealDao {

    private List<Deal> deals = new ArrayList<>();

    @Override
    public void save(final Deal deal) {
        deals.add(deal);
    }

    @Override
    public Collection<Deal> findAll() {
        return deals;
    }

}
