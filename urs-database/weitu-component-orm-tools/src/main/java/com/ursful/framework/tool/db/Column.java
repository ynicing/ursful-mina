package com.ursful.framework.tool.db;

public class Column{
    private String name;
		private String comment;
		private Boolean nullabe;
		
		public Column(String name, String comment, Boolean nullable){
			this.nullabe = nullable;
			this.name = name;
			this.comment = comment;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		public Boolean getNullabe() {
			return nullabe;
		}
		public void setNullabe(Boolean nullabe) {
			this.nullabe = nullabe;
		}
		
	}