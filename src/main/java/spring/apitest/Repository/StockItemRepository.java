package spring.apitest.Repository;

import spring.apitest.domain.item.Item;
import spring.apitest.domain.item.StockName;

import java.util.List;

public interface StockItemRepository {
    void save(List<Item> itemList);
    Item findItemByIdAndDate(String id, String basDt);
    List<Item> findMkpAndDateByIdAndDate(String id, String stDate, String endDate);

    List<Item> findAll(String basDt);
}
