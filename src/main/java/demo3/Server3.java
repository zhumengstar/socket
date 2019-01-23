import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;

/**
 * @author:zhumeng
 * @desc:
 **/
public class Server3 {
    private static final int PORT = 20000;

    public static void main(String[] args) throws IOException {

        ServerSocket server = createServerSocket();

        initServerSocket(server);
        server.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 50);


        System.out.println("服务器准备就绪～");
        System.out.println("服务器信息：" + server.getInetAddress() + "P:" + server.getLocalPort());

        for (; ; ) {
            //得到客户端
            Socket client = server.accept();

            //客户端构建异步线程
            ClientHandle clientHandle = new ClientHandle(client);

            //启动线程
            clientHandle.start();
        }

    }

    private static void initServerSocket(ServerSocket serverSocket) throws SocketException {

        //是否复用未完全关闭的端口
        serverSocket.setReuseAddress(true);
        //等效Socket#setReceiveBufferSzie
        serverSocket.setReceiveBufferSize(64 * 1024 * 1024);
        //设置serverSocket#accept超时时间
//        serverSocket.setSoTimeout(2000);

        //设置性能参数：短链接，延迟，带宽的相对重要性
        serverSocket.setPerformancePreferences(1, 1, 1);
    }

    private static ServerSocket createServerSocket() throws IOException {
        //创建基础ServerSocket
        ServerSocket serverSocket = new ServerSocket();

        //绑定到本地端口上
//        serverSocket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 50);


        //serverSocket=new ServerSocket(PORT);
        //serverSocket=new ServerSocket(PORT,50);
//        serverSocket=new ServerSocket(PORT,50,Inet4Address.getLocalHost());


        return serverSocket;
    }

    private static class ClientHandle extends Thread {
        private Socket socket;
        private boolean flag = true;

        public ClientHandle(Socket socket) {
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
