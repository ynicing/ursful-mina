package com.ursful.framework.mina.common.support;

import com.ursful.framework.mina.server.client.Client;

import java.io.Serializable;

public class User implements Serializable{

	private static final long serialVersionUID = 1L;

//	private String cid; // hyh@server
	private String id; //hyh
	private String domain;// server
	private String name;//黄永华
	private String resource;

	public User(){
	}

	public User(String cid){
		setCid(cid);
	}

	public String getCid() {
		String cid = id + "@" + domain;
		if(resource != null && !"".equalsIgnoreCase(resource)){
			cid += "/" + resource;
		}
		return cid;
	}

	public static String getId(String cid){
		String [] parts = getParts(cid);
		return parts[0];
	}

	public static String getDomain(String cid){
		String [] parts = getParts(cid);
		return parts[1];
	}

	public static String getResource(String cid){
		String [] parts = getParts(cid);
		return parts[2];
	}

	public void setCid(String cid) {
		if(cid == null){
			return;
		}
		String [] parts = getParts(cid);
		this.id = parts[0];
		this.domain = parts[1];
		this.resource = parts[2];

	}



	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	@Override
	public String toString() {
		return "User[" + getCid() + "]";
	}

	public static void main(String[] args) {
		String [] testIds = new String[]{
				"test@xxxx",
				"xxxx@",
				"@xxxx",
				"test@yyy@xxxx",
				"testyyy@xxxx/test",
				"@/",
				"z@/",
				"@x/",
				"@/xxx"};
		for(int i = 0; i < testIds.length; i++){
			User user = new User(testIds[i]);
			System.out.println(String.format("Cid:%s Id:%s Domain:%s Resource:%s", user.getCid(), user.getId(), user.getDomain(), user.getResource()));
		}
	}

	public static String[] getParts(String cid) {
		String [] parts = new String[]{"","",""};
		if(cid == null){
			return parts;
		}
		int atIndex = cid.lastIndexOf("@");

		if(atIndex != -1 && atIndex > 0){
			parts[0] = cid.substring(0, atIndex);
			int slashIndex = cid.indexOf("/", atIndex);
			if(slashIndex != -1){
				if(atIndex + 1 < slashIndex) {
					parts[1] = cid.substring(atIndex + 1, slashIndex);
				}
				if(slashIndex + 1 < cid.length()) {
					parts[2] = cid.substring(slashIndex + 1);
				}
			}else{
				if(atIndex + 1 < cid.length()) {
					parts[1] = cid.substring(atIndex + 1);
				}
			}
		}else{
			int slashIndex = cid.indexOf("/");
			if(slashIndex != -1){
				if(atIndex + 1 < slashIndex) {
					parts[1] = cid.substring(atIndex + 1, slashIndex);
				}
				if(slashIndex + 1 < cid.length()) {
					parts[2] = cid.substring(slashIndex + 1);
				}
			}else{
				parts[1] = cid.substring(atIndex + 1);
			}
		}

		return parts;
	}
}
