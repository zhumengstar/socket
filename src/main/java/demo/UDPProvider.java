import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author:zhumeng
 * @desc:
 **/
public class UDPProvider {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider Started.");

        //��Ϊ�����ߣ�ָ��һ���˿��������ݽ���
        DatagramSocket ds = new DatagramSocket(20000);

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
        System.out.println("UDPProvider receive from ip:" + ip
                + "\tport:" + port + "\tdata:" + data);

        //����һ�ݻ�������
        String responseData = "Receive Data with len:" + dataLen;
        byte[] responseDataBytes = responseData.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes,
                responseDataBytes.length,
                receivePack.getAddress(),
                receivePack.getPort());

        ds.send(responsePacket);

        //���
        System.out.println("UDPProvider finished.");
        ds.close();
    }


}
