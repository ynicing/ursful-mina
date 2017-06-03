package com.ursful.framework.database;

import java.sql.Connection;

public class PoolConnection{
		
		private Connection connection;
		private boolean isActive = false;
		private int transactional = 0;
		
		public Connection getConnection() {
			return connection;
		}

		public void setConnection(Connection connection) {
			this.connection = connection;
		}
		
		public int getTransactional() {
			return transactional;
		}

		public void increase() {
			this.transactional++;
		}
		
		public void decrease() {
			this.transactional--;
		}

		public PoolConnection(Connection connection){
			this.connection = connection;
		}
		 
		public boolean isActive() {
			return isActive;
		}
		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}
		
		
	}