package com.mine.study.utils;

public class CRC16 {

    public static String getCRC(byte[] bytes) {
        int CRC = 0xFFFF;
        int check = 0;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC =  (CRC >> 8) ^(int) bytes[i];
            for (j = 0; j < 8; j++) {
                check = CRC & 0x0001;
                CRC >>= 1;
                if (check == 0x0001) {
                    CRC ^= 0xA001;
                }
            }
        }
        // 交换高低位，低位在前高位在后
        //CRC = ((CRC & 0x0000FF00) >> 8) | ((CRC & 0x000000FF) << 8);
        String result = Integer.toHexString(CRC).toUpperCase();
        if(result.length()== 3){
            result = result.substring(0,2) + "0" + result.charAt(2);
        }
        return result.substring(0, 2) + "" + result.substring(2, 4);
    }

    public static byte[] getData(String...strings){
        byte[] data = new byte[]{};
        for (int i = 0; i < strings.length; i++) {
            int x = Integer.parseInt(strings[i],16);
            byte n = (byte) x;
            byte[] buffer = new byte[data.length + 1];
            byte[] aa = {n};
            System.arraycopy(data,0,buffer,0,data.length);
            System.arraycopy(aa,0,buffer,data.length,aa.length);
            data = buffer;
        }
        return getData(data);
    }

    public static byte[] getDataByArray(String[] strings){
        byte[] data = new byte[]{};
        for (int i = 0; i < strings.length; i++) {
            int x = Integer.parseInt(strings[i],16);
            byte n = (byte) x;
            byte[] buffer = new byte[data.length + 1];
            byte[] aa = {n};
            System.arraycopy(data,0,buffer,0,data.length);
            System.arraycopy(aa,0,buffer,data.length,aa.length);
            data = buffer;
        }
        return getData(data);
    }

    private static byte[] getData(byte[] aa){
        byte[] bb = getCrc16(aa);
        byte[] reverseArray = new byte[bb.length];
        for (int i = 0; i < bb.length; i++) {
            reverseArray[i] = bb[bb.length - i - 1];
        }
        byte[] cc = new byte[aa.length + bb.length];
        System.arraycopy(aa,0,cc,0,aa.length);
        System.arraycopy(reverseArray,0,cc,aa.length,reverseArray.length);
        return cc;
    }
    private static byte[] getCrc16(byte[] arr_buff){
        int len = arr_buff.length;
        int crc = 0xFFFF;
        int i,j;
        for (i = 0; i < len; i++){
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));
            for (j = 0; j < 8; j++){
                if((crc & 0x0001) > 0){
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                }else {
                    crc = crc >> 1;
                }
            }
        }
        return intToBytes(crc);
    }

    private static byte[] intToBytes(int value){
        byte[] src = new byte[2];
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static String byteTo16String(byte[] data){
        StringBuffer buffer = new StringBuffer();
        for (byte b : data){
            buffer.append(byteTo16String(b));
        }
        return buffer.toString();
    }

    public static String byteTo16String(byte b){
        StringBuffer buffer = new StringBuffer();
        int aa = (int) b;
        if(aa < 0){
            buffer.append(Integer.toString(aa + 256, 16) + " ");
        }else if(aa == 0){
            buffer.append("00 ");
        }else if(aa > 0 && aa <= 15){
            buffer.append("0" + Integer.toString(aa,16) + " ");
        }else if(aa > 15){
            buffer.append(Integer.toString(aa,16) + " ");
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
//        String s = "7E7E00123456780112343280080200152112221005061B";
//        String crc = CRC16.getCRC(s.getBytes());
//        System.out.println(crc);
        String s = "7E 7E 00 12 34 56 78 01 12 34 32 80 08 02 00 15 21 12 22 10 05 06 1B";
        String[] ss = s.split(" ");
        byte[] ff = getData("7E","7E","00","12","34","56","78","01","12","34","32","80","08","02","00","15","21","12","22","10","05","06","1B");
        byte[] dd = getDataByArray(ss);
        String str1 = byteTo16String(ff).toUpperCase();
        System.out.println(str1);
        String str = byteTo16String(dd).toUpperCase();
        System.out.println(str);
    }
}
