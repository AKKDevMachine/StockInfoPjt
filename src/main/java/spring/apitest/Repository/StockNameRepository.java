package spring.apitest.Repository;

import spring.apitest.domain.item.StockName;

public interface StockNameRepository {
    void save(StockName stockName);

    StockName findById(String id);

    String findCount();
}
