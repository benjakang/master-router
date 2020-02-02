package com.sustc.masterrouter.service;

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

    public TCPClient(String ip, Integer port, String msg, BlockingQueue<String> msgQueue){
        this.ip = ip;
        this.port = port;
        this.msg = msg;
        this.msgQueue = msgQueue;
    }


    public Runnable getRunnable() {

        return new ClientThread(ip,port,msg, msgQueue);
    }

    class ClientThread implements Runnable{

        private String ip;
        private Integer port;
        private String msg;
        private BlockingQueue<String> msgQueue;

        public ClientThread(String ip, Integer port, String msg, BlockingQueue<String> msgQueue){
            this.ip = ip;
            this.port = port;
            this.msg = msg;
            this.msgQueue = msgQueue;
        }

        @Override
        public void run() {
            try {
                System.out.println(ip +":"+ port);
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
        }
    }
}
