/**
 * @author:zhumeng
 * @desc:
 **/
public class MessageCreator {

    private static final String SN_HEADER = "�յ����ţ�����(SN) :";
    private static final String PORT_HEADER = "���ǰ��ţ���ص�˿�(PORT) :";

    public static String buildWithPort(int port) {
        return PORT_HEADER + port;
    }

    public static int parsePort(String data) {
        if (data.startsWith(PORT_HEADER)) {
            return Integer.parseInt(data.substring(PORT_HEADER.length()));
        }
        return -1;

    }

    public static String buildWithSn(String sn) {
        return SN_HEADER + sn;
    }

    public static String parseSn(String data) {
        if (data.startsWith(SN_HEADER)) {
            return data.substring(SN_HEADER.length());
        }
        return null;
    }
}
