import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wangjj17 on 2018/11/17.
 */
public class Server {
    private int port = 8888;
    private ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server start...");
    }

    public void service() {
        while (true) {
            Socket socket = null;
            InputStream is = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            OutputStream os = null;
            PrintWriter pw = null;
            try {
                //调用accept()方法开始监听，等待客户端的连接
                socket = serverSocket.accept();
                System.out.println("new connection accepted "+socket.getInetAddress()+":"+socket.getPort());
                //获取输入流，并读取客户端信息
                is = socket.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                String info = null;
                while ((info = br.readLine()) != null) {
                    System.out.println("client say: "+info);
                }
                socket.shutdownInput();//关闭输入流
                //获取输出流，响应客户端的请求
                os = socket.getOutputStream();
                pw = new PrintWriter(os);
                pw.write("hello,client");
                pw.flush();//调用flush()方法将缓冲输出
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    //关闭资源
                    pw.close();
                    os.close();
                    br.close();
                    isr.close();
                    is.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.service();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}