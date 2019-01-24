import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author:zhumeng
 * @desc:
 **/
public class Client4 {
    private static final int PORT = 20000;
    private static final int LOCAL_PORT = 20001;

    public static void main(String[] args) throws IOException {
        Socket socket = createSocket();
        initSocket(socket);

        // ���ӵ�����20000�˿�
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 3000);

        System.out.println("�ѷ�����������ӣ�������������̡�");
        System.out.println("�ͻ�����Ϣ ��" + socket.getLocalAddress() + "  P:" + socket.getLocalPort());
        System.out.println("����������Ϣ ��" + socket.getInetAddress() + "  P:" + socket.getPort());

        try {
            //���ͽ�������
            todo(socket);
        } catch (Exception e) {
            System.out.println("�쳣�ر�");
        }

        //�ͷ���Դ
        socket.close();
        System.out.println("�ͻ������˳�:" + socket.getInetAddress() + " P:" + socket.getLocalPort());
    }

    private static Socket createSocket() throws IOException {
        /**
         //�޴���ģʽ����Ч�ڿչ��캯��
         Socket socket = new Socket(Proxy.NO_PROXY);

         //�½�һ�ݾ���HTTP�����׽��֣��������ݽ�ͨ��www.baidu.com:8080�˿�ת��
         Proxy proxy = new Proxy(Proxy.Type.HTTP,
         new InetSocketAddress(Inet4Address.getByName("www.baidu.com"), 8080));

         //�½�һ���׽��֣���ֱ�����ӵ�����20000��������
         socket=new Socket("localhost",PORT);

         //�½�һ���׽��֣���ֱ�����ӵ�����20000��������
         socket =new Socket(Inet4Address.getLocalHost(),PORT);

         //�½�һ���׽��֣���ֱ�����ӵ�����20000�������ϣ����󶨵����ض˿�20001
         socket =new Socket("localhost",PORT,Inet4Address.getLocalHost(),LOCAL_PORT);
         socket=new Socket(Inet4Address.getLocalHost(),PORT,Inet4Address.getLocalHost(), LOCAL_PORT);
         **/

        Socket socket = new Socket();
        //�󶨵�����20001�˿�
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT));

        return socket;
    }

    private static void todo(Socket client) throws IOException {


        //�õ�Socket���������ת��Ϊ��ӡ��
        OutputStream outputStream = client.getOutputStream();

        //�õ�Socket������
        InputStream inputStream = client.getInputStream();

        byte[] buffer = new byte[128];

        //���͵�������
        outputStream.write(new byte[]{1});


        int read = inputStream.read(buffer);
        if (read > 0) {
            System.out.println("�յ�������" + read + " ���ݣ�" + Array.getByte(buffer, 0));

        } else {
            System.out.println("û���յ���" + read);
        }


        //�ͷ���Դ
        outputStream.close();
        inputStream.close();
    }

    private static void initSocket(Socket socket) throws SocketException {

        //���ö�ȡʱ�䳬ʱΪ2s
        socket.setSoTimeout(2000);

        //�Ƿ���δ��ȫ�رյ�Socket��ַ������ָ��bind�������׽�����Ч
        socket.setReuseAddress(true);

        //�Ƿ���Nagle�㷨
        //socket.setTcpNoDelay(false);

        //�Ƿ���Ҫ��ʱ����������Ӧʱ����ȷ�����ݣ���������������ʱ���ԼΪ2Сʱ
        socket.setKeepAlive(true);

        socket.setSoLinger(true, 20);

        //�Ƿ��ý�������������Ĭ��false����������ͨ��socket.sendUrgentData(1)����
        socket.setOOBInline(true);

        //���ý��շ��ͻ�������С
        socket.setReceiveBufferSize(64 * 1024 * 1024);
        socket.setSendBufferSize(64 * 1024 * 1024);

        //�������ܲ����������ӣ��ӳ٣�����������Ҫ��
        socket.setPerformancePreferences(1, 1, 1);


    }

}
