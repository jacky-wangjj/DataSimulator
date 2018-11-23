package network;

import param.Param;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Created by wangjj17 on 2018/11/19.
 */
public class ServerThread extends Thread {
    private Socket socket;
    private boolean isRunning = true;
    private InputStream is = null;
    private ObjectInputStream ois = null;
    private Object object;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public synchronized void receiveObject() throws Exception {
        //获取socket的输入流，并读取客户端发送来的消息
        is = socket.getInputStream();
        if (is.available() > 0) {
            ois = new ObjectInputStream(is);//对象输入流
            if ((object = ois.readObject()) instanceof Param) {
                Param<String, Number> param = (Param<String, Number>) object;
                System.out.println(param.toString());
            } else {
                List<Param<String, Number>> params = (List<Param<String, Number>>) object;
                for (Param param : params) {
                    System.out.println(param.toString());
                }
            }
        }
    }

    public void overThis() {
        if (isRunning)
            isRunning = false;
        try {
            //关闭资源
            if (ois != null)
                ois.close();
            if (is != null)
                is.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        while (isRunning) {
            try {
                receiveObject();
            } catch (Exception e) {
                e.printStackTrace();
                overThis();
            }
        }
    }
}
