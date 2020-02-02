package com.sustc.masterrouter.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sustc.masterrouter.domain.EvaInfo;
import com.sustc.masterrouter.domain.Evaluator;
import com.sustc.masterrouter.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.*;

@Component
public class Master {

    @Autowired
    Router router;

    @Value("${master.port}")
    int masterPort;

    @Value("${master.ip}")
    String masterIp;

    private TCPServer tcpServer;

    private int leftNeedEva;

    private int iteration;

    private int k;

    private List<EvaInfo> evaInfoList = new ArrayList<>();

    private boolean isAutoQuery;

    private String fileURL = "accident.txt";

    private ExecutorService autoQueryPoolService = Executors.newFixedThreadPool(1);

    /**
     * 将本地文件上传给云盘
     * @Param: []
     * @return: void
     * @Author: benjakang
     * @Date: 2020/1/4
     *
     *
     */
    public void file(File f){
        //向云盘上传文件
        //upload(f,cloudURL);
        //生成MD5
        String md5 = null;
        try {
            md5 = MD5Util.getFileMD5String(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //打包成file消息

        JSONObject fileJson = MsgUtil.createFile("", fileURL, md5, f.length());
        router.broadcastFileMsg(fileJson);

        //接收filed消息
        for (int i = 0; i < router.getEvaluators().size(); i++) {
            String filedMsg = null;
            try {
                filedMsg = router.getFiledQueue().poll(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(filedMsg == null){
                break;
            }
            JSONObject filedJson = (JSONObject) JSONUtil.stringToBean(filedMsg, new Object());
            String id = filedJson.getString("id");

            System.out.println("收到" + id + "主机的filed消息");
            System.out.println("filed消息为：" + filedJson.toJSONString());

        }
    }

    /**
     *
     * }
     * @param needEva
     *
     */

    public void start(int iter, int k1, int needEva){

        iteration = iter;
        k = k1;

        //生成随机population(该关键字丢给broadcast方法生成)
        List<List<Integer>> population = null;

        //打包成start消息并广播
        JSONObject startJson = MsgUtil.createStart("", population, iteration, k);
        leftNeedEva = router.broadcastStartMsg(startJson, needEva, this);

        //接收started消息
        for (int i = 0; i < needEva; i++) {
            String startedMsg = null;
            try {
                startedMsg = router.getStartedQueue().poll(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(startedMsg == null){
                break;
            }
            //接收到started消息，即开始自动查询所有Eva
            isAutoQuery = true;
            autoQuery(400);

            JSONObject startedJson = (JSONObject) JSONUtil.stringToBean(startedMsg, new Object());
            String id = startedJson.getString("id");
//            String ip = id.split(":")[0];
//            int port = Integer.valueOf(id.split(":")[1]);

            System.out.println("收到" + id + "主机的started消息");
            System.out.println("started消息为：" + startedJson.toJSONString());

        }
    }

    /**
     * {//M→E
     * 	"type":"stop"
     * 	"content":{}
     * }
     */
    public void stop(){

        //打包成stop消息
        JSONObject stopJson = MsgUtil.createStop("");
        router.broadcastStopMsg(stopJson);
        leftNeedEva = 0;

        //接收stopped消息
        for (int i = 0; i < router.getEvaluators().size(); i++) {
            String stoppedMsg = null;
            try {
                stoppedMsg = router.getStoppedQueue().poll(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(stoppedMsg == null){
                break;
            }
            isAutoQuery = false;
            JSONObject stoppedJson = (JSONObject) JSONUtil.stringToBean(stoppedMsg, new Object());
            String id = stoppedJson.getString("id");
//            String ip = id.split(":")[0];
//            int port = Integer.valueOf(id.split(":")[1]);

            System.out.println("收到" + id + "主机的stopped信息");
        }

        evaInfoList.clear();
    }

    /**
     * {//M→E
     * 	"type":"query"
     * 	"content":{}
     * }
     */

    public void query(){

        //打包成query消息并发送
        JSONObject queryJson = MsgUtil.createQuery("");
        router.broadcastQueryMsg(queryJson);

        //接收消息
        for (int i = 0; i < router.getEvaluators().size(); i++) {
            String resultMsg = null;
            try {
                resultMsg = router.getResultQueue().poll(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(resultMsg == null){
                break;
            }
            JSONObject resultJson = (JSONObject) JSONUtil.stringToBean(resultMsg, new Object());
            String id = resultJson.getString("id");

            String ip = id.split(":")[0];
            int port = Integer.valueOf(id.split(":")[1]);

            System.out.println("收到" + id + "主机的"+ resultJson.getString("type") +"信息\n"
                    + resultJson.getString("type") + "消息为："+resultJson.toJSONString());
            Evaluator eva = router.getEvaluatorBy(ip, port);

            //返回final消息，说明有Eva空闲，如果needEva！=0,再次启动start
            if (resultJson.getString("type").equals("final")) {
                eva.setStarted(false);
                if (leftNeedEva > 0) {
                    start(iteration, k, leftNeedEva);
                }
            }

            JSONObject content = resultJson.getObject("content", JSONObject.class);
            List<Integer> solution = jsonArrayToList(content.getJSONArray("solution"));
            int fitness = content.getInteger("fitness");
            int iter = content.getInteger("iteration");
            double timecost = content.getDouble("timecost");

            if (eva.getEvaInfo() == null) {
                eva.setEvaInfo(new EvaInfo());
            }

            EvaInfo evaInfo = eva.getEvaInfo();

            //上一次query不为初始值，当前为初始值，则表示完成了一个任务。
            if(evaInfo.getIteration() != 0 && iter == 0) {
                evaInfoList.add(new EvaInfo(evaInfo));
            }


            evaInfo.setSolution(solution);
            evaInfo.setPercentage(1.0 * iter / iteration);
            evaInfo.setFitness(fitness);
            evaInfo.setIteration(iter);
            evaInfo.setTimecost(timecost);
        }

    }
    public void autoQuery(long interval){
        Runnable runnable = () -> {
            while (isAutoQuery) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                query();
            }
        };
        autoQueryPoolService.execute(runnable);
    }

    public void simpleQuery(){
        List<Evaluator> evaluators = router.getEvaluators();
        for (Evaluator eva : evaluators) {
            if(eva.getEvaInfo() != null){

                EvaInfo evaInfo = eva.getEvaInfo();

                System.out.println(JSONUtil.beanToJson(evaInfo));
            }
        }
    }

    public String registered(String registerMsg) {
        JSONObject registerJson = (JSONObject) JSONUtil.stringToBean(registerMsg, new Object());
        JSONObject content = registerJson.getObject("content", JSONObject.class);
        String ip = content.getString("ip");
        int port = content.getInteger("port");

        Evaluator e = router.getEvaluatorBy(ip,port);
        if(e == null) {
            router.getEvaluators().add(new Evaluator(ip, port));
        }

        JSONObject resgisteredJson = MsgUtil.createRegistered(ip+":"+port, "success", "success to register", fileURL);
        registerJson.fluentPut("id", ip + ":" + port);

        return resgisteredJson.toJSONString();
    }


    /**
     * start a TCPServer to listen the connection of evaluators.
     *
     */
    @PostConstruct
    public Thread startTCPServer(){

        if (tcpServer == null){
            tcpServer = new TCPServer(masterPort, this);
        }
        try {
            tcpServer.start();
            System.out.println("启动register成功，ip:"+ InetAddress.getLocalHost().getHostAddress()+", port:"+masterPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tcpServer.getThread();
//        registerThread.start(9999);
    }


    /**
     * stop a TCPServer to listen the connection of evaluators.
     *
     */

    public void stopTCPServer(){
        try {
            tcpServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<List<Integer>> generatePopulation(int len, int k1) {
        List<List<Integer>> population = new ArrayList<>();
        population.add(new ArrayList<>());

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            list.add(i);
        }
        for (int i = 1; i < k1; i++) {

            Collections.shuffle(list);
            List<Integer> solution = new ArrayList<>();
            int length = new Random().nextInt(k1) % k1  + 1;
            for (int j = 0; j < length; j++) {
                solution.add(list.get(j));
            }
            population.add(solution);
        }
        return population;
    }

    public List<Integer> jsonArrayToList(JSONArray jsonArray){
        Object[] arr = jsonArray.toArray();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add((Integer) arr[i]);
        }
        return list;
    }


    public int getLeftNeedEva() {
        return leftNeedEva;
    }

    public void setLeftNeedEva(int leftNeedEva) {
        this.leftNeedEva = leftNeedEva;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public List<EvaInfo> getEvaInfoList() {
        return evaInfoList;
    }

    public void setEvaInfoList(List<EvaInfo> evaInfoList) {
        this.evaInfoList = evaInfoList;
    }
}
