package com.ursful.framework.mina.common.tools;

import com.ursful.framework.mina.common.packet.ByteArrayPacket;
import com.ursful.framework.mina.common.packet.Packet;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class ByteWriter {
	
	private byte [] _data;
	
	
	//int -> 4
	//short -> 2
	// [XX xx xx xx] ［xx xx xx xx xx］
	// 长度
	
	public static void main(String[] args) {
		
		
		ByteWriter bw = new ByteWriter();
		// 总长度  名称 图片数据
		// [xx xx xx xx] [xx xx] [xx] [xx xx xx xx] [bytes]
		bw.writeString("abc123");
		bw.writeInt(4);
		bw.writeString("abc");
		bw.writeByte(1);
		bw.writeShort(2);
		bw.writeFloat(1.1f);
		bw.writeDouble(2.01);
		bw.writeBytes(new byte[]{1,2,3,4,5});
		
		//byte ivSend[] = {82, 48, 120,  2};
       // AesOfb sendCypher = new AesOfb(AesOfb.AES_KEY, ivSend);
       // AesOfb recvCypher = new AesOfb(AesOfb.AES_KEY, ivSend);
	 
		System.out.println(bw.toString());
		
		ByteReader br = new ByteReader(bw.getPacket().getBytes());
		System.out.println(br.readString());
		System.out.println(br.readInt());
		System.out.println(br.readString());
		System.out.println(br.readByte());
		System.out.println(br.readShort());
		System.out.println(br.readFloat());
		System.out.println(br.readDouble());
		System.out.println(br.readBytes());
		System.out.println(br.toString());


//		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
//			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//				return new Date(json.getAsJsonPrimitive().getAsLong());
//			}
//		}).registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
//			@Override
//			public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
//				return new JsonPrimitive(src.getTime());
//			}
//		}).create();
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("a",1);
//		map.put("b", "x");
//		map.put("c", new Date());
//		String json = gson.toJson(map);
//		System.out.println(json);
//		System.out.println(gson.fromJson(json, Map.class));
//
//		Map<String, Object> table = new HashMap<>();
//		table.put("a", null);
//		table.put("a1", new Date());
//		table.put("a2", new Float(1.0));
//		table.put(null,"x");
//		table.entrySet().removeIf(new Predicate<Map.Entry<String, Object>>() {
//			@Override
//			public boolean test(Map.Entry<String, Object> stringObjectEntry) {
//				if(stringObjectEntry.getKey() == null){
//					return true;
//				}
//				Object value = stringObjectEntry.getValue();
//				if(value instanceof Integer){
//					return false;
//				}else if(value instanceof String){
//					return false;
//				}else if(value instanceof Long){
//					return false;
//				}else if(value instanceof Double){
//					return false;
//				}else if(value instanceof Date){
//					return false;
//				}else{
//					return true;
//				}
//			}
//		});
//		System.out.println(table);
		
	}

	public ByteWriter(byte[] data){
		this._data = data;
	}

    public ByteWriter(){
        this._data = new byte[0];
    }

    public void writeByte(int value){
        byte[] temp = this._data;
        this._data = new byte[temp.length + 1];
        System.arraycopy(temp, 0, this._data, 0, temp.length);
        this._data[temp.length] = (byte)(value & 0x000000ff);
    }
    
    public void writeDouble(double value) {
    	long v = Double.doubleToLongBits(value);
    	writeLong(v);
	}
	 
	public void writeFloat(float value) {
		int v = Float.floatToIntBits(value);
		writeInt(v);
   }

	//int, long, double, string, List
	public void writeObject(Map<String, Object> map){

		ByteWriter bw = new ByteWriter();
		if(map != null) {
			Set<String> keys = map.keySet();
			for (String key : keys) {
				if (key == null) {
					continue;
				}
				Object value = map.get(key);
				if(value instanceof Integer){
					bw.writeString(key);
					bw.writeString("Integer");
					bw.writeInt((Integer) value);
				}else if(value instanceof String){
					bw.writeString(key);
					bw.writeString("String");
					bw.writeString((String) value);
				}else if(value instanceof Long){
					bw.writeString(key);
					bw.writeString("Long");
					bw.writeLong((Long) value);
				}else if(value instanceof Double){
					bw.writeString(key);
					bw.writeString("Double");
					bw.writeDouble((Double) value);
				}else if(value instanceof Date){
					bw.writeString(key);
					bw.writeString("Date");
					bw.writeLong(((Date) value).getTime());
				}else{
					//none
				}

			}
		}
		byte[] temp = bw._data;
		writeBytes(temp);

//		try {
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
//			objectOutputStream.writeObject(object);
//			objectOutputStream.close();
//			writeBytes(outputStream.toByteArray());
//			outputStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

    public void writeInt(int value){
    	byte a = (byte)(value & 0x000000ff);
    	byte b = (byte)((value>>8) & 0x000000ff);
    	byte c = (byte)((value>>16) & 0x000000ff);
    	byte d = (byte)((value>>24) & 0x000000ff);
    	byte[] temp = this._data;
    	this._data = new byte[temp.length + 4];
        System.arraycopy(temp, 0, this._data, 0, temp.length);
        this._data[temp.length] = a;
        this._data[temp.length + 1] = b;
        this._data[temp.length + 2] = c;
        this._data[temp.length + 3] = d;
    }
    
    
    public void writeLong(long value){
    	byte a = (byte)(value & 0x000000ff);
    	byte b = (byte)((value>>8) & 0x000000ff);
    	byte c = (byte)((value>>16) & 0x000000ff);
    	byte d = (byte)((value>>24) & 0x000000ff);
    	byte e = (byte)((value>>32) & 0x000000ff);
    	byte f = (byte)((value>>40) & 0x000000ff);
    	byte g = (byte)((value>>48) & 0x000000ff);
    	byte h = (byte)((value>>56) & 0x000000ff);
    	byte[] temp = this._data;
    	this._data = new byte[temp.length + 8];
        System.arraycopy(temp, 0, this._data, 0, temp.length);
        this._data[temp.length] = a;
        this._data[temp.length + 1] = b;
        this._data[temp.length + 2] = c;
        this._data[temp.length + 3] = d;
        this._data[temp.length + 4] = e;
        this._data[temp.length + 5] = f;
        this._data[temp.length + 6] = g;
        this._data[temp.length + 7] = h;
    }

    public void writeShort(int value){
    	byte a = (byte)(value & 0x000000ff);
    	byte b = (byte)((value>>8) & 0x000000ff);
    	byte[] temp = this._data;
    	this._data = new byte[temp.length + 2];
        System.arraycopy(temp, 0, this._data, 0, temp.length);
        this._data[temp.length] = a;
        this._data[temp.length + 1] = b;
    }

	public void writeRawData(byte [] value){
		if(value.length > 0){
			byte [] temp = this._data;
			this._data = new byte[temp.length + value.length];
			System.arraycopy(temp, 0, this._data, 0, temp.length);
			System.arraycopy(value, 0, this._data, temp.length, value.length);
		}
	}

    public void writeBytes(byte [] value){
    	if(value.length == 0){
    		writeInt(0);
    	}else{
    		int length = value.length;
        	writeInt(length);
            byte [] temp = this._data;
            this._data = new byte[temp.length + value.length];
            System.arraycopy(temp, 0, this._data, 0, temp.length);
            System.arraycopy(value, 0, this._data, temp.length, value.length);
    	}
    }

    public void writeString(String str){
    	try {
    		if(str == null){
    			str = "";
    		}
			byte [] value = str.getBytes(ByteReader.CHARSET);
			
			writeShort(value.length);
			byte [] temp = this._data;
			this._data = new byte[temp.length + value.length];
	        System.arraycopy(temp, 0, this._data, 0, temp.length);
	        System.arraycopy(value, 0, this._data, temp.length, value.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
    }

    public int length(){
        return this._data.length;
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
		return "BW@" + Integer.toHexString(hashCode()) + " [" + sb.toString() + ", " +  size + "]";
	}
	
	public Packet getPacket(){
		return new ByteArrayPacket(_data);
	}
}
