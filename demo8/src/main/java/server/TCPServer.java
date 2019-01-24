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
            System.out.println("服务器信息：" + server.getInetAddress() + "\tP" + server.getLocalPort());

        }

        @Override
        public void run() {
            super.run();
            System.out.println("服务器准备就绪～");
            //等待客户端连接
            do {
                //得到客户端
                Socket client = null;
                try {
                    client = server.accept();
                } catch (IOException e) {
                    e.printStackTrace();

                }
                //客户端构建异步线程
                ClientHandle clientHandle = null;
                try {
                    clientHandle = new ClientHandle(client, handler -> clientHandles.remove(handler));
                    //读取数据并打印
                    clientHandle.readToPrint();
                    clientHandles.add(clientHandle);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("客户端连接异常" + e.getMessage());
                }

            } while (!done);
            System.out.println("服务器已关闭～");
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

    //客户端消息处理
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
//            System.out.println("新客户端连接：" + socket.getInetAddress() + "P:" + socket.getLocalPort());
//
//            try {
//                //得到打印流，用于数据输出，服务器回送数据使用
//                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
//                //得到输入流，用于接收数据
//                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//                do {
//                    //客户端拿到数据
//                    String str = socketInput.readLine();
//                    if ("bye".equalsIgnoreCase(str)) {
//                        flag = false;
//                        //回送
//                        System.out.println("bye");
//                        socketOutput.println("bye");
//
//                    } else {
//                        //打印到屏幕，并回送数据长度
//                        if (str != null) {
//                            System.out.println(str);
//                        }
//
//                        //发回客户端
//                        socketOutput.println("回送长度:" + str.length());
//                    }
//                } while (flag);
//
//                socketInput.close();
//                socketOutput.close();
//
//            } catch (Exception e) {
//                System.out.println("连接异常断开～");
//            } finally {
//                //连接关闭
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("客户端已退出" + socket.getInetAddress() + "P:" + socket.getPort());
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
