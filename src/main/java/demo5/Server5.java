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

        public ClientHandle(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("�¿ͻ������ӣ�" + socket.getInetAddress() + "P:" + socket.getLocalPort());

            try {

                //�õ��׽�����
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
                System.out.println("�յ������ݣ�" + readCount + " ���ݣ�\n"
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
//                    System.out.println("�յ�������" + readCount + " ���ݣ�" + value);
//
//                    outputStream.write(buffer, 0, readCount);
//
//
//                } else {
//                    System.out.println("û���յ���" + readCount);
//
//                    outputStream.write(new byte[]{0});
//                }

                outputStream.write(buffer, 0, readCount);


                outputStream.close();
                inputStream.close();


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
