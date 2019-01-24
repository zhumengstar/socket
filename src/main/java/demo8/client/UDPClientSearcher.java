package client;

import client.bean.ServerInfo;
import constants.UDPConstants;
import utils.ByteUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author:zhumeng
 * @desc:
 **/
public class UDPClientSearcher {
    private static final int LISTEN_PORT = UDPConstants.PORT_CLIENT_RESPONSE;

    public static ServerInfo searchServer(int timeout) {
        System.out.println("UDPSearch Started.");

        //�ɹ��յ����͵�դ��
        CountDownLatch receiveLatch = new CountDownLatch(1);
        Listener listener = null;

        try {
            listener = listen(receiveLatch);
            sendBroadcast();
            receiveLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //���
        System.out.println("UDPSearcher Finished.");

        if (listener == null) {
            return null;
        }

        List<ServerInfo> devices = listener.getServerAndClose();
        if (devices.size() > 0) {
            return devices.get(0);
        }
        return null;
    }

    private static void sendBroadcast() throws IOException {

        System.out.println("UDPSearcher sendBroadcast started.");

        //��Ϊ����������ϵͳ����˿�
        DatagramSocket ds = new DatagramSocket();

        //����һ����������
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);

        //tͷ��
        byteBuffer.put(UDPConstants.HEADER);
        //CMD����
        byteBuffer.putShort((short) 1);
        //���Ͷ˿���Ϣ
        byteBuffer.putInt(LISTEN_PORT);

        //ֱ�ӹ���Packet
        DatagramPacket requestPack = new DatagramPacket(byteBuffer.array(), byteBuffer.position() + 1);

        //�㲥��ַ
        requestPack.setAddress(InetAddress.getByName("255.255.255.255"));

        //���÷������˿�
        requestPack.setPort(UDPConstants.PORT_SERVER);

        //����
        ds.send(requestPack);

        ds.close();
        // ���
        System.out.println("UDPSearcher sendBroadcast finished.");
    }

    private static Listener listen(CountDownLatch receiveLatch) throws InterruptedException {
        System.out.println("UDPSearcher start listen.");
        CountDownLatch startDownLatch = new CountDownLatch(1);

        Listener listener = new Listener(LISTEN_PORT, startDownLatch, receiveLatch);

        listener.start();

        startDownLatch.await();

        return listener;
    }

    public static class Listener extends Thread {

        private final int listenPort;
        private final CountDownLatch startDownLatch;
        private final CountDownLatch receiveDownLatch;

        private List<ServerInfo> serverInfoList = new ArrayList<>();

        private final byte[] buffer = new byte[128];
        private final int minLen = UDPConstants.HEADER.length + 2 + 4;
        private boolean done = false;

        private DatagramSocket ds = null;

        public Listener(int listenPort, CountDownLatch startDownLatch, CountDownLatch receiveDownLatch) {
            this.listenPort = listenPort;
            this.startDownLatch = startDownLatch;
            this.receiveDownLatch = receiveDownLatch;
        }


        @Override
        public void run() {
            super.run();
            //֪ͨ������
            startDownLatch.countDown();

            try {
                //�������Ͷ˿�
                ds = new DatagramSocket(listenPort);
                DatagramPacket receivePack = new DatagramPacket(buffer, buffer.length);
                while (!done) {
                    //����
                    ds.receive(receivePack);

                    //��ӡ���յ���Ϣ�ͷ�������Ϣ
                    //�����ߵ�ip��ַ
                    String ip = receivePack.getAddress().getHostAddress();
                    int port = receivePack.getPort();
                    int dataLen = receivePack.getLength();
                    byte[] data = receivePack.getData();
                    boolean isValid = dataLen >= minLen
                            && ByteUtils.startsWith(data, UDPConstants.HEADER);

                    System.out.println("ServerProvider receiver from ip:" + ip + "\tport:" + port + "\tdataValid:" + isValid);

                    if (!isValid) {
                        continue;
                    }

                    //�ڶ���������������ֵ
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, UDPConstants.HEADER.length, dataLen);

                    final short cmd = byteBuffer.getShort();

                    final int serverPort = byteBuffer.getInt();
                    if (cmd != 2 || serverPort <= 0) {
                        System.out.println("UDPSearcher receive cmd:" + cmd + "\tserverPort:" + serverPort);
                        continue;
                    }

                    String sn = new String(buffer, minLen, dataLen - minLen);
                    ServerInfo info = new ServerInfo(serverPort, ip, sn);

                    serverInfoList.add(info);

                    //�ɹ����յ�һ��
                    receiveDownLatch.countDown();

                }
            } catch (Exception ignored) {
            } finally {
                close();
            }
            System.out.println("UDPSearcher listener finished");
        }

        public List<ServerInfo> getServerAndClose() {
            done = true;
            close();
            return serverInfoList;
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        //��������
        private void exit() {
            done = true;
            close();
        }
    }

}
