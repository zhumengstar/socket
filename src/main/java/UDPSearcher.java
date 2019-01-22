import java.io.IOException;
import java.net.*;

/**
 * @author:zhumeng
 * @desc:
 **/
public class UDPSearcher {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPSearcher Started.");

        //��Ϊ����������ϵͳ�Զ�����
        DatagramSocket ds = new DatagramSocket();

        //����һ����������
        String requestData = "Hello World ��";
        byte[] requestDataBytes = requestData.getBytes();
        //ֱ�ӹ���packet
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes,
                requestDataBytes.length);
        //����20000�˿�
        requestPacket.setAddress(Inet4Address.getLocalHost());
        requestPacket.setPort(20000);

        //
        ds.send(requestPacket);

        //��������ʵ��
        final byte[] buf = new byte[512];
        DatagramPacket receivePack = new DatagramPacket(buf, buf.length);

        //����
        ds.receive(receivePack);

        //��ӡ���յ�����Ϣ�뷢���ߵ���Ϣ
        //�����ߵ�ip��ַ
        String ip = receivePack.getAddress().getHostAddress();

        int port = receivePack.getPort();

        int dataLen = receivePack.getLength();

        String data = new String(receivePack.getData(), 0, dataLen);
        System.out.println("UDPSearcher receive from ip:" + ip
                + "\tport:" + port + "\tdata:" + data);
        //���
        System.out.println("UDPSearcher finished.");
        ds.close();
    }
}

