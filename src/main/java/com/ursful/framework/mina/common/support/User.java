package com.ursful.framework.mina.common.support;

import com.ursful.framework.mina.server.client.Client;

public class User {
//	private String cid; // hyh@server
	private String id; //hyh
	private String domain;// server
	private String name;//黄永华

	public User(){
	}

	public User(String cid){
		setCid(cid);
	}

	public String getCid() {
		return id + "@" + domain;
	}

	public static String getId(String cid){
		int index = cid.indexOf("@");
		return cid.substring(0, index);
	}

	public static String getDomain(String cid){
		int index = cid.indexOf("@");
		return cid.substring(index + 1);
	}


	public void setCid(String cid) {
		if(cid == null){
			return;
		}
		int index = cid.indexOf("@");
		this.id = cid.substring(0, index);
		this.domain = cid.substring(index + 1);
	}

	public static void main(String[] args) {
		User user = new User();
		user.setCid("assds@234");
		System.out.println(user.getId());
		System.out.println(user.getDomain());
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

	@Override
	public String toString() {
		return "User[" + id +  "@" + domain + "]";
	}
}
