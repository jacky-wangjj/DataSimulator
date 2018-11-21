import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangjj17 on 2018/11/17.
 */
public class Client {
    private String host;
    private int port;
    private static Config config;
    private List<ParamConfs> paramConfs;
    private long startTime;
    private ParamUtils paramUtils;
    private Timer timer;
    private long delay;
    private long sendInterval;
    private int sendBufSize;
    private Socket socket;
    private OutputStream os;
    private ObjectOutputStream oos;

    public void init() {
        host = SiteConfig.get("tcp.host");
        port = Integer.valueOf(SiteConfig.get("tcp.port"));
        paramUtils = new ParamUtils();
        config = ParseConfig.parseConfig();//解析config.json中的配置到Config对象
        paramConfs = config.getParamConfs();
        startTime = System.currentTimeMillis();//获取系统时间，作为开始时间
        timer = new Timer();
        delay = Integer.valueOf(SiteConfig.get("timer.schedule.delay"));//定时任务，延迟时间
        sendInterval = Long.valueOf(SiteConfig.get("timer.send.interval"));//发送定时任务发送时间间隔
        sendBufSize = Integer.valueOf(SiteConfig.get("send.buf.size"));//发送缓冲区大小
    }

    public synchronized void sendObject(Object param) {
        try {
            oos = new ObjectOutputStream(os);
            oos.writeObject(param);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            overThis();
        }
    }

    public void overThis() {
        timer.cancel();//取消定时任务
        try {
            if (oos != null)
                oos.close();
            if (os != null)
                os.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startClient() {
        try {
            //创建客户端Socket，指定服务器地址和端口
            socket = new Socket(host, port);
            System.out.println("local port:"+socket.getLocalPort());
            //获取输出流，向服务器端发送信息
            os = socket.getOutputStream();
            for (ParamConfs paramConf : paramConfs) {
                long timeInterval = paramConf.getTimeInterval();
                if (timeInterval == 0) {
                    Param<String, Number> param = paramUtils.getParam(paramConf, startTime);
                    //System.out.println(param.toString());
                    sendObject(param);
                } else if (timeInterval > 0){
                    final List<Param<String, Number>> params = new ArrayList<>(sendBufSize);
                    TimerTask getParamTask = new TimerTask() {
                        @Override
                        public void run() {
                            long timestamp = System.currentTimeMillis();
                            Param<String, Number> param = paramUtils.getParam(paramConf, timestamp);
                            //System.out.println(param.toString());
                            sendObject(param);
                            //params.add(param);
                        }
                    };
                    //设置定时任务，延时delay后开始执行，每个timeInterval时长，执行一次task任务
                    timer.scheduleAtFixedRate(getParamTask, delay, timeInterval);
                    TimerTask sendParamTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (params.size() == sendBufSize) {
                                sendObject(params);
                                params.clear();
                            }
                        }
                    };
                    //timer.scheduleAtFixedRate(sendParamTask, delay, sendInterval);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            overThis();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.init();
        client.startClient();
    }
}