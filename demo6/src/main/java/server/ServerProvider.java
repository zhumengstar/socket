package server;

import constants.UDPConstants;
import utils.ByteUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @author:zhumeng
 * @desc:
 **/
public class ServerProvider {

    private static Provider PROVIDER_INSTANCE;

    public ServerProvider() {
    }

    public static void start(int portServer) {
        stop();
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn, portServer);


        provider.start();
        PROVIDER_INSTANCE = provider;
    }

    public static void stop() {
        if (PROVIDER_INSTANCE != null) {
            PROVIDER_INSTANCE.exit();
            PROVIDER_INSTANCE = null;
        }
    }

    private static class Provider extends Thread {

        private final byte[] sn;
        private final int port;
        private boolean done = false;
        private DatagramSocket ds = null;
        //�洢��Ϣ��buffer
        final byte[] buffer = new byte[128];

        public Provider(String sn, int port) {
            super();
            this.sn = sn.getBytes();
            this.port = port;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("UDPProvider Started.");

            try {
                //����20000�˿�
                ds = new DatagramSocket(UDPConstants.PORT_SERVER);

                //������Ϣ��Packet
                DatagramPacket receiver = new DatagramPacket(buffer, buffer.length);

                while (!done) {
                    //������Ϣ
                    ds.receive(receiver);

                    //��ӡ��������Ϣ�ͷ�������Ϣ
                    //�����ߵ�ip��ַ
                    String clientIp = receiver.getAddress().getHostAddress();
                    int clientPort = receiver.getPort();
                    int clientDataLen = receiver.getLength();
                    byte[] clientData = receiver.getData();
                    boolean isValid = clientDataLen >= (UDPConstants.HEADER.length + 2 + 4)
                            && ByteUtils.startsWith(clientData, UDPConstants.HEADER);
                    System.out.println("ServerProvider receiver from ip:" + clientIp + "\tport:" + clientPort + "\tdataValid:" + isValid);

                    if (!isValid) {
                        continue;
                    }
                    //������������Ͷ˿�
                    int index = UDPConstants.HEADER.length;
                    short cmd = (short) ((clientData[index++] << 8) | (clientData[index++] & 0xff));

                    int responsePort = (((clientData[index++]) << 24) |
                            ((clientData[index++] & 0xff) << 16) |
                            ((clientData[index++] & 0xff) << 8) |
                            ((clientData[index++] & 0xff)));

                    //�жϺϷ���
                    if (cmd == 1 && responsePort > 0) {
                        //����һ�ݻ�������
                        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                        byteBuffer.put(UDPConstants.HEADER);
                        byteBuffer.putShort((short) 2);
                        byteBuffer.putInt(port);
                        byteBuffer.put(sn);

                        int len = byteBuffer.position();

                        //ֱ�Ӹ��ݷ����߹���һ�ݻ�����Ϣ
                        DatagramPacket responsePack = new DatagramPacket(buffer, len, receiver.getAddress(), responsePort);

                        ds.send(responsePack);

                        System.out.println("ServerProvider response to:" + clientIp + "\tport:" + responsePort + "\tdataLen:" + len);

                    } else {
                        System.out.println("ServerProvider receive cmd nonsupport;cmd::" + cmd + "\tport:" + port);

                    }
                }


            } catch (Exception ignore) {
            } finally {
                close();
            }
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
