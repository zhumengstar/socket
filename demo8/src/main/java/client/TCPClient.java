package client;

import client.bean.ServerInfo;
import utils.CloseUtils;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @author:zhumeng
 * @desc:
 **/
public class TCPClient {
    public static void linkWith(ServerInfo info) throws IOException {
        Socket socket = new Socket();
        //��ʱʱ��
        socket.setSoTimeout(3000);

        //���ӱ��ض˿�2000����ʱʱ��3000ms
        socket.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()), 3000);

        System.out.println("�ѷ�����������ӣ�������������̡�");

        System.out.println("�ͻ�����Ϣ��" + socket.getLocalAddress() + "  P:" + socket.getLocalPort());
        System.out.println("����������Ϣ��" + socket.getInetAddress() + "  P:" + socket.getPort());

        try {
            ReadHandler readHandler = new ReadHandler(socket.getInputStream());
            readHandler.start();

            //���ͽ�������
            write(socket);

            //�˳�
            readHandler.exit();
        } catch (Exception e) {
            System.out.println("�쳣�ر�");
        }
        //�ͷ���Դ
        socket.close();
        System.out.println("�ͻ������˳���");
    }

    private static void write(Socket client) throws IOException {
        //������������
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //�õ�Socket���������ת��Ϊ��ӡ��
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        do {
            //���̶�ȡһ��
            String str = input.readLine();
            //���͵�������
            socketPrintStream.println(str);


            if ("00bye00".equalsIgnoreCase(str)) {
                break;
            }
        } while (true);

        //�ͷ���Դ
        socketPrintStream.close();
    }

    static class ReadHandler extends Thread {
        private boolean done = false;
        private final InputStream inputStream;

        ReadHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }


        @Override
        public void run() {
            super.run();

            try {
                //�õ���ӡ�������������������������������ʹ��
//                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                //�õ������������ڽ�������
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));

                do {
                    String str;
                    try {
                        //�ͻ����õ�����
                        str = socketInput.readLine();
                    } catch (SocketTimeoutException e) {
                        continue;
                    }
                    if (str == null) {
                        System.out.println("�����ѹرգ��޷���ȡ���ݡ�");
                        break;
                    }
                    //��ӡ����Ļ
                    System.out.println(str);


                } while (!done);


            } catch (Exception e) {
                if (!done) {
                    System.out.println("�����쳣�Ͽ�:" + e.getMessage());
                }
            } finally {
                CloseUtils.close(inputStream);
            }
        }

        void exit() {
            done = true;
            CloseUtils.close(inputStream);
        }
    }

}
