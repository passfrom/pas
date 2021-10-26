package com.wh.pas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author ktt
 * @Date 2021/7/9 19:03
 **/


@Service
public class TestService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String dburl;

    @Value("${spring.datasource.dynamic.datasource.master.username}")
    private String username;

    @Value("${spring.datasource.dynamic.datasource.master.password}")
    private String password;

    String pattern = "\\{([^}]*)\\}";

    // 创建 Pattern 对象
    Pattern r = Pattern.compile(pattern);

    public List<Map<String, Object>> getSql(String sql) {
        if (sql == null || "".equals(sql)) {
            sql = "SELECT * FROM TP_GL_TA_POLLING_MAP T";
        }

        if (sql.indexOf(" drop table ") >= 0 || sql.indexOf(" DROP TABLE ") >= 0) {
            return new ArrayList<>();
        }

        return jdbcTemplate.queryForList(sql);
    }

    public void insertSql(String sql, String sqlNum, String sqlBatch) {
        int num = 0;
        int batch = 1000;
        int start = 1;
        if (!StringUtils.isEmpty(sqlNum)) {
            num = Integer.valueOf(sqlNum);
        }
        if (!StringUtils.isEmpty(sqlBatch)) {
            batch = Integer.valueOf(sqlBatch);
        }
        String[] sqls = sql.split(";");
        num = sqls.length;
        List<String> sqlList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if(("").equals(sqls[i].trim()) || null == sqls[i].trim()){
                continue;
            }
            sqlList.add(getInsertSql(sqls[i]));
            if (start % batch == 0 ) {
                jdbcTemplate.batchUpdate(sqlList.toArray(new String[]{}));
                sqlList.clear();
            }
            start++;
        }
        if (sqlList.size() > 0) {
            jdbcTemplate.batchUpdate(sqlList.toArray(new String[]{}));
        }
    }
    public void insertSqln(String sql, String sqlNum, String sqlBatch) {
        Connection conn = null;
        PreparedStatement pstm =null;
        Statement stm = null;
        ResultSet rt = null;
        int num = 0;
        int start = 1;
        int batch = 1000;
        if (!StringUtils.isEmpty(sqlBatch)) {
            batch = Integer.valueOf(sqlBatch);
        }
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dburl, username, password);
            String[] sqls = sql.split(";");
            num = sqls.length;
            for (int i = 0; i < num; i++) {
                if(("").equals(sqls[i].trim()) || null == sqls[i].trim()){
                    continue;
                }
                if(stm == null){
                    stm = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
                }
                conn.setAutoCommit(false);
                stm.addBatch(sqls[i]);
                if (start % batch == 0 ) {
                    stm.executeBatch();
                    conn.commit();
                }
                start++;
            }
            stm.executeBatch();
            conn.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(pstm!=null){
                try {
                    pstm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }
    /***
     * 获取sql
     * @Author: ktt
     * @Date: 2021/7/30
     * @Param: []
     * @return: java.lang.String
     */
    private String getInsertSql(String sql) {
        // 现在创建 matcher 对象
        Matcher m = r.matcher(sql);
        String uuid = UUID.randomUUID().toString();
        while (m.find()) {
            sql = sql.replace(m.group(0), String.format("'%s'", uuid));
        }
        return sql;
    }

    public static void main(String[] args) {


    }
}
