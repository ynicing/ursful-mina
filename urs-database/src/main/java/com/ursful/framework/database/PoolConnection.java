/*
 * Copyright 2017 @ursful.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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