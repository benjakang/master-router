package com.sustc.masterrouter.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class MsgUtil {

    /**
     *{//M→E
     *     "type":"file"
     *     "content":{
     *     	"url":"pan.baidu.com/file.txt"
     *     	"md5":"0ca175b9c0f726a831d895e269332461"
     *     	"size":2131412   //File.getSize()
     *        }
     * 	"id":"106.128.2.1:5084"
     * }
     */
    public static JSONObject createFile(String id, String url, String md5, long size){
        JSONObject content = new JSONObject();
        content.fluentPut("url", url);
        content.fluentPut("md5", md5);
        content.fluentPut("size", size);
        return create("file", content, id);
    }

    /**
     * {//E→M
     * 	"type":"filed"
     * 	"content":{
     * 		"status":"succeed"/"failed"
     *     	"msg":"success/fail to download file"
     *        }
     * 	"id":"106.128.2.1:5084"
     * }
     *
     */

    public static JSONObject createFiled(String id, String status, String msg){
        JSONObject content = new JSONObject();
        content.fluentPut("status", status);
        content.fluentPut("msg", msg);
        return create("filed", content, id);
    }

    /**
     *{//M→E
     * 	"type":"start"
     * 	"content":{
     * 		"population":[[5,6,8],[4,8,6,9],[0,24,6],[2],[8,5]]  0~435
     * 		"iteration":100
     * 		"k":5
     *        }
     * 	"id":"106.128.2.1:5084"
     * }
     */

    public static JSONObject createStart(String id, List<List<Integer>> population,
                                     int iteration, int k){
        JSONObject content = new JSONObject();
        content.fluentPut("population", population);
        content.fluentPut("iteration", iteration);
        content.fluentPut("k", k);
        return create("start", content, id);
    }

    /**
     * {//E→M
     * 	"type":"started"
     * 	"content":{}
     * 	"id":"106.128.2.1:5084"
     * }
     */

    public static JSONObject createStarted(String id){
        JSONObject content = new JSONObject();
        return create("started", content, id);
    }

    /**
     * {//M→E
     * 	"type":"stop"
     * 	"content":{}
     * 	"id":"106.128.2.1:5084"
     * }
     */

    public static JSONObject createStop(String id){
        JSONObject content = new JSONObject();
        return create("stop", content, id);
    }

    public static JSONObject createStopped(String id, List<Integer> solution,
                                         int fitness, int iteration, double timecost){
        JSONObject content = new JSONObject();
        content.fluentPut("solution", solution);
        content.fluentPut("fitness", fitness);
        content.fluentPut("iteration", iteration);
        content.fluentPut("timecost",timecost);
        return create("stopped", content, id);
    }



    /**
     * {//M→E
     * 	"type":"query"
     * 	"content":{}
     * 	"id":"106.128.2.1:5084"
     * }
     */

    public static JSONObject createQuery(String id){
        JSONObject content = new JSONObject();
        return create("query", content, id);
    }

    /**
     * {//E→M
     * 	"type":"result"
     * 	"content":{
     * 		"solution":[5,9,6,3,4]
     * 		"fitness":96
     * 		"iteration":50
     * 		"timecost":12.364
     *        }
     * 	"id":"106.128.2.1:5084"
     * }
     */

    public static JSONObject createResult(String id, List<Integer> solution,
                                     int fitness, int iteration, double timecost){
        JSONObject content = new JSONObject();
        content.fluentPut("solution", solution);
        content.fluentPut("fitness", fitness);
        content.fluentPut("iteration", iteration);
        content.fluentPut("timecost",timecost);
        return create("result", content, id);
    }

    /**
     * {//E→M
     * 	"type":"final"
     * 	"content":{
     * 		"solution":[5,9,6,3,4]
     * 		"fitness":96
     * 		"iteration":50
     * 		"timecost":12.364
     *        }
     * 	"id":"106.128.2.1:5084"
     * }
     */

    public static JSONObject createFinal(String id, List<Integer> solution,
                                         int fitness, int iteration, double timecost){
        JSONObject content = new JSONObject();
        content.fluentPut("solution", solution);
        content.fluentPut("fitness", fitness);
        content.fluentPut("iteration", iteration);
        content.fluentPut("timecost",timecost);
        return create("final", content, id);
    }

    /**
     * {//E→M
     * 	"type":"register"
     * 	"content":{
     * 		"ip":"106.128.2.1"
     * 		"port":5084
     *        }
     * 	"id":""  //置空就行
     * }
     */



    public static JSONObject createRegister(String id, String ip, int port){
        JSONObject content = new JSONObject();
        content.fluentPut("ip", ip);
        content.fluentPut("port", port);

        return create("register", content, id);
    }

    /**
     * {//M→E
     * 	"type":"registered"
     * 	"content":{
     * 		"status":"succeed"/"failed"
     *     	"msg":"success/fail to register"
     *     	"url":null/"pan.baidu.com/file.txt"
     *        }
     * 	"id":"106.128.2.1:5084"
     * }
     */

    public static JSONObject createRegistered(String id, String status, String msg, String url){
        JSONObject content = new JSONObject();
        content.fluentPut("status", status);
        content.fluentPut("msg", msg);
        content.fluentPut("url", url);

        return create("registered", content, id);
    }

    public static JSONObject create(String type, JSONObject content, String id){
        JSONObject root = new JSONObject();
        root.fluentPut("type", type);
        root.fluentPut("content", content);
        root.fluentPut("id",id);
        return root;
    }

    public static String objToStr(JSONObject object){
        return object.toJSONString();
    }

}
