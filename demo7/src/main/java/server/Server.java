package server;

import constants.TCPConstants;

import java.io.IOException;

/**
 * @author:zhumeng
 * @desc:
 **/
public class Server {
    public static void main(String[] args) {
        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER);
        boolean isSucceed = tcpServer.start();
        if (!isSucceed) {
            System.out.println("Start TCP Server failed.");
            return;
        }

        UDPServerProvider.start(TCPConstants.PORT_SERVER);

        try {
            //noinspection ResultOfMethedCallIgnored
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tcpServer.stop();
        UDPServerProvider.stop();
        System.out.println("Server Stop.");

    }

}
