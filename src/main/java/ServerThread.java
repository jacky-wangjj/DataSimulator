import java.io.*;
import java.net.Socket;

/**
 * Created by wangjj17 on 2018/11/19.
 */
public class ServerThread extends Thread {
    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream is = null;
        ObjectInputStream ois = null;
        //OutputStream os = null;
        try {
            //获取socket的输入流，并读取客户端发送来的消息
            is = socket.getInputStream();
            ois = new ObjectInputStream(is);//对象输入流
            Param<String, Number> param = (Param<String, Number>) ois.readObject();
            System.out.println(param.toString());
            socket.shutdownInput();//关闭输入流
            //获取输出流，响应客户端的请求
            //os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭资源
                //if (os != null)
                //    os.close();
                if (ois != null)
                    ois.close();
                if (is != null)
                    is.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
