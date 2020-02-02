package com.sustc.masterrouter.controller;


import com.sustc.masterrouter.service.Master;
import com.sustc.masterrouter.service.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * RestAPI 访问接口
 *
 * @author Ken
 */
@RestController
class APIController {

    @Value("${file.path}")
    private String filePath;

    @Autowired
    private Master master;


    @RequestMapping("/file")
    String file() {
        master.file(new File(filePath));
        return "file!";
    }

    @RequestMapping("/start/iter={iter}&k={k1}&eva={needEva}")
    String start(@PathVariable int iter, @PathVariable int k1, @PathVariable int needEva) {
        master.start(iter, k1, needEva);
        return "start!";
    }

    @RequestMapping("/stop")
    String stop() {
        master.stop();
        return "stop!";
    }

    @RequestMapping("/query")
    String query() {
        master.query();
        return "query!";
    }
}
