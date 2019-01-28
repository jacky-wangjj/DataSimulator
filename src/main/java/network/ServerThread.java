package network;

import com.alibaba.fastjson.JSON;
import kafka.ParamProducer;
import param.Param;
import properties.SiteConfig;

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
    private Boolean isAsync = Boolean.valueOf(SiteConfig.get("producer.send.isAsync"));
    private String topic = SiteConfig.get("kafka.topic");
    private ParamProducer pp;

    public ServerThread(Socket socket) {
        this.socket = socket;
        pp = new ParamProducer(topic, isAsync);
    }

    public synchronized void receiveObject() throws Exception {
        //获取socket的输入流，并读取客户端发送来的消息
        is = socket.getInputStream();
        if (is.available() > 0) {
            String paramJson;
            ois = new ObjectInputStream(is);//对象输入流
            if ((object = ois.readObject()) instanceof Param) {
                Param<String, Number> param = (Param<String, Number>) object;
                System.out.println(param.toString());
//                pp.producer(param.getName(), param);
                paramJson = JSON.toJSON(param).toString();
                pp.producer(param.getName(), paramJson);
            } else {
                List<Param<String, Number>> params = (List<Param<String, Number>>) object;
                for (Param param : params) {
                    System.out.println(param.toString());
//                    pp.producer(param.getName(), param);
                    paramJson = JSON.toJSON(param).toString();
                    pp.producer(param.getName(), paramJson);
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
