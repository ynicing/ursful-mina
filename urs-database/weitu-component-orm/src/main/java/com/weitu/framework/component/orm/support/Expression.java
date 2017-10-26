package com.weitu.framework.component.orm.support;

public class Expression {

    public static final String EXPRESSION_ALL = "*";
    public static final String EXPRESSION_SUM = "SUM";
    public static final String EXPRESSION_MAX = "MAX";
    public static final String EXPRESSION_MIN = "MIN";
    public static final String EXPRESSION_AVG = "AVG";
    public static final String EXPRESSION_COUNT = "COUNT";


    private Column left;
	private ExpressionType type;
	private Object value;

	public Expression(Column left, Column value){
		this.left = left;
		this.type = ExpressionType.CDT_Equal;
		this.value = value;
	}

	public Expression(Column left, Object value, ExpressionType type){
		this.left = left;
		this.type = type;
		this.value = value;
	}

	public Column getLeft() {
		return left;
	}

    /**
     * set
     * @param left
     */
	public void setLeft(Column left) {
		this.left = left;
	}

	public ExpressionType getType() {
		return type;
	}
	public void setType(ExpressionType type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

    public static void main(String[] args) {

    }
}