package spring.apitest.domain.item;

import org.springframework.stereotype.Repository;
import spring.apitest.domain.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {
    Map<String, Item> store = new HashMap<>();
    private static long sequence = 0L;

    public Item save(Item item){
        //item.setId(++sequence);
        store.put(item.getSrtnCd(), item);
        return item;
    }

    public List<Item> findAll(){
        return new ArrayList<>(store.values());
    }

    public Item findById(String srtnCd){
        return store.get(srtnCd);
    }

    public void clear(){
        store.clear();
    }

}
