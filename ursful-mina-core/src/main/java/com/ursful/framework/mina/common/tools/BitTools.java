/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.ursful.framework.mina.common.tools;

/**
 * Provides static methods for working with raw byte sequences.
 * 
 * @author Frz
 * @since Revision 206
 * @version 1.0
 */
public class BitTools {

    /**
     * Static class dummy constructor.
     */
    private BitTools() {
    }

    /**
     * Reads a short from <coder>array</coder> at <coder>index</coder>
     * 
     * @param array The byte array to read the short integer from.
     * @param index Where reading begins.
     * @return The short integer value.
     */
    public static int getShort(byte array[], int index) {
        int ret = array[index];
        ret &= 0xFF;
        ret |= ((int) (array[index + 1]) << 8) & 0xFF00;
        return ret;
    }

    /**
     * Reads a string from <coder>array</coder> at
     * <coder>index</coder> <coder>length</coder> in length.
     * 
     * @param array The array to read the string from.
     * @param index Where reading begins.
     * @param length The number of bytes to read.
     * @return The string read.
     */
    public static String getString(byte array[], int index, int length) {
        char[] cret = new char[length];
        for (int x = 0; x < length; x++) {
            cret[x] = (char) array[x + index];
        }
        return String.valueOf(cret);
    }

    /**
     * Reads a maplestory-convention string from <coder>array</coder> at
     * <coder>index</coder>
     * 
     * @param array The byte array to read from.
     * @param index Where reading begins.
     * @return The string read.
     */
    public static String getMapleString(byte array[], int index) {
        int length = ((int) (array[index]) & 0xFF) | ((array[index + 1] << 8) & 0xFF00);
        return BitTools.getString(array, index + 2, length);
    }

    /**
     * Rotates the bits of <coder>in</coder> <coder>count</coder> places to the
     * left.
     * 
     * @param in The byte to rotate the bits
     * @param count Number of times to rotate.
     * @return The rotated byte.
     */
    public static byte rollLeft(byte in, int count) {
        /*
         * in: 11001101 count: 3 out: 0110 1110
         */
        int tmp = (int) in & 0xFF;
        tmp = tmp << (count % 8);
        return (byte) ((tmp & 0xFF) | (tmp >> 8));
    }

    /**
     * Rotates the bits of <coder>in</coder> <coder>count</coder> places to the
     * right.
     * 
     * @param in The byte to rotate the bits
     * @param count Number of times to rotate.
     * @return The rotated byte.
     */
    public static byte rollRight(byte in, int count) {
        /*
         * in: 11001101 count: 3 out: 1011 10011
         * 
         * 0000 1011 1011 0000 0101 1000
         * 
         */
        int tmp = (int) in & 0xFF;
        tmp = (tmp << 8) >>> (count % 8);

        return (byte) ((tmp & 0xFF) | (tmp >>> 8));
    }

    /**
     * Repeats <coder>count</coder> bytes of <coder>in</coder> <coder>mul</coder> times.
     * 
     * @param in The array of bytes containing the bytes to multiply.
     * @param count The number of bytes to repeat.
     * @param mul The number of times to repeat.
     * @return The repeated bytes.
     */
    public static byte[] multiplyBytes(byte[] in, int count, int mul) {
        byte[] ret = new byte[count * mul];
        for (int x = 0; x < count * mul; x++) {
            ret[x] = in[x % count];
        }
        return ret;
    }

    /**
     * Turns a double-precision floating point integer into an integer.
     * 
     * @param d The double to transform.
     * @return The converted integer.
     */
    public static int doubleToShortBits(double d) {
        long l = Double.doubleToLongBits(d);
        return (int) (l >> 48);
    }

    public static byte[] getInt(int value){
        byte[] temp = new byte[4];
        temp[0] = (byte)(value & 0x000000ff);
        temp[1] = (byte)((value>>8) & 0x000000ff);
        temp[2] = (byte)((value>>16) & 0x000000ff);
        temp[3] = (byte)((value>>24) & 0x000000ff);
        return temp;
    }

    public static int getInt(byte[] value){
        int a = (value[0]& 0xFF);
        int b = (value[1]& 0xFF) << 8;
        int c = (value[2]& 0xFF) << 16;
        int d = (value[3]& 0xFF) << 24;
        return a + b + c + d;
    }

    public static void main(String[] args) {
        byte[] data = getInt(195);
        int res = getInt(data);
        System.out.println(res);

        ByteWriter bw = new ByteWriter();
        bw.writeInt(195);
        ByteReader reader = new ByteReader(bw.getPacket().getBytes());
        System.out.println(reader.readInt());
    }
}
