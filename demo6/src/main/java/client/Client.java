package client;

import client.bean.ServerInfo;

/**
 * @author:zhumeng
 * @desc:
 **/
public class Client {
    public static void main(String[] args) {
        ServerInfo info = ClientSearcher.searchServer(10000);
        System.out.println("Server:" + info);
    }
}
