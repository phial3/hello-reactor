package com.mine.study.utils;

public class SteelyardUtil {
    public static final String DELIMITER = "_";
    public static final String PACKAGE_HEADER = "CZ_";
    public static final String ALLOW = "_ALLOW";
    public static final String REJECT = "_REJECT";
    public static final String REGISTER = "REGISTER";
    public static final String WEIGHT = "WEIGHT";
    public static final String PRINT = "PRINT";
    public static final String MODE = "MODE";
    public static final String BAT = "BAT";
    public static final String SETZERO = "SETZERO";
    public static final String DISPLAY = "DISPLAY";
    public static final String COMMSTATUS = "COMMSTATUS";
    public static final String KG = "kg";
    public static final int CRC_LENGTH = 4;
    public static final String LINE_FEED = "\r";

    public static final String HEARTBEAT = "120";


    public static boolean isPackage(String msg) {
        return (msg != null && msg.startsWith(PACKAGE_HEADER));
    }

    public static boolean isRegister(String msg) {
        return (isPackage(msg) && msg.endsWith(REGISTER));
    }

    public static boolean isReportWeight(String msg) {
        return (isPackage(msg) && msg.contains(WEIGHT) && msg.endsWith(KG));
    }

    public static boolean isPrintWeight(String msg) {
        return (isPackage(msg) && msg.contains(PRINT));
    }

    public static String getId(String msg) {
        String[] msgs = msg.split(DELIMITER);
        if (msgs.length >= 2) {
            return msgs[1];
        }
        return "";
    }

    public static String getOperate(String msg) {
        String[] msgs = msg.split(DELIMITER);
        if (msgs.length >= 2) {
            return msgs[2];
        }
        return "";
    }

    public static String allowAck(String id) {
        return (id + DELIMITER + REGISTER + ALLOW + DELIMITER + HEARTBEAT);
    }

    public static String rejectAck(String id) {
        return (id + DELIMITER + REGISTER + REJECT + DELIMITER + HEARTBEAT);
    }

    public static String batAck(String id) {
        return (id + DELIMITER + BAT + DELIMITER + HEARTBEAT);
    }

    //CZ_1901001_MODE _3_00123_015_010
    public static String setMode(String id, int mode, String weight, String less, String more) {
        return (id + DELIMITER + MODE + DELIMITER + mode + DELIMITER + weight + DELIMITER + less + DELIMITER + more);
    }

    //CZ_1901001_WEIGHT_0000191.8KG
    public static Double getWeight(String msg) {
        String[] msgs = msg.split(DELIMITER);
        if (msgs.length >= 4) {
            String weight = msgs[3];
            //负数
            if (weight.contains("-")) {
                int index = weight.indexOf("-");
                weight = weight.substring(index, weight.length() - 2);
            } else {
                weight = weight.substring(0, weight.length() - 2);
            }
            return Double.valueOf(weight);
        }
        return null;
    }

    //CZ_1901001_MODE_3_OK
    public static String getModeResult(String msg) {
        String[] msgs = msg.split(DELIMITER);
        if (msgs.length >= 5) {
            return msgs[4];
        }
        return null;
    }

    //CZ_1901001_SETZERO_ SUCCESS
    public static String getZeroResult(String msg) {
        String[] msgs = msg.split(DELIMITER);
        if (msgs.length >= 4) {
            return msgs[4];
        }
        return null;
    }

    //CZ_1901001_PRINT_N_131_0000191.8kg
    public static Double getPrintWeight(String msg) {
        if (!isPrintWeight(msg)) {
            return null;
        }
        String[] msgs = msg.split(DELIMITER);
        if (msgs.length >= 5) {
            String weight = msgs[5];
            weight = weight.substring(0, weight.length() - 2);
            return Double.valueOf(weight);
        }
        return null;
    }

    //CZ_1901001_COMMSTATUS_YES
    public static String getCommStatus(String msg) {
        String[] msgs = msg.split(DELIMITER);
        if (msgs.length >= 4) {
            return msgs[3];
        }
        return null;
    }

    public static Boolean checkCRC16(byte[] data, byte[] crc) {
        String crcData = CRC16.getCRC(data);
        return crcData.equals(new String(crc));
    }

}
