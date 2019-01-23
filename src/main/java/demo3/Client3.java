import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author:zhumeng
 * @desc:
 **/
public class Client3 {
    private static final int PORT = 20000;
    private static final int LOCAL_PORT = 20001;

    public static void main(String[] args) throws IOException {
        Socket socket = createSocket();
        initSocket(socket);

        // 链接到本地20000端口
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 3000);

        System.out.println("已发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息 ：" + socket.getLocalAddress() + "  P:" + socket.getLocalPort());
        System.out.println("服务器端信息 ：" + socket.getInetAddress() + "  P:" + socket.getPort());

        try {
            //发送接收数据
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }

        //释放资源
        socket.close();
        System.out.println("客户端已退出:"+socket.getInetAddress()+" P:"+socket.getLocalPort());
    }

    private static Socket createSocket() throws IOException {
        /**
         //无代理模式，等效于空构造函数
         Socket socket = new Socket(Proxy.NO_PROXY);

         //新建一份具有HTTP代理到套接字，传输数据将通过www.baidu.com:8080端口转发
         Proxy proxy = new Proxy(Proxy.Type.HTTP,
         new InetSocketAddress(Inet4Address.getByName("www.baidu.com"), 8080));

         //新建一个套接字，并直接链接到本地20000服务器上
         socket=new Socket("localhost",PORT);

         //新建一个套接字，并直接链接到本地20000服务器上
         socket =new Socket(Inet4Address.getLocalHost(),PORT);

         //新建一个套接字，并直接链接到本地20000服务器上，并绑定到本地端口20001
         socket =new Socket("localhost",PORT,Inet4Address.getLocalHost(),LOCAL_PORT);
         socket=new Socket(Inet4Address.getLocalHost(),PORT,Inet4Address.getLocalHost(), LOCAL_PORT);
         **/

        Socket socket = new Socket();
        //绑定到本地20001端口
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT));

        return socket;
    }

    private static void todo(Socket client) throws IOException {
        //构建键盘输入
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //得到Socket输出流，并转换为打印流
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        //得到Socket输入流,并转换为BufferedReader
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;
        do {
            //键盘读取一行
            String str = input.readLine();
            //发送到服务器
            socketPrintStream.println(str);

            //从服务器读取一行
            String echo = socketBufferedReader.readLine();
            if ("bye".equalsIgnoreCase(echo)) {
                flag = false;
                System.out.println("bye");
            } else {
                System.out.println(echo);
            }
        } while (flag);

        //释放资源
        socketPrintStream.close();
        socketBufferedReader.close();
    }

    private static void initSocket(Socket socket) throws SocketException {

        //设置读取时间超时为2s
        socket.setSoTimeout(2000);

        //是否复用未完全关闭到Socket地址，对于指定bind超作后到套接字有效
        socket.setReuseAddress(true);

        //是否开启Nagle算法
        //socket.setTcpNoDelay(false);

        //是否需要长时间无数据响应时发送确认数据（类似心跳包），时间大约为2小时
        socket.setKeepAlive(true);

        socket.setSoLinger(true, 20);

        //是否让紧急数据内敛，默认false，紧急数据通过socket.sendUrgentData(1)发送
        socket.setOOBInline(true);

        //设置接收发送缓冲器大小
        socket.setReceiveBufferSize(64 * 1024 * 1024);
        socket.setSendBufferSize(64 * 1024 * 1024);

        //设置性能参数：短链接，延迟，宽带的相对重要性
        socket.setPerformancePreferences(1, 1, 1);


    }

}
