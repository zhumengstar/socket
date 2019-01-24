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


        System.out.println("������׼��������");
        System.out.println("��������Ϣ��" + server.getInetAddress() + "P:" + server.getLocalPort());

        for (; ; ) {
            //�õ��ͻ���
            Socket client = server.accept();

            //�ͻ��˹����첽�߳�
            ClientHandle clientHandle = new ClientHandle(client);

            //�����߳�
            clientHandle.start();
        }

    }

    private static void initServerSocket(ServerSocket serverSocket) throws SocketException {

        //�Ƿ���δ��ȫ�رյĶ˿�
        serverSocket.setReuseAddress(true);
        //��ЧSocket#setReceiveBufferSzie
        serverSocket.setReceiveBufferSize(64 * 1024 * 1024);
        //����serverSocket#accept��ʱʱ��
//        serverSocket.setSoTimeout(2000);

        //�������ܲ����������ӣ��ӳ٣�����������Ҫ��
        serverSocket.setPerformancePreferences(1, 1, 1);
    }

    private static ServerSocket createServerSocket() throws IOException {
        //��������ServerSocket
        ServerSocket serverSocket = new ServerSocket();

        //�󶨵����ض˿���
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
            System.out.println("�¿ͻ������ӣ�" + socket.getInetAddress() + "P:" + socket.getLocalPort());

            try {
                //�õ���ӡ�������������������������������ʹ��
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                //�õ������������ڽ�������
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                do {
                    //�ͻ����õ�����
                    String str = socketInput.readLine();
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                        //����
                        System.out.println("bye");
                        socketOutput.println("bye");

                    } else {
                        //��ӡ����Ļ�����������ݳ���
                        System.out.println(str);
                        socketOutput.println("���ͳ���:" + str.length());
                    }
                } while (flag);

                socketInput.close();
                socketOutput.close();


            } catch (Exception e) {
                System.out.println("�����쳣�Ͽ���");
            } finally {
                //���ӹر�
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("�ͻ������˳�" + socket.getInetAddress() + "P:" + socket.getPort());
        }
    }
}
