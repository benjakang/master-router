package com.sustc.masterrouter.controller;


import com.sustc.masterrouter.domain.EvaInfo;
import com.sustc.masterrouter.domain.Evaluator;
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

        return "result";
    }
}
