import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.os.Handler;

/**
 * @Description UDP
 * none
 * @version 1.0
 * @time 3/28
 *
 * @issue not designed package
 * @issue untested (except send)
 * @issue uncompleted try catch
 * @issue unencapsulated by Thread
 * @permission <uses-permission android:name="android.permission.INTERNET"/>
 * @reference https://blog.csdn.net/weixin_34072637/article/details/89201724
 */

public class UDP {
    private String destinationAddress = "192.168.43.78";
    private int destinationPort = 1234;
    private int sendPort = 4320;
    private int receivePort = 4321;
    private DatagramSocket receiveSocket = null;
    private static final String TAG = "UDP";

    public UDP(){

    }

    public UDP(String destinationAddress, int destinationPort, int port){
        this(destinationAddress, destinationPort, port, port);
    }

    public UDP(String destinationAddress, int destinationPort, int sendingPort, int receivingPort){
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
        this.sendPort = sendingPort;
        this.receivePort = receivingPort;
    }

    public void setAddress(String address){
        this.destinationAddress = address;
    }
    public String getAddress(){
        return destinationAddress;
    }

    public void setPort(int port){
        this.destinationPort = port;
    }
    public int getPort(){
        return destinationPort;
    }

    public void setSendingAddress(int sendingPort){
        this.sendPort = sendingPort;
    }
    public int getSendingAddress(){
        return sendPort;
    }

    public void setReceivingPort(int receivingPort) {
        this.receivePort = receivingPort;
    }
    public int getReceivingPort(){
        return receivePort;
    }

    /**
     * @Descritpion
     * sending string to the given IP:port by the given local:port
     * @param strs
     * @throws IOException
     * @throws InterruptedException
     */
    public void send(String[] strs) throws IOException,InterruptedException {
        List<DatagramPacket> messages = new ArrayList<>(1024);
        for (String str: strs) {
            DatagramPacket msg = parseMsg(
                    str, destinationAddress, destinationPort); // 发送给同一局域网的一台机器

            // JDK1.5 时 Collections 添加的 addAll 方法，可以一次往某个集合中添加多个元素
            Collections.addAll(messages, msg);
        }

        startSending(messages);
    }

    /**
     * @Descritpion
     * initiate (create the receive socket)
     */
    public void init(){
        try {
            receiveSocket = new DatagramSocket(receivePort);
            Log.d(TAG, "Receiver Launching...\n");
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    /**
     * @Descritpion
     * release the receive socket
     */
    public void release(){
        this.receiveSocket.close();
        this.receiveSocket = null;
        Log.d(TAG,"Receiver closed。");
    }

    /**
     * @Descritpion
     * receive bytes from socket without release the socket
     * @return byte[] bytes
     * @throws IOException
     * @throws InterruptedException
     */
    public byte[] receive() throws IOException,InterruptedException {
        byte[] buffer = new byte[1024];
        DatagramPacket msg = new DatagramPacket(buffer, buffer.length);
        byte[] result = null;
        boolean hasReceived = true;
        if(receiveSocket == null || receiveSocket.isClosed()){init();}
        DatagramSocket socket = this.receiveSocket;

        while (hasReceived) {
            socket.receive(msg); // 接收数据包

            String msgBody = new String(
                    msg.getData(), msg.getOffset(), msg.getLength());
            if (msgBody.isEmpty()) { // 发现接收的消息是空字符串("")便跳出循环
                break;
            }

            int senderPort = msg.getPort();
            InetAddress senderAddr = msg.getAddress();

            Log.d(TAG, String.format("Sender IP Port -> (%s:%d)\n",
                    senderAddr.getHostAddress(), senderPort));

            Log.d(TAG, "Sender Msg -> " + msgBody + "\n");
            result = msg.getData();
            hasReceived = false;
        }

        return result;
    }

    /**
     * @Description
     * sending datagram
     * @param messages
     * @throws IOException
     * @throws InterruptedException
     */
    private void startSending(List<DatagramPacket> messages)
            throws IOException, InterruptedException {

        // 无参构造的 DatagramSocket 会随机选择一个端口进行监听
        // 因为此时 DatagramSocket 的作用是发送，所以无需显式指定固定端口
        try (DatagramSocket socket = new DatagramSocket(sendPort)) {
            Log.d(TAG, "startSending: "+"Port：" + socket.getLocalPort() + "\n");
            for (DatagramPacket msg : messages) {
                int sendCount = 0;
                while(sendCount < 3){
                    sendCount++;
                    new Thread() {
                        public void run() {
                            try {
                                socket.send(msg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    Thread.sleep(10);
                }

                Log.i("send", "Overtime");

                int recverPort = msg.getPort();
                InetAddress recverAddr = msg.getAddress();
                Log.d(TAG, "startSending: Msg ->" + recverAddr.getHostAddress()+": "+recverPort);
                Thread.sleep(500); // 设定 每隔 0.5 秒发送一个消息
            }
        }
    }

    /**
     * @Description
     * Encapsulate the str into Datagram
     * @param msgBody
     * @param addr
     * @param port
     * @return DatagramPacket
     * @throws UnknownHostException
     */
    private static DatagramPacket parseMsg(String msgBody, String addr, int port)
            throws UnknownHostException {

        byte[] msgData = msgBody.getBytes();
        DatagramPacket msg = new DatagramPacket(
                msgData, 0, msgData.length, // 数据从位置 0 开始，长度为 msgData.length
                InetAddress.getByName(addr), port); // 目的地 地址为 addr，监听端口为 port

        return msg;
    }

    /**
     * @Description
     * demo for create a thread that receive data
     * @require Handler to handle bytes
     * @note need to know the type it received
     */

    public class ReceiveThread extends Thread{
        boolean flag = true;
        Handler handler = null;
        public ReceiveThread(Handler handler){
            this.handler = handler;
        }
        @Override
        public void run() {
            super.run();
            init();
            try{
                while(flag){
                    byte[] bytes= receive();
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("bytes",bytes);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                release();
            }
        }

        public void close(){
            flag = false;
        }
    }
}
