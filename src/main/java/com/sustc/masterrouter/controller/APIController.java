package com.sustc.masterrouter.controller;


import com.sustc.masterrouter.domain.Evaluator;
import com.sustc.masterrouter.service.AllTime;
import com.sustc.masterrouter.service.Master;
import com.sustc.masterrouter.service.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

/**
 * RestAPI 访问接口
 *
 * @author Ken
 */
@Controller
class APIController {

    @Value("${file.path}")
    private String filePath;

    @Autowired
    private Router router;

    @Autowired
    private Master master;

    @Autowired
    private AllTime allTime;

    boolean doRefresh = false;

    int itera = 1000;

    int k = 5;

    int workers = 1;


    @RequestMapping("/file")
    String file() {
        master.file(new File(filePath));
        doRefresh = false;
        //System.out.println("========================"+System.currentTimeMillis());
        return "redirect:/result";
    }

    @RequestMapping("/start/iter={iter}&k={k1}&eva={needEva}")
    String start(@PathVariable int iter, @PathVariable int k1, @PathVariable int needEva) {
        master.start(iter, k1, needEva);

        itera = iter;
        k = k1;
        workers = needEva;
        doRefresh = true;
        return "redirect:/result";
    }

    @RequestMapping("/stop")
    String stop() {
        master.stop();
        doRefresh = false;
        return "redirect:/result";
    }

    @RequestMapping("/query")
    String query() {
        master.query();
        return "redirect:/result";
    }

    @RequestMapping("/clear")
    String clear() {
        List<Evaluator> list = router.getEvaluators();
        for (Evaluator eva : list) {
            eva.setEvaInfo(null);
        }
        allTime.clear();
        master.getEvaInfoList().clear();

        doRefresh = false;
        return "redirect:/result";
    }
}
