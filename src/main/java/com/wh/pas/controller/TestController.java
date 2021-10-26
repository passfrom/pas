package com.wh.pas.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.wh.pas.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author ktt
 * @Date 2021/7/9 16:21
 **/

@Controller
public class TestController {

    @Autowired
    TestService testService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }


    @RequestMapping("/sql")
    @ResponseBody
    public String sql(String sql) {
        return JSONUtils.toJSONString(testService.getSql(sql));
    }

    @RequestMapping("/insert")
    public String insert() {
        return "insert";
    }


    @RequestMapping("/insertSql")
    @ResponseBody
    public String insertSql(String sql,String sqlNum,String sqlBatch) {
        long startTime = System.currentTimeMillis();
        testService.insertSql(sql,sqlNum,sqlBatch);
        return String.format("useTime:%d",(System.currentTimeMillis()-startTime))+"ms";
    }

}
