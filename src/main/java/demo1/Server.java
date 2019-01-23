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

        System.out.println(" ������׼��������");
        System.out.println("����������Ϣ��" + server.getLocalSocketAddress() + "  P:" + server.getLocalPort());

        //�ȴ��ͻ�������

        for (; ; ) {
            //�õ��ͻ���
            Socket client = server.accept();
            //�ͻ��˹����첽�߳�
            ClientHandle clientHandle = new ClientHandle(client);
            //�����߳�
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
