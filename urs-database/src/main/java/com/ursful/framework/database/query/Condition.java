package com.ursful.framework.database.query;

public class Condition{
		
		private Column left;
		private ConditionType type;
		private Object value;
		
		public Condition(Column left, Object value, ConditionType type){
			this.left = left;
			this.type = type;
			this.value = value;
		}
		
		public Column getLeft() {
			return left;
		}

		public void setLeft(Column left) {
			this.left = left;
		}
		 
		public ConditionType getType() {
			return type;
		}
		public void setType(ConditionType type) {
			this.type = type;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
	}