package com.sustc.masterrouter.service;

import com.alibaba.fastjson.JSONObject;
import com.sustc.masterrouter.utils.MillisecondClock;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class TCPClient {

    private String ip;
    private Integer port;
    private String msg;
    private BlockingQueue<String> msgQueue;
    private AllTime allTime;

    public TCPClient(String ip, Integer port, String msg, BlockingQueue<String> msgQueue, AllTime allTime){
        this.ip = ip;
        this.port = port;
        this.msg = msg;
        this.msgQueue = msgQueue;
        this.allTime = allTime;
    }


    public Runnable getRunnable() {

        return new ClientThread(ip,port,msg, msgQueue, allTime);
    }

    class ClientThread implements Runnable{

        private String ip;
        private Integer port;
        private String msg;
        private BlockingQueue<String> msgQueue;
        private AllTime allTime;

        public ClientThread(String ip, Integer port, String msg, BlockingQueue<String> msgQueue, AllTime allTime){
            this.ip = ip;
            this.port = port;
            this.msg = msg;
            this.msgQueue = msgQueue;
            this.allTime = allTime;
        }

        @Override
        public void run() {
            long s = MillisecondClock.CLOCK.now();

            try {
//                System.out.println(ip +":"+ port);
                Socket socket = new Socket(ip, port);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pw.write(msg);
                pw.flush();
                socket.shutdownOutput();

                //4.读取以及响应
                String line = "";
                StringBuilder info = new StringBuilder();
                while ( (line = br.readLine()) != null ){
                    info.append(line);
                }
                try {
                    msgQueue.put(new String(info));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                pw.close();
                br.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            long e = MillisecondClock.CLOCK.now();
            JSONObject jsonObject = JSONObject.parseObject(msg, JSONObject.class);
            String type = jsonObject.getString("type");

            if (type.equals("file")){
                allTime.fileNetTime += (e-s);
                allTime.fileNetCount ++;
            }else if (type.equals("start")){
                allTime.startNetTime += (e-s);
                allTime.startNetCount ++;
            }else if (type.equals("stop")){
                allTime.stopNetTime += (e-s);
                allTime.stopNetCount ++;
            }else if (type.equals("query")){
                allTime.queryNetTime += (e-s);
                allTime.queryNetCount ++;
            }
        }
    }
}
