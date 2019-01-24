package server;

import server.handle.ClientHandle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:zhumeng
 * @desc:
 **/
public class TCPServer {
    private final int port;
    private ClientListener mListener;

    private List<ClientHandle> clientHandles = new ArrayList<>();

    public TCPServer(int port) {

        this.port = port;
    }

    public boolean start() {
        try {
            ClientListener listener = new ClientListener(port);
            mListener = listener;
            listener.start();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public void stop() {
        if (mListener != null) {
            mListener.exit();
        }
        for (ClientHandle clientHandle : clientHandles) {
            clientHandle.exit();
        }

        clientHandles.clear();
    }

    public void broadcast(String str) {
        for (ClientHandle clientHandle : clientHandles) {
            clientHandle.send(str);
        }
    }

    private class ClientListener extends Thread {
        private ServerSocket server;
        private boolean done = false;

        public ClientListener(int port) throws IOException {
            server = new ServerSocket(port);
            System.out.println("��������Ϣ��" + server.getInetAddress() + "\tP" + server.getLocalPort());

        }

        @Override
        public void run() {
            super.run();
            System.out.println("������׼��������");
            //�ȴ��ͻ�������
            do {
                //�õ��ͻ���
                Socket client = null;
                try {
                    client = server.accept();
                } catch (IOException e) {
                    e.printStackTrace();

                }
                //�ͻ��˹����첽�߳�
                ClientHandle clientHandle = null;
                try {
                    clientHandle = new ClientHandle(client, handler -> clientHandles.remove(handler));
                    //��ȡ���ݲ���ӡ
                    clientHandle.readToPrint();
                    clientHandles.add(clientHandle);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("�ͻ��������쳣" + e.getMessage());
                }

            } while (!done);
            System.out.println("�������ѹرա�");
        }

        void exit() {
            done = true;
            try {
                server.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //�ͻ�����Ϣ����
//    private static class ClientHandle extends Thread {
//        private Socket socket;
//        private boolean flag = true;
//
//        ClientHandle(Socket socket) {
//            this.socket = socket;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            System.out.println("�¿ͻ������ӣ�" + socket.getInetAddress() + "P:" + socket.getLocalPort());
//
//            try {
//                //�õ���ӡ�������������������������������ʹ��
//                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
//                //�õ������������ڽ�������
//                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//                do {
//                    //�ͻ����õ�����
//                    String str = socketInput.readLine();
//                    if ("bye".equalsIgnoreCase(str)) {
//                        flag = false;
//                        //����
//                        System.out.println("bye");
//                        socketOutput.println("bye");
//
//                    } else {
//                        //��ӡ����Ļ�����������ݳ���
//                        if (str != null) {
//                            System.out.println(str);
//                        }
//
//                        //���ؿͻ���
//                        socketOutput.println("���ͳ���:" + str.length());
//                    }
//                } while (flag);
//
//                socketInput.close();
//                socketOutput.close();
//
//            } catch (Exception e) {
//                System.out.println("�����쳣�Ͽ���");
//            } finally {
//                //���ӹر�
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("�ͻ������˳�" + socket.getInetAddress() + "P:" + socket.getPort());
//        }
//
//        public void exit(ClientHandle clientHandle) {
//
//        }
//
//        public void send(String str) {
//
//        }
//    }
}
