import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * @author:zhumeng
 * @desc:
 **/
public class Server5 {
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

        public ClientHandle(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接：" + socket.getInetAddress() + "P:" + socket.getLocalPort());

            try {

                //得到套接字流
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();

                byte[] buffer = new byte[256];

                int readCount = inputStream.read(buffer);

                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, readCount);

                //byte
                byte be = byteBuffer.get();

                //char
//                char ch = (char) byteBuffer.get();
                char ch = byteBuffer.getChar();

                //int
                int i = byteBuffer.getInt();

                //bool
                boolean b = byteBuffer.get() == 1;

                //long
                long l = byteBuffer.getLong();

                //float
                float f = byteBuffer.getFloat();

                //double
                double d = byteBuffer.getDouble();

                //String
                int pos = byteBuffer.position();
                String str = new String(buffer, pos, readCount - pos - 1);
                System.out.println("收到的数据：" + readCount + " 数据：\n"
                        + be + "\n"
                        + ch + "\n"
                        + i + "\n"
                        + b + "\n"
                        + l + "\n"
                        + f + "\n"
                        + d + "\n"
                        + str + "\n"
                );

//                if (readCount > 0) {
//                    int value = Tools.byteArrayToInt(buffer);
//                    System.out.println("收到数量：" + readCount + " 数据：" + value);
//
//                    outputStream.write(buffer, 0, readCount);
//
//
//                } else {
//                    System.out.println("没有收到：" + readCount);
//
//                    outputStream.write(new byte[]{0});
//                }

                outputStream.write(buffer, 0, readCount);


                outputStream.close();
                inputStream.close();


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
