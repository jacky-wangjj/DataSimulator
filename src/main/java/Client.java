import java.io.*;
import java.net.Socket;

/**
 * Created by wangjj17 on 2018/11/17.
 */
public class Client {
    private int port = 8888;
    private String host = "localhost";

    public void startClient() {
        Socket socket = null;
        OutputStream os = null;
        PrintWriter pw = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            //创建客户端Socket，指定服务器地址和端口
            socket = new Socket(host, port);
            //获取输出流，向服务器端发送信息
            os = socket.getOutputStream();
            pw = new PrintWriter(os);
            pw.write("hello,server");
            pw.flush();
            socket.shutdownOutput();//关闭输出流
            //获取输入流，并读取服务端的相应信息
            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String info = null;
            while ((info = br.readLine()) != null) {
                System.out.println("server say: "+info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭资源
                br.close();
                is.close();
                pw.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.startClient();
    }
}