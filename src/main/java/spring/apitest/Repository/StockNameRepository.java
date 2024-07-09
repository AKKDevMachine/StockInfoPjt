package spring.apitest.Repository;

import spring.apitest.domain.item.Item;
import spring.apitest.domain.item.StockName;

import java.util.List;

public interface StockNameRepository {
    void save(StockName stockName);

    StockName findById(String id);

    String findCount();

    List<StockName> findAll();
}
