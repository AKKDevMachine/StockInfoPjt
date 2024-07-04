package spring.apitest.Repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import spring.apitest.domain.item.ItemRepository;
import spring.apitest.domain.item.StockName;
import spring.apitest.domain.member.Member;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class MySqlMemberRepository {
    private JdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    public MySqlMemberRepository(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("member")
                .usingGeneratedKeyColumns("id");
    }

    public void save(Member member){
        SqlParameterSource param = new BeanPropertySqlParameterSource(member);
        jdbcInsert.executeAndReturnKey(param);
    }

    public Optional<Member> findByLoginId(String loginId){

        return findAll().stream()
                .filter(m->m.getLoginId().equals(loginId))
                .findFirst();
    }

    public List<Member> findAll(){
        String sql = "select * from member";
        List<Member> members = template.query(sql, BeanPropertyRowMapper.newInstance(Member.class));
        return members;
    }


}
