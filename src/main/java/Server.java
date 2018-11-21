import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wangjj17 on 2018/11/17.
 */
public class Server {
    private String host = SiteConfig.get("tcp.host");
    private int port = Integer.valueOf(SiteConfig.get("tcp.port"));
    private ServerSocket serverSocket;
    private Config config = ParseConfig.parseConfig();

    public Server() {
        try {
            //新建ServerSocket，backlog为最大等待连接数，InetAddress为绑定的IP
            serverSocket = new ServerSocket(port, config.getNumOfParams(), InetAddress.getByName(host));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server start...");
        System.out.println(serverSocket.getInetAddress().getHostAddress()+":"+serverSocket.getLocalPort());//服务端绑定的ip,port
    }

    public void service() {
        while (true) {
            Socket socket = null;
            try {
                //调用accept()方法开始监听，等待客户端的连接
                socket = serverSocket.accept();
                System.out.println("new connection accepted "+socket.getInetAddress()+":"+socket.getPort());
                //每个socket起一个线程来处理
                ServerThread serverThread = new ServerThread(socket);
                serverThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        Server server = new Server();
        server.service();
    }
}