package com.sustc.masterrouter.controller;


import com.sustc.masterrouter.domain.EvaInfo;
import com.sustc.masterrouter.domain.Evaluator;
import com.sustc.masterrouter.service.AllTime;
import com.sustc.masterrouter.service.Master;
import com.sustc.masterrouter.service.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ResultController {

    @Autowired
    Router router;

    @Autowired
    Master master;

    @Autowired
    APIController apiController;

    @Autowired
    AllTime aTime;

    @GetMapping(value = "/result")
    public String result(Model model) {
        List<EvaInfo> list = new ArrayList<>();
        for (Evaluator eva : router.getEvaluators()){
            if(eva.getEvaInfo() != null){
                list.add(eva.getEvaInfo());
            }
        }

        list.addAll(master.getEvaInfoList());

        double totalTime = 0.0;
        int evaNum = list.size();
        double avgTime = 0.0;
        int bestFitness = 0;
        List<Integer> bestSolution = new ArrayList<>();

        double allTime = 0;
        double allIter = 0;
        for (EvaInfo evaInfo : list){
            if(evaInfo.getFitness() > bestFitness) {
                bestFitness = evaInfo.getFitness();
                bestSolution = evaInfo.getSolution();
            }
            if(evaInfo.getTimecost() > totalTime) {
                totalTime = evaInfo.getTimecost();
            }
            allTime = allTime + evaInfo.getTimecost();
            allIter = allIter + evaInfo.getIteration();
        }

        if(allIter != 0){
            avgTime = allTime / allIter;
        }

        model.addAttribute("list", list);
        model.addAttribute("totalTime", totalTime);
        model.addAttribute("evaNum", evaNum);
        model.addAttribute("avgTime", avgTime);
        model.addAttribute("bestFitness", bestFitness);
        model.addAttribute("bestSolution", bestSolution);

        model.addAttribute("iter", apiController.itera);
        model.addAttribute("k", apiController.k);
        model.addAttribute("workers", apiController.workers);
        model.addAttribute("doRefresh", apiController.doRefresh);

        model.addAttribute("fileLogicTime", aTime.fileLogicTime);
        model.addAttribute("fileNetTime", aTime.fileNetTime);
        model.addAttribute("fileCount", aTime.fileCount);
        model.addAttribute("fileNetCount", aTime.fileNetCount);
        model.addAttribute("fileAvg", aTime.fileLogicTime*1.0/aTime.fileCount);
        model.addAttribute("fileNetAvg", aTime.fileNetTime*1.0/aTime.fileNetCount);

        model.addAttribute("startLogicTime1", aTime.startLogicTime1);
        model.addAttribute("startLogicTime2", aTime.startLogicTime2);
        model.addAttribute("startNetTime", aTime.startNetTime);
        model.addAttribute("startCount", aTime.startCount);
        model.addAttribute("startNetCount", aTime.startNetCount);
        model.addAttribute("startAvg", aTime.startLogicTime2*1.0/aTime.startCount);
        model.addAttribute("startNetAvg", aTime.startNetTime*1.0/aTime.startNetCount);

        model.addAttribute("startScheTime", aTime.startScheTime);
        model.addAttribute("startScheCount", aTime.startScheCount);
        model.addAttribute("scheAvg", aTime.startScheTime*1.0 / aTime.startScheCount);


        model.addAttribute("stopLogicTime", aTime.stopLogicTime);
        model.addAttribute("stopNetTime", aTime.stopNetTime);
        model.addAttribute("stopCount", aTime.stopCount);
        model.addAttribute("stopNetCount", aTime.stopNetCount);
        model.addAttribute("stopAvg", aTime.stopLogicTime*1.0/aTime.stopCount);
        model.addAttribute("stopNetAvg", aTime.stopNetTime*1.0/aTime.stopNetCount);

        model.addAttribute("queryLogicTime", aTime.queryLogicTime);
        model.addAttribute("queryNetTime", aTime.queryNetTime);
        model.addAttribute("queryCount", aTime.queryCount);
        model.addAttribute("queryNetCount", aTime.queryNetCount);
        model.addAttribute("queryAvg", aTime.queryLogicTime*1.0/aTime.queryCount);
        model.addAttribute("queryNetAvg", aTime.queryNetTime*1.0/aTime.queryNetCount);

        //System.out.println("+++++++++++++++++++++"+System.currentTimeMillis());

        return "result";
    }
}
