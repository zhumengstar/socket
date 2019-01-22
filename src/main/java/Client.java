import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author:zhumeng
 * @desc:
 **/
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        //��ʱʱ��
        socket.setSoTimeout(3000);

        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 2000), 3000);

        System.out.println("�ѷ�����������ӣ�������������̡�");

        System.out.println("�ͻ�����Ϣ��" + socket.getLocalAddress() + "  P:" + socket.getLocalPort());
        System.out.println("����������Ϣ��" + socket.getInetAddress() + "  P:" + socket.getPort());

        try {
            //���ͽ�������
            todo(socket);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("�쳣�ر�");
        }
        //�ͷ���Դ
        socket.close();
        System.out.println("�ͻ������˳���");
    }

    private static void todo(Socket client) throws IOException {
        //������������
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //�õ�Socket���������ת��Ϊ��ӡ��
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        //�õ�Socket������,��ת��ΪBufferedReader
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;
        do {
            //���̶�ȡһ��
            String str = input.readLine();
            //���͵�������
            socketPrintStream.println(str);

            //�ӷ�������ȡһ��
            String echo = socketBufferedReader.readLine();
            if ("bye".equalsIgnoreCase(echo)) {
                flag = false;
                System.out.println("bye");
            } else {
                System.out.println(echo);
            }
        } while (flag);

        //�ͷ���Դ
        socketPrintStream.close();
        socketBufferedReader.close();
    }

}
