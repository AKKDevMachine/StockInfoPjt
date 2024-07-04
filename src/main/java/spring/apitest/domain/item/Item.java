package spring.apitest.domain.item;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private String srtnCd;
    private String basDt;
    private String itmsNm;
    private String mrktCtg;
    private Long mkp;
    private Long clpr;
    private Long lopr;
    private Long hipr;
}
