package spring.apitest.Repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import spring.apitest.domain.item.Item;
import spring.apitest.domain.item.ItemRepository;
import spring.apitest.domain.item.StockName;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Repository
public class MySqlStockNameRepository implements StockNameRepository{
    private JdbcTemplate template;
    private ItemRepository itemRepository;
    private final SimpleJdbcInsert jdbcInsert2;

    public MySqlStockNameRepository(DataSource dataSource, ItemRepository itemRepository) {
        this.template = new JdbcTemplate(dataSource);
        this.itemRepository = itemRepository;
        this.jdbcInsert2 = new SimpleJdbcInsert(dataSource)
                .withTableName("stockname")
                .usingGeneratedKeyColumns("id");
    }


    @Override
    public void save(StockName stockName) {
        // 데이터베이스에서 기존 StockName 객체 조회
        String sqlSelect = "SELECT * FROM stockname WHERE srtnCd = ?";
        StockName existingStockName = null;

        try {
            existingStockName = template.queryForObject(sqlSelect, BeanPropertyRowMapper.newInstance(StockName.class), stockName.getSrtnCd());
        } catch (EmptyResultDataAccessException e) {
            // 조회된 결과가 없으면 existingStockName은 null로 유지됨
        }

        // 기존 객체와 비교하여 변경사항 확인 및 처리
        if (existingStockName == null) {
            // 기존 객체가 없을 경우 새로운 객체로 삽입
            SqlParameterSource param = new BeanPropertySqlParameterSource(stockName);
            jdbcInsert2.executeAndReturnKey(param);
            log.info("새로운 StockName 저장: {}", stockName);
        } else {
            // 기존 객체가 있을 경우, itmsNm 필드 비교 및 업데이트 처리
            if (!existingStockName.getItmsNm().equals(stockName.getItmsNm())) {
                // 이름이 변경되었을 경우, 업데이트 수행
                String sqlUpdate = "UPDATE stockname SET itmsNm = ? WHERE srtnCd = ?";
                template.update(sqlUpdate, stockName.getItmsNm(), stockName.getSrtnCd());
                log.info("StockName 업데이트: {}", stockName);
            }
        }
    }

    @Override
    public StockName findById(String id) {
        String sql = "select * from stockname where srtnCd = ?";
        StockName stockName = template.queryForObject(sql, BeanPropertyRowMapper.newInstance(StockName.class), id);
        return stockName;
    }

    @Override
    public String findCount() {
        String sql = "select count(srtnCd) from stockname";
        String count = template.queryForObject(sql, String.class);
        return count;
    }

    @Override
    public List<StockName> findAll() {
        String sql = "select * from stockname";
        List<StockName> stockNames = template.query(sql, BeanPropertyRowMapper.newInstance(StockName.class));
        return stockNames;
    }
}
