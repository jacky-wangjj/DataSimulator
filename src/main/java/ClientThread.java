import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wangjj17 on 2018/11/20.
 */
public class ClientThread extends Thread {
    private int port;
    private String host;
    private Param<String, Number> param;

    public ClientThread(String host, int port, Param<String, Number> param) {
        this.host = host;
        this.port = port;
        this.param = param;
    }

    @Override
    public void run() {
        Socket socket = null;
        OutputStream os = null;
        ObjectOutputStream oos = null;
        //InputStream is = null;
        try {
            //创建客户端Socket，指定服务器地址和端口
            socket = new Socket(host, port);
            //获取输出流，向服务器端发送信息
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);
            oos.writeObject(param);
            socket.shutdownOutput();//关闭输出流
            //获取输入流，并读取服务端的相应信息
            //is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭资源
                //if (is != null)
                //    is.close();
                if (os != null)
                    os.close();
                if (oos != null)
                    oos.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
