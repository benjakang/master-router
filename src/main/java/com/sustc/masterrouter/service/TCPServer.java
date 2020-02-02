package com.sustc.masterrouter.service;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Description: TCPServer 负责控制Router的TCP服务器，负责消息接收和发送
 * @Author: benjakang
 * @Time: 2020/1/5 15:13
 **/
public class TCPServer {

    private boolean isStarted;
    private int port;
    private Master master;
    private ServerThread serverThread;

    public TCPServer(int port, Master master){
        this.port = port;
        this.master = master;
    }

    public ServerThread getThread() {
        return serverThread;
    }

    public void stop() throws IOException {
        //关闭连接
        isStarted = false;
        new Socket("localhost",port);

    }

    public void start() throws IOException {
        isStarted = true;
        //启动服务器
        if (serverThread == null){
            serverThread = new ServerThread(port, master);
        }
        serverThread.start();

    }

    public synchronized boolean isStarting(){
        return isStarted;
    }

    /**
     * 当前Server专属线程
     */

    class ServerThread extends Thread{
        private int port;
        private Master master;

        public ServerThread(int port, Master master) throws IOException{
            this.port = port;
            this.master = master;
        }

        @Override
        public void run() {

            try {
                ServerSocket ss = new ServerSocket(port);
                //循环接收
                while (isStarting()) {
//                    System.out.println("等待client接入");
//                    System.out.println(Thread.currentThread().getName());
//                    System.out.println(InetAddress.getLocalHost().getHostAddress());
//                    System.out.println("开启register服务，ip："+ss.getLocalSocketAddress()+"port："+port);
                    Socket socket = ss.accept();

//                    System.out.println(socket);

                    if(isStarting()) { //最后一次连接，不载入
                        DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                        PrintWriter output = new PrintWriter(socket.getOutputStream());

//                        String line = "";
//                        StringBuilder message = new StringBuilder();
//                        while ((line = input.readLine()) != null){
//                            message.append(line);
//                        }

                        byte[] bytes = new byte[1024]; // 一次读取一个byte
                        StringBuilder message = new StringBuilder();
                        int len=0;
                        while ((len = input.read(bytes)) != -1) {
                            String cur = new String(bytes, 0, len);
                            message.append(cur);
                        }
                        System.out.println("收到register消息\nregister消息为："+message);

                        String reply = master.registered(new String(message));

                        output.write(reply);
                        output.flush();
                        input.close();
                        output.close();
                        //还有start任务没有完成，继续启动start
                        if(master.getLeftNeedEva() > 0) {
                            master.start(master.getIteration(), master.getK(), master.getLeftNeedEva());
                        }

                    }
                    socket.close();
                }
                ss.close();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
