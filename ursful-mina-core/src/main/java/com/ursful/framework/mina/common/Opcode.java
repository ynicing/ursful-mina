package com.ursful.framework.mina.common;

public enum Opcode{
    // GENERAL
    TEST,
	PING,
	HELLO,
	INFO,// base info
	SERVER_INFO,// base info
	PRESENCE_INFO,//在线状态 6
	PRESENCE,//在线状态 7
	MESSAGE;//消息交互
}
