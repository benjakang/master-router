package com.sustc.masterrouter.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Evaluator的计算中间信息
 *
 */
public class EvaInfo implements Serializable {

    private double timecost;
    private int iteration;
    private double percentage;
    private List<Integer> solution;
    private int fitness;

    public EvaInfo(){}

    public EvaInfo(EvaInfo evaInfo) {
        this.timecost = evaInfo.getTimecost();
        this.iteration = evaInfo.getIteration();
        this.percentage = evaInfo.getPercentage();
        this.fitness = evaInfo.getFitness();
        this.solution = new ArrayList<>(evaInfo.getSolution());
    }

    public double getTimecost() {
        return timecost;
    }

    public void setTimecost(double timecost) {
        this.timecost = timecost;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public List<Integer> getSolution() {
        return solution;
    }

    public void setSolution(List<Integer> solution) {
        this.solution = solution;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }
}
