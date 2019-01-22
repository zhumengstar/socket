import java.io.IOException;
import java.net.*;

/**
 * @author:zhumeng
 * @desc:
 **/
public class UDPSearcher {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPSearcher Started.");

        //作为搜索方，让系统自动分配
        DatagramSocket ds = new DatagramSocket();

        //构建一份请求数据
        String requestData = "Hello World ！";
        byte[] requestDataBytes = requestData.getBytes();
        //直接构建packet
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes,
                requestDataBytes.length);
        //本机20000端口
        requestPacket.setAddress(Inet4Address.getLocalHost());
        requestPacket.setPort(20000);

        //
        ds.send(requestPacket);

        //构建接收实体
        final byte[] buf = new byte[512];
        DatagramPacket receivePack = new DatagramPacket(buf, buf.length);

        //接收
        ds.receive(receivePack);

        //打印接收到的信息与发送者的信息
        //发送者的ip地址
        String ip = receivePack.getAddress().getHostAddress();

        int port = receivePack.getPort();

        int dataLen = receivePack.getLength();

        String data = new String(receivePack.getData(), 0, dataLen);
        System.out.println("UDPSearcher receive from ip:" + ip
                + "\tport:" + port + "\tdata:" + data);
        //完成
        System.out.println("UDPSearcher finished.");
        ds.close();
    }
}

