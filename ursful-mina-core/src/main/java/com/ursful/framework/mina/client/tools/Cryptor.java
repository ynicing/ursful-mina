/**
 * @(#)com.ursful.framework.mina.client.Cryptor.java
 * Date: 2017 May 16, 2017
 *
 * Copyright (C) 2004-2008 XiamenCares. All rights reserved.
 * 
 * Description.
 *
 * travelsky.com
 */
package com.ursful.framework.mina.client.tools;

import com.ursful.framework.mina.common.tools.AesOfb;

public class Cryptor {
	
	public static final String CRYPTOR = "cryptor.key";
	
	private AesOfb send;
	private AesOfb recv;

	public Cryptor(){}

	public Cryptor(byte[] sendBytes, byte[] recvBytes){
		this.send = new AesOfb(AesOfb.AES_KEY, sendBytes);
		this.recv = new AesOfb(AesOfb.AES_KEY, recvBytes);
	}
	
	public AesOfb getSend() {
		return send;
	}
	public void setSend(AesOfb send) {
		this.send = send;
	}
	public AesOfb getRecv() {
		return recv;
	}
	public void setRecv(AesOfb recv) {
		this.recv = recv;
	}
	
	
	
}
