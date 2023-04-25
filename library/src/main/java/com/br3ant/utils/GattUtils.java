/*
 * Copyright (c) 2017 Huami Inc. All Rights Reserved.
 */

package com.br3ant.utils;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.UUID;

public final class GattUtils {
    private static final String TAG = "GattUtils";
    private static final String UUID_BASE = "0000%4s-0000-1000-8000-00805f9b34fb";
    // descriptor UUID
    public static final UUID UUID_DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION = uuid16("2902");
    public static final UUID UUID_DESCRIPTOR_CHARACTERISTIC_USER_CONFIGURATION = uuid16("2901");
    private static final String UUID_BASE_RE = String.format(UUID_BASE, "....");
    // Bluetooth Base UUID: "00000000-0000-1000-8000-00805f9b34fb"
    private static final long BT_UUID_M64 = 0x0000000000001000L;
    private static final long BT_UUID_L64 = 0x800000805f9b34fbL;
    private static final int[] CRC16_LOOKUP_TABLE = {0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241, 0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440, 0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40, 0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841, 0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40, 0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41, 0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641, 0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040, 0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240, 0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441, 0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41, 0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840, 0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41, 0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40, 0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640, 0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041, 0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240, 0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441, 0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41, 0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840, 0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41, 0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40, 0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640, 0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041, 0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241, 0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440, 0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40, 0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841, 0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40, 0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41, 0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641, 0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,};

    private static final long mostSigBits_128 = 0x0000000000003512L;
    private static final long leastSigBits_128 = 0x21180009af100700L;
    private static final UUID UUID_BASE_128 = new UUID(mostSigBits_128, leastSigBits_128);
    public static UUID nRF_DFU_BASE = new UUID(0x000000001212EFDEL, 0x1523785FEABCD123L);
    public static UUID TI_UUID_BASE = new UUID(0xF000000004514000L, 0xB000000000000000L);
    private static Field m_field_BluetoothGatt_mClientIf = null;

    public static UUID uuid16(final String uuid16) {
        return UUID.fromString(String.format(UUID_BASE, uuid16));
    }

    public static UUID uuid128(int uuid16) {
        return toUUID(UUID_BASE_128, (short) uuid16);
    }

    public static UUID uuid128(final String uuid128) {
        return UUID.fromString(uuid128);
    }

    public static UUID uuid(final byte[] data) {
        switch (data.length) {
            case 2:
                return uuid16(String.format("%02x%02x", data[0], data[1]));
            case 16:
                String sb = String.format("%02x%02x%02x%02x", data[0], data[1], data[2], data[3]) + String.format("-%02x%02x-%02x%02x", data[4], data[5], data[6], data[7]) + String.format("-%02x%02x-%02x%02x", data[8], data[9], data[10], data[11]) + String.format("%02x%02x%02x%02x", data[12], data[13], data[14], data[15]);
                return uuid128(sb);
            default:
                return null;
        }
    }

    public static UUID toUUID(final UUID uuid, int uuid16) {
        return toUUID(uuid, (short) uuid16);
    }

    private static UUID toUUID(final UUID uuid, short uuid16) { // XXX:
        final long v = uuid.getMostSignificantBits();
        return new UUID((v & 0xFFFF0000FFFFFFFFL) | ((uuid16 & 0xFFFFL) << 32), uuid.getLeastSignificantBits());
    }

    // XXX: UUID.toString(), String.format("%04X", uuid16);
    // public static UUID toUUID(String uuid_s) { return
    // UUID.fromString(uuid_s); }
    public static UUID toUUID(short uuid16) {
        return toUUID(uuid16 & 0xFFFF);
    }

    private static UUID toUUID(int uuid32) {
        return new UUID(((uuid32 & 0xFFFFFFFFL) << 32) | BT_UUID_M64, BT_UUID_L64);
    }

    public static short toUUID16(final UUID uuid) {
        return (short) toUUID32(uuid);
    }

    private static int toUUID32(final UUID uuid) {
        final long v = uuid.getMostSignificantBits();
        if (uuid.getLeastSignificantBits() == BT_UUID_L64 && (v & ~(0xFFFFFFFFL << 32)) == BT_UUID_M64) {
            return (int) (v >> 32);
        } else {
            return 0; // XXX:
        }
    }

    public static void writeLE(byte[] data, int i, short value) {
        data[i++] = (byte) (value & 0xFF);
        data[i] = (byte) ((value >> 8) & 0xFF);
    }

    public static void writeLE(byte[] data, int i, int value) {
        data[i++] = (byte) (value & 0xFF);
        data[i++] = (byte) ((value >> 8) & 0xFF);
        data[i++] = (byte) ((value >> 16) & 0xFF);
        data[i] = (byte) ((value >> 24) & 0xFF);
    }

    public static String bytesToHexString(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "null";
        }

        final StringBuilder sb = new StringBuilder();
        for (final byte b : bytes) {
            sb.append(String.format("%02x ", b));
        }
        return sb.substring(0, sb.length() - 1);
    }


    /**
     * bytes字符串转换为byte数组,字符串之间无空格
     */
    public static byte[] hexStringToBytes(String target) {

        if (target == null || target.length() == 0) {
            return null;
        }
        String str = target.replace(" ", "");

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public static String bytesToHexStringNoSpace(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "null";
        }

        final StringBuilder sb = new StringBuilder();
        for (final byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)
     * 来转换成16进制字符串
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesTo16HexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "null";
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }


    public static int crc16(int crc, final byte[] data, int i, int n) {
        // irreducible polynomial: 1 + x + x^5 + x^12 + x^16
        for (; i < n; ++i) { // crc16-CCITT
            /*
             * http://introcs.cs.princeton.edu/java/51data for (int i = 0; i <
             * 8; i++) { boolean bit = ((data[i] >> (7 - i) & 1) == 1); boolean
             * c15 = ((crc >> 15 & 1) == 1); crc <<= 1; if (c15 ^ bit) crc ^=
             * 0x1021; }
             */

            crc = (((crc >>> 8) | (crc << 8)) & 0xFFFF);
            crc ^= (data[i] & 0xFF);
            crc ^= ((crc & 0xFF) >> 4);
            crc ^= (crc << 12) & 0xFFFF;
            crc ^= ((crc & 0xFF) << 5) & 0xFFFF;
        }
        return crc;
    }

    public static int crc16(final byte[] buffer) {
        int crc = 0xffff;

        for (byte aBuffer : buffer) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
            crc ^= (aBuffer & 0xff);
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xff) << 5) & 0xffff;
        }
        crc &= 0xffff;

        return crc;
    }

    public static int crc16(final byte[] buffer, int start, int end) {
        int crc = 0xffff;

        if (start >= buffer.length || start > end) {
            return 0;
        }

        for (int i = start; i < end; i++) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
            crc ^= (buffer[i] & 0xff);
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xff) << 5) & 0xffff;
        }
        crc &= 0xffff;

        return crc;
    }

    public static int crc8(byte[] buffer) {
        int crc = 0;

        for (byte aBuffer : buffer) {
            crc ^= aBuffer & 0xff;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x01) != 0) {
                    crc = (crc >> 1) & 0xff;
                    crc ^= 0x8C;
                } else {
                    crc = (crc >> 1) & 0xff;
                }
            }
        }

        return crc;
    }

    @SuppressWarnings("unused")
    private static int crc16Fast(final byte[] buffer) {
        int crc = 0x0000;
        for (byte b : buffer) {
            crc = (crc >>> 8) ^ CRC16_LOOKUP_TABLE[(crc ^ b) & 0xff];
        }

        return crc;
    }

    /**
     * 以小端模式转有符号短整形
     */
    public static short bytesToSignShort(byte[] bytes, int offset) {
        return (short) ((bytes[offset + 1] << 8) | (bytes[offset] & 0xff));
    }

    /**
     * 以小端模式转有符号整数
     */
    public static int bytesToSignedInt(byte[] bytes, int offset) {
        return bytesToSignedInt(bytes, offset, 4);
    }

    /**
     * 以小端模式转有符号整数
     */
    public static int bytesToSignedInt(byte[] bytes, int offset, int len) {
        int result = 0;
        for (int i = 0; i < len; i++) {
            if (i == len - 1) {
                result = result | ((bytes[offset + i]) << (8 * i));
            } else {
                result = result | ((bytes[offset + i] & 0xff) << (8 * i));
            }
        }
        return result;
    }

    /**
     * 1.小端模式
     * 2.不能处理有符号数
     */
    public static int bytesToInt(byte[] bytes, int offset) {
        return bytesToInt(bytes, offset, 4);
    }

    /**
     * 1.小端模式
     * 2.不能处理有符号数
     */
    public static int bytesToInt(byte[] bytes, int offset, int len) {
        return (int) bytesToLong(bytes, offset, len);
    }

    public static int bytesToIntBE(byte[] bytes, int offset) {
        return bytesToIntBE(bytes, offset, 4);
    }

    public static int bytesToIntBE(byte[] bytes, int offset, int len) {
        return (int) bytesToLongBE(bytes, offset, len);
    }

    public static long bytesToLong(byte[] bytes, int offset) {
        return bytesToLong(bytes, offset, 8);
    }

    public static long bytesToLong(byte[] bytes, int offset, int len) {
        long ret = 0;
        for (int i = 0; i < len; i++) {
            ret |= ((long) bytes[offset + i] & 0xff) << (8 * i);
        }
        return ret;
    }

    public static long bytesToLongBE(byte[] bytes, int offset, int len) {
        long ret = 0;
        for (int i = 0; i < len; i++) {
            ret |= ((long) bytes[offset + i] & 0xff) << (8 * (len - 1 - i));
        }
        return ret;
    }

    public static Number bytesToSignedNumber(byte[] bytes, int offset, int len) {
        long ret = 0;
        boolean neg = (bytes[offset + len - 1] & 0x80) > 0;
        for (int i = 0; i < len; i++) {
            if (neg) {
                ret |= ((long) ~bytes[offset + i] & 0xff) << (8 * i);
            } else {
                ret |= ((long) bytes[offset + i] & 0xff) << (8 * i);
            }
        }
        if (neg) {
            ret = -ret - 1;
        }
        return ret;
    }

    public static byte[] longToBytes(long i) {
        return numberToBytes(i, 8);
    }

    public static byte[] intToBytes(int i) {
        return numberToBytes(i, 4);
    }

    public static byte[] shortToBytes(short s) {
        return numberToBytes(s, 2);
    }

    /**
     * 将数字转换为指定位数的字节数组
     */
    public static byte[] numberToBytes(long num, int bits) {
        byte[] result = new byte[bits];
        for (int i = 0; i < bits; i++) {
            result[i] = (byte) ((num >> (8 * i)) & 0xff);
        }
        return result;
    }

    /**
     * 将数字转换为指定位数的字节数组,大端模式
     */
    public static byte[] numberToBytesBE(long num, int bits) {
        byte[] result = new byte[bits];
        for (int i = 0; i < bits; i++) {
            result[i] = (byte) ((num >> (8 * (bits - 1 - i))) & 0xff);
        }
        return result;
    }

    public static String parseUUID(UUID uuid) {
        final String l = uuid.toString();
        if (l.matches(UUID_BASE_RE)) {
            return l.substring(4, 8);
        }
        return l;
    }

    public static String formatUUID(UUID uuid) {
        final String l = uuid.toString();
        if (l.matches(UUID_BASE_RE)) {
            return "0x" + l.substring(4, 8).toUpperCase(Locale.getDefault());
        }
        return l;
    }
}
