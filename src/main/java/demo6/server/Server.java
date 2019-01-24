package server;

import constants.TCPConstants;

import java.io.IOException;

/**
 * @author:zhumeng
 * @desc:
 **/
public class Server {
    public static void main(String[] args) {
        ServerProvider.start(TCPConstants.PORT_SERVER);

        try {
            //noinspection ResultOfMethedCallIgnored
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ServerProvider.stop();
        System.out.println("·þÎñÆ÷Í£Ö¹¡«");


    }
}
