package spring.apitest.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import spring.apitest.domain.item.Item;
import spring.apitest.domain.item.ItemRepository;
import spring.apitest.domain.item.StockName;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class H2StockItemRepository implements StockItemRepository{

    private JdbcTemplate template;
    private ItemRepository itemRepository;
    private final SimpleJdbcInsert jdbcInsert1;

    public H2StockItemRepository(DataSource dataSource, ItemRepository itemRepository) {
        this.template = new JdbcTemplate(dataSource);
        this.itemRepository = itemRepository;
        this.jdbcInsert1 = new SimpleJdbcInsert(dataSource)
                .withTableName("stockitem")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public void save(List<Item> itemList) {
        //String sql = "insert into stockitem values (?,?,?,?,?,?,?,?)";
        String sql2 = "select basdt from stockitem where basDt= ? limit 1";



        List<Item> all = itemRepository.findAll();
        Item item = all.get(1);
        String basDt = item.getBasDt();


        try {
            template.queryForObject(sql2, memberRowMapper(), basDt);
        } catch (EmptyResultDataAccessException e) {
            for (Item item1 : all) {
                SqlParameterSource param = new BeanPropertySqlParameterSource(item1);
                Number key = jdbcInsert1.executeAndReturnKey(param);
                item1.setId(key.longValue());
            }
        }

    }

    @Override
    public Item findItemByIdAndDate(String id, String basDt) {
        String sql = "select * from stockitem where srtnCd= ? and basDt =?";
        Item item = template.queryForObject(sql, itemRowMapper(), id, basDt);
        return item;
    }

    @Override
    public List<Item> findMkpAndDateByIdAndDate(String srtnCd, String stdate, String endDate) {
        String sql = "select mkp, basDt from stockitem where srtnCd = ? and basDt between ? and ?";
        List<Item> query = template.query(sql, memberRowMapper1(), srtnCd, stdate , endDate );
        return query;

    }

    @Override
    public List<Item> findAll(String basdDt) {
        String sql = "select * from stockitem where basDt =?";
        List<Item> query = template.query(sql, itemRowMapper(), basdDt);
        return query;
    }

    private RowMapper<Item> memberRowMapper(){
        return (rs, rowNum)->{
            Item item = new Item();
            item.setBasDt(rs.getString("basDt"));
            return item;
        };
    }

    private RowMapper<Item> memberRowMapper1(){
        return (rs, rowNum)->{
            Item item = new Item();
            item.setMkp(rs.getLong("mkp"));
            item.setBasDt(rs.getString("basDt"));
            return item;
        };
    }

    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class);
    }
}
