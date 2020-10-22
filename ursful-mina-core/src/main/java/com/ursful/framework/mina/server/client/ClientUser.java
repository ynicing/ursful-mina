package com.ursful.framework.mina.server.client;

import com.ursful.framework.mina.common.support.User;

public class ClientUser extends User{
	private Client client;
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}

}
