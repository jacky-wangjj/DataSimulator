import java.util.*;

/**
 * Created by wangjj17 on 2018/11/17.
 */
public class Client {
    private String host;
    private int port;
    private static Config config;
    private List<ParamConfs> paramConfs;
    private long startTime;
    private long delay = 0;
    private ParamUtils paramUtils;

    public void init() {
        host = SiteConfig.get("tcp.host");
        port = Integer.valueOf(SiteConfig.get("tcp.port"));
        paramUtils = new ParamUtils();
        config = ParseConfig.parseConfig();//解析config.json中的配置到Config对象
        paramConfs = config.getParamConfs();
        startTime = System.currentTimeMillis();//获取系统时间，作为开始时间
    }

    public void startClient() {
        for (ParamConfs paramConf : paramConfs) {
            long timeInterval = paramConf.getTimeInterval();
            if (timeInterval == 0) {
                Param<String, Number> param = paramUtils.getParam(paramConf, startTime);
                System.out.println(param.toString());
                ClientThread clientThread = new ClientThread(host, port, param);
                clientThread.start();
            } else if (timeInterval > 0){
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        Param<String, Number> param = paramUtils.getParam(paramConf, System.currentTimeMillis());
                        System.out.println(param.toString());
                        ClientThread clientThread = new ClientThread(host, port, param);
                        clientThread.start();
                    }
                };
                //设置定时任务，延时delay后开始执行，每个timeInterval时长，执行一次task任务
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(task, delay, timeInterval);
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.init();
        client.startClient();
    }
}