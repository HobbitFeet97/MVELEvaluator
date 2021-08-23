package com.MVELService.evaluator.dao;

import com.MVELService.evaluator.models.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("postgres")
public class ConstantDaoService implements ConstantDao{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ConstantDaoService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Constant> getAllConstants() {
        String sql = "SELECT * FROM Constant;";
        return jdbcTemplate.query(sql, constantRowMapper());
    }

    private RowMapper<Constant> constantRowMapper(){

        return (resultSet, i) -> {
            int id = resultSet.getInt("id");
            String code = resultSet.getString("code");
            String data = resultSet.getString("data");

            return new Constant(
                    id,
                    code,
                    data
            );
        };
    }
}
