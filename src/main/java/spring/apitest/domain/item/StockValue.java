package spring.apitest.domain.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StockValue {
    //@JsonProperty("item")
    //private List<Item> itemList = new ArrayList<>();
    private int totalCount;
}


