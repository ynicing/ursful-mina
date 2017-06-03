package com.ursful.framework.database.query;
public class Order{
		
		private Column column;
		private String order;
		
		public Order(Column column, String order){
			this.order = order;
			this.column = column;
		}
		
		public Column getColumn() {
			return column;
		}

		public void setColumn(Column column) {
			this.column = column;
		}

		public String getOrder() {
			return order;
		}
		public void setOrder(String order) {
			this.order = order;
		}
		
		
	}