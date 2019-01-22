import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author:zhumeng
 * @desc:
 **/
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(2000);

        System.out.println(" 服务器准备就绪～");
        System.out.println("服务器端信息：" + server.getLocalSocketAddress() + "  P:" + server.getLocalPort());

        //等待客户端连接

        for (; ; ) {
            //得到客户端
            Socket client = server.accept();
            //客户端构建异步线程
            ClientHandle clientHandle = new ClientHandle(client);
            //启动线程
            clientHandle.start();
        }
    }

    private static class ClientHandle extends Thread {
        private Socket socket;
        private boolean flag=true;

        ClientHandle(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接：" + socket.getInetAddress() + "P:" + socket.getLocalPort());

            try {
                //得到打印流，用于数据输出，服务器回送数据使用
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                //得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                do {
                    //客户端拿到数据
                    String str = socketInput.readLine();
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                        //回送
                        System.out.println("bye");
                        socketOutput.println("bye");

                    } else {
                        //打印到屏幕，并回送数据长度
                        System.out.println(str);
                        socketOutput.println("回送长度:" + str.length());
                    }
                } while (flag);

                socketInput.close();
                socketOutput.close();


            } catch (Exception e) {
                System.out.println("连接异常断开～");
            } finally {
                //连接关闭
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("客户端已退出" + socket.getInetAddress() + "P:" + socket.getPort());
        }

    }
}
