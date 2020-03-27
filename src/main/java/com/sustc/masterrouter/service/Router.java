package com.sustc.masterrouter.service;

import com.alibaba.fastjson.JSONObject;
import com.sustc.masterrouter.domain.Evaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Router作为路由器，对Master的消息进行分发以及对Eva的消息进行接收
 *
 */

@Component
public class Router {

    private List<Evaluator> evaluators = Collections.synchronizedList(new ArrayList<>());

//    private BlockingQueue<String> registerQueue = new LinkedBlockingQueue<>();

    private BlockingQueue<String> filedQueue = new LinkedBlockingQueue<>();

    private BlockingQueue<String> startedQueue = new LinkedBlockingQueue<>();

    private BlockingQueue<String> stoppedQueue = new LinkedBlockingQueue<>();

    private BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>();

    private ExecutorService threadPoolService = Executors.newFixedThreadPool(10);

    @Autowired
    private AllTime allTime;


    public Evaluator getEvaluatorBy(String ip, int port){
        for (Evaluator eva : evaluators) {
            if (eva.getPort() == port && eva.getIp().equals(ip)){
                return eva;
            }
        }
        return null;
    }

    public void broadcastFileMsg(JSONObject json){

        for (Evaluator eva : evaluators) {
            sendMsg(json, eva.getIp(), eva.getPort(), filedQueue);
        }

    }


    public int broadcastStartMsg(JSONObject json, int needEva, Queue<List<List<Integer>>> populationQueue){

        long s = System.currentTimeMillis();

        for (Evaluator eva : evaluators) {
            if (needEva == 0) {
                return 0;
            }
            if (!eva.isStarted()) {


                List<List<Integer>> population = populationQueue.poll();
                JSONObject content = json.getJSONObject("content");
                content.put("population", population);

                sendMsg(json, eva.getIp(), eva.getPort(), startedQueue);
                eva.setStarted(true);
                needEva--;
                allTime.startScheCount ++;

            }
        }
        long e = System.currentTimeMillis();
        allTime.startScheTime += (e-s);

        return needEva;

    }

    public void broadcastStopMsg(JSONObject json){

        for (Evaluator eva : evaluators) {
            if (eva.isStarted()) {
                sendMsg(json, eva.getIp(), eva.getPort(), stoppedQueue);
                eva.setStarted(false);
            }
        }

    }

    public void broadcastQueryMsg(JSONObject json){

        for (Evaluator eva : evaluators) {
            if (eva.isStarted()) {
                sendMsg(json, eva.getIp(), eva.getPort(), resultQueue);
            }
        }

    }

    /**
     * send Massage to one Evaluator.
     * @param json
     * @param ip
     * @param port
     */

    public void sendMsg(JSONObject json, String ip, Integer port, BlockingQueue<String> msgQueue){

        json.fluentPut("id", ip + ":" + port);
        TCPClient tcpClient = new TCPClient(ip, port, json.toJSONString(), msgQueue, allTime);
        threadPoolService.execute(tcpClient.getRunnable());

    }

    /**
     * 判断是否所有eval全部空闲或终止
     * @return
     */
    public boolean evaIsAllStop(){
        boolean allstop = true;
        for (Evaluator e: getEvaluators()) {
            if(e.isStarted()){
                allstop = false;
                break;
            }
        }
        return allstop;
    }


    /**
     * getter and  setter
     * 
     * @return
     */


    public List<Evaluator> getEvaluators() {
        return evaluators;
    }

    public void setEvaluators(List<Evaluator> evaluators) {
        this.evaluators = evaluators;
    }

    public BlockingQueue<String> getFiledQueue() {
        return filedQueue;
    }

    public void setFiledQueue(BlockingQueue<String> filedQueue) {
        this.filedQueue = filedQueue;
    }

    public BlockingQueue<String> getStartedQueue() {
        return startedQueue;
    }

    public void setStartedQueue(BlockingQueue<String> startedQueue) {
        this.startedQueue = startedQueue;
    }

    public BlockingQueue<String> getStoppedQueue() {
        return stoppedQueue;
    }

    public void setStoppedQueue(BlockingQueue<String> stoppedQueue) {
        this.stoppedQueue = stoppedQueue;
    }

    public BlockingQueue<String> getResultQueue() {
        return resultQueue;
    }

    public void setResultQueue(BlockingQueue<String> resultQueue) {
        this.resultQueue = resultQueue;
    }

    public ExecutorService getThreadPoolService() {
        return threadPoolService;
    }

    public void setThreadPoolService(ExecutorService threadPoolService) {
        this.threadPoolService = threadPoolService;
    }

}
