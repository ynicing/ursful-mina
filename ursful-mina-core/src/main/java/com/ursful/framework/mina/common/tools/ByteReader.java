package com.ursful.framework.mina.common.tools;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ByteReader {
	
	public final static String CHARSET = "utf-8";
	
	private byte[] _data;
	private int _index;

	public ByteReader(byte[] data) {
		if(data == null){
			data = new byte[0];
		}
		this._data = data;
		this._index = 0;
	}

	public ByteReader(byte[] data, int from) {
		this._data = data;
		this._index = from;
	}

	public void skip(int index) {
		this._index += index;
	}

	public int readByte() {
		if (this._index < this._data.length) {
			int tmp = this._data[this._index] & 0xFF;
			this._index++;
			return tmp;
		}
		return 0;
	}

	public int readInt() {
		int a = readByte();
		int b = readByte() << 8;
		int c = readByte() << 16;
		int d = readByte() << 24;
		return a + b + c + d;
	}

	public long readLong() {
        long byte1 = readByte();
        long byte2 = readByte();
        long byte3 = readByte();
        long byte4 = readByte();
        long byte5 = readByte();
        long byte6 = readByte();
        long byte7 = readByte();
        long byte8 = readByte();

        return (byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16) +
                (byte2 << 8) + byte1;
    }
	
	public double readDouble() {
		 return Double.longBitsToDouble(readLong());
	}
	 
	public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }
	
	public int readShort() {
		int a = readByte();
		int b = readByte() << 8;
		return (short)(a + b);
	}

	public byte[] readRawData(int length) {
		byte[] tmp = new byte[length];
		if (this._index + length <= this._data.length) {
			System.arraycopy(this._data, this._index, tmp, 0, length);
			this._index += length;
			return tmp;
		}
		return null;
	}

	public int position(){
		return this._index;
	}

	public void position(int index){
		this._index = index;
	}

	public byte[] readBytes() {
		int length = readInt();
		byte[] tmp = new byte[length];
		if (this._index + length <= this._data.length) {
			System.arraycopy(this._data, this._index, tmp, 0, length);
			this._index += length;
			return tmp;
		}
		return null;
	}

	public String readString() {
		int length = readShort();
		byte[] data = readRawData(length);
		if (data != null && data.length > 0) {
			try {
				return new String(data, CHARSET);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public int available(){
		return Math.max(0, this._data.length - this._index);
	}

	public int length() {
		return this._data.length;
	}

	public byte[] getBytes(){
		return this._data;
	}

	public Map<String, Object> readObject(){
		byte [] bytes = readBytes();
		ByteReader br = new ByteReader(bytes);
		Map<String, Object> temp = new HashMap<String, Object>();
		while (br.available() > 0) {
			String key = br.readString();
			String type = br.readString();
			if ("Integer".equalsIgnoreCase(type)) {
				int value = br.readInt();
				temp.put(key, value);
			} else if ("String".equalsIgnoreCase(type)) {
				String value = br.readString();
				temp.put(key, value);
			} else if ("Long".equalsIgnoreCase(type)) {
				long value = br.readLong();
				temp.put(key, value);
			} else if ("Double".equalsIgnoreCase(type)) {
				double value = br.readDouble();
				temp.put(key, value);
			} else if ("Date".equalsIgnoreCase(type)) {
				long value = br.readLong();
				temp.put(key, new Date(value));
			} else {
				//none.
			}
		}
		return temp;
//		byte [] data = readBytes();
//		Object object = null;
//		try {
//			ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
//			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
//			object = objectInputStream.readObject();
//			objectInputStream.close();
//			inputStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		return (T)object;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int size = this._data.length;
		int length = Math.min(size, 10);
		for (int i = 0; i < length; i++) {
			sb.append(Integer.toHexString((0x000000ff&this._data[i]) | 0xFFFFFF00).substring(6) + " ");
		}
		if(size > 10){
			sb.append("... ");
			for(int i = Math.max(size - 10, 10); i < size; i++){
				sb.append(Integer.toHexString((0x000000ff&this._data[i]) | 0xFFFFFF00).substring(6) + " ");
			}
		}
		return "BR@" + Integer.toHexString(hashCode()) + " [" + sb.toString() + ", " +  size + "]";
	}

}
