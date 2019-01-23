import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * @author:zhumeng
 * @desc:
 **/
public class UDPProvider2 {

    public static void main(String[] args) throws IOException {
        //生成一份唯一标示
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn);
        provider.start();

        //读取任意键盘后可以退出
        System.in.read();
        provider.exit();
    }

    private static class Provider extends Thread {
        private final String sn;
        private boolean done = false;
        private DatagramSocket ds = null;

        public Provider(String sn) {
            super();
            this.sn = sn;
        }

        @Override
        public void run() {
            super.run();

            System.out.println("UDPProvider Started.");
            try {
                //监听20000端口
                ds = new DatagramSocket(20000);
                while (!done) {


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
                    System.out.println("UDPProvider receive from ip:" + ip + "\tport:" + port + "\tdata:" + data);


                    //解析端口号
                    int responsePort = MessageCreator.parsePort(data);
                    if (responsePort != -1) {
                        //构建一份回送数据
                        String responseData = MessageCreator.buildWithSn(sn);
                        byte[] responseDataBytes = responseData.getBytes();
                        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes,
                                responseDataBytes.length,
                                receivePack.getAddress(),
                                responsePort);

                        ds.send(responsePacket);
                    }
                }
            } catch (Exception ignored) {
            } finally {
                close();
            }
            //完成
            System.out.println("UDPProvider Finished.");
            ds.close();
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        //提供结束
        void exit() {
            done = true;
            close();
        }
    }
}
