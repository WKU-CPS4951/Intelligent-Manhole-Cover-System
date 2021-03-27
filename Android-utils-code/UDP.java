import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description UDP
 * none
 * @version 0.1
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
    public static String destinationAddress = "192.168.43.78";
    public static int destinationPort = 1234;
    public static int sendingPort = 4321;
    DatagramSocket socket = null;
    private static final String TAG = "UDP";

    public boolean init (String[] args) throws IOException{
//        socket = new DatagramSocket(port);
        return true;
    }

    public void setAddress(String address){
        this.destinationAddress = address;
    }

    public void setAddress(int port){
        this.destinationPort = port;
    }

    public void setMyAddress(int myport){
        this.sendingPort = myport;
    }

    public static void send(String[] strs) throws IOException,InterruptedException {
        List<DatagramPacket> messages = new ArrayList<>(1024);
        for (String str: strs) {
            DatagramPacket msg = parseMsg(
                    str, destinationAddress, destinationPort); // 发送给同一局域网的一台机器

            // JDK1.5 时 Collections 添加的 addAll 方法，可以一次往某个集合中添加多个元素
            Collections.addAll(messages, msg);
        }

        startSending(messages);
    }

    public static void receive() throws IOException,InterruptedException {
        byte[] buffer = new byte[1024];
        DatagramPacket msg = new DatagramPacket(buffer, buffer.length);

        try (DatagramSocket socket = new DatagramSocket(sendingPort)) {
            Log.d(TAG,"Receiver Launching...\n");
            while (true) {
                socket.receive(msg); // 接收数据包

                String msgBody = new String(
                        msg.getData(), msg.getOffset(), msg.getLength());
                if (msgBody.isEmpty()) { // 发现接收的消息是空字符串("")便跳出循环
                    break;
                }

                int senderPort = msg.getPort();
                InetAddress senderAddr = msg.getAddress();

                Log.d(TAG,String.format("Sender IP Port -> (%s:%d)\n",
                        senderAddr.getHostAddress(), senderPort));

                Log.d(TAG,"Sender Msg -> " + msgBody + "\n");
            }
        }

        Log.d(TAG,"Receiver closed。");
    }

    private static void startSending(List<DatagramPacket> messages)
            throws IOException, InterruptedException {

        // 无参构造的 DatagramSocket 会随机选择一个端口进行监听
        // 因为此时 DatagramSocket 的作用是发送，所以无需显式指定固定端口
        try (DatagramSocket socket = new DatagramSocket(sendingPort)) {
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

    private static DatagramPacket parseMsg(String msgBody, String addr, int port)
            throws UnknownHostException {

        byte[] msgData = msgBody.getBytes();
        DatagramPacket msg = new DatagramPacket(
                msgData, 0, msgData.length, // 数据从位置 0 开始，长度为 msgData.length
                InetAddress.getByName(addr), port); // 目的地 地址为 addr，监听端口为 port

        return msg;
    }

}
