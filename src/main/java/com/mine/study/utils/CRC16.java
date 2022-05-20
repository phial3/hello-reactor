package com.mine.study.utils;

public class CRC16 {
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

        String ssss = "010415000004";
        byte[] sbuf2 = getSendBuf(ssss);
        System.out.println(getBufHexStr(sbuf2));
    }

    public static AlgoParams Crc16CcittFalse = new AlgoParams("CRC-16/CCITT-FALSE", 16, 0x1021, 0xFFFF, false, false, 0x0, 0x29B1);
    public static AlgoParams Crc16Arc = new AlgoParams("CRC-16/ARC", 16, 0x8005, 0x0, true, true, 0x0, 0xBB3D);
    public static AlgoParams Crc16AugCcitt = new AlgoParams("CRC-16/AUG-CCITT", 16, 0x1021, 0x1D0F, false, false, 0x0, 0xE5CC);
    public static AlgoParams Crc16Buypass = new AlgoParams("CRC-16/BUYPASS", 16, 0x8005, 0x0, false, false, 0x0, 0xFEE8);
    public static AlgoParams Crc16Cdma2000 = new AlgoParams("CRC-16/CDMA2000", 16, 0xC867, 0xFFFF, false, false, 0x0, 0x4C06);
    public static AlgoParams Crc16Dds110 = new AlgoParams("CRC-16/DDS-110", 16, 0x8005, 0x800D, false, false, 0x0, 0x9ECF);
    public static AlgoParams Crc16DectR = new AlgoParams("CRC-16/DECT-R", 16, 0x589, 0x0, false, false, 0x1, 0x7E);
    public static AlgoParams Crc16DectX = new AlgoParams("CRC-16/DECT-X", 16, 0x589, 0x0, false, false, 0x0, 0x7F);
    public static AlgoParams Crc16Dnp = new AlgoParams("CRC-16/DNP", 16, 0x3D65, 0x0, true, true, 0xFFFF, 0xEA82);
    public static AlgoParams Crc16En13757 = new AlgoParams("CRC-16/EN-13757", 16, 0x3D65, 0x0, false, false, 0xFFFF, 0xC2B7);
    public static AlgoParams Crc16Genibus = new AlgoParams("CRC-16/GENIBUS", 16, 0x1021, 0xFFFF, false, false, 0xFFFF, 0xD64E);
    public static AlgoParams Crc16Maxim = new AlgoParams("CRC-16/MAXIM", 16, 0x8005, 0x0, true, true, 0xFFFF, 0x44C2);
    public static AlgoParams Crc16Mcrf4Xx = new AlgoParams("CRC-16/MCRF4XX", 16, 0x1021, 0xFFFF, true, true, 0x0, 0x6F91);
    public static AlgoParams Crc16Riello = new AlgoParams("CRC-16/RIELLO", 16, 0x1021, 0xB2AA, true, true, 0x0, 0x63D0);
    public static AlgoParams Crc16T10Dif = new AlgoParams("CRC-16/T10-DIF", 16, 0x8BB7, 0x0, false, false, 0x0, 0xD0DB);
    public static AlgoParams Crc16Teledisk = new AlgoParams("CRC-16/TELEDISK", 16, 0xA097, 0x0, false, false, 0x0, 0xFB3);
    public static AlgoParams Crc16Tms37157 = new AlgoParams("CRC-16/TMS37157", 16, 0x1021, 0x89EC, true, true, 0x0, 0x26B1);
    public static AlgoParams Crc16Usb = new AlgoParams("CRC-16/USB", 16, 0x8005, 0xFFFF, true, true, 0xFFFF, 0xB4C8);
    public static AlgoParams CrcA = new AlgoParams("CRC-A", 16, 0x1021, 0xc6c6, true, true, 0x0, 0xBF05);
    public static AlgoParams Crc16Kermit = new AlgoParams("CRC-16/KERMIT", 16, 0x1021, 0x0, true, true, 0x0, 0x2189);
    public static AlgoParams Crc16Modbus = new AlgoParams("CRC-16/MODBUS", 16, 0x8005, 0xFFFF, true, true, 0x0, 0x4B37);
    public static AlgoParams Crc16X25 = new AlgoParams("CRC-16/X-25", 16, 0x1021, 0xFFFF, true, true, 0xFFFF, 0x906E);
    public static AlgoParams Crc16Xmodem = new AlgoParams("CRC-16/XMODEM", 16, 0x1021, 0x0, false, false, 0x0, 0x31C3);

    public static final AlgoParams[] Params = new AlgoParams[]
            {
                    Crc16CcittFalse,
                    Crc16Arc,
                    Crc16AugCcitt,
                    Crc16Buypass,
                    Crc16Cdma2000,
                    Crc16Dds110,
                    Crc16DectR,
                    Crc16DectX,
                    Crc16Dnp,
                    Crc16En13757,
                    Crc16Genibus,
                    Crc16Maxim,
                    Crc16Mcrf4Xx,
                    Crc16Riello,
                    Crc16T10Dif,
                    Crc16Teledisk,
                    Crc16Tms37157,
                    Crc16Usb,
                    CrcA,
                    Crc16Kermit,
                    Crc16Modbus,
                    Crc16X25,
                    Crc16Xmodem,
            };

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    static final String HEXES = "0123456789ABCDEF";
    /**
     * crc16 for modbus
     * @param buf buffer to be crc
     * @param len buffer length
     * @return crc result word
     */
    static int alex_crc16(byte[] buf, int len) {
        int i, j;
        int c, crc = 0xFFFF;
        for (i = 0; i < len; i++) {
            c = buf[i] & 0x00FF;
            crc ^= c;
            for (j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else
                    crc >>= 1;
            }
        }
        return (crc);
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }
    /**
     * convert string of hex to buffer
     * @param src string to be convert
     * @return buffer
     */
    public static byte[] HexString2Buf(String src) {
        int len = src.length();
        byte[] ret = new byte[len / 2 + 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < len; i += 2) {
            ret[i / 2] = uniteBytes(tmp[i], tmp[i + 1]);
        }
        return ret;
    }
    /**
     * convert string to buffer and append the crc check word to the end of buffer
     * @param toSend string to be convert
     * @return buffer with crc word, high byte if after low byte according the modbus
     */
    public static byte[] getSendBuf(String toSend) {
        byte[] bb = HexString2Buf(toSend);
        int ri = alex_crc16(bb, bb.length - 2);
        bb[bb.length - 2] = (byte) (0xff & ri);
        bb[bb.length - 1] = (byte) ((0xff00 & ri) >> 8);
        return bb;
    }
    /**
     * print buffer to hex string
     * @param raw
     * @return
     */
    public static String getBufHexStr(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
                    HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }
    /**
     *
     * @param bb buffer to check
     * @return check result
     */
    public static boolean checkBuf(byte[] bb){
        int ri = alex_crc16(bb, bb.length-2);
        if(bb[bb.length-1]==(byte)(ri&0xff)
                && bb[bb.length-2]==(byte) ((0xff00 & ri) >> 8))
            return true;
        return false;
    }
}
