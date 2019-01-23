import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author:zhumeng
 * @desc:
 **/
public class UDPSearcher {


    private static final int LISTEN_PORT = 30000;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("UDPSearcher Started.");

        Listener listener = listen();

        //��ȡ���������Ϣ�˳�
        System.in.read();
        sendBroadcast();


        List<Device> devices = listener.getDevicesAndClose();
        for (Device device : devices) {
            System.out.println("Device:" + device.toString());
        }

        //���
        System.out.println("UDPSearcher Finished");


    }

    //����
    private static Listener listen() throws InterruptedException {
        System.out.println("UDPSearcher Start Listen.");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();
        countDownLatch.await();
        System.out.println("UDPSearcher Finished Listen.");
        return listener;

    }

    private static void sendBroadcast() throws IOException {
        System.out.println("UDPSearcher SendBroadcast Started.");

        //��Ϊ����������ϵͳ�Զ�����
        DatagramSocket ds = new DatagramSocket();

        /**
         * ����һ����������
         */
        String requestData = MessageCreator.buildWithPort(LISTEN_PORT);
        byte[] requestDataBytes = requestData.getBytes();
        //ֱ�ӹ���packet
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes, requestDataBytes.length);

        //����20000�˿�,�㲥��ַ
        requestPacket.setAddress(Inet4Address.getByName("255.255.255.255"));
        requestPacket.setPort(20000);

        //���͹㲥
        ds.send(requestPacket);
        ds.close();


        //���
        System.out.println("UDPSearcher SendBroadcast Finished.");
    }

    private static class Device {
        final int port;
        final String ip;
        final String sn;

        private Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip='" + ip + '\'' +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }

    private static class Listener extends Thread {
        private final int listenPort;
        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList<>();
        private DatagramSocket ds = null;
        private boolean done = false;


        private Listener(int listenPort, CountDownLatch countDownLatch) {
            super();
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();
            //֪ͨ������
            countDownLatch.countDown();
            try {

                ds = new DatagramSocket(listenPort);

                System.out.println("UDPSearcher Started.");
                while (!done) {

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

                    System.out.println("UDPSearcher receive from ip:" + ip + "\tport:" + port + "\tdata:" + data);

                    String sn = MessageCreator.parseSn(data);
                    if (sn != null) {
                        Device device = new Device(port, ip, sn);
                        devices.add(device);
                    }
                }
            } catch (Exception ignored) {
            } finally {
                close();
            }
            System.out.println("UDPSearcher Listen Finished.");
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }
    }


}

