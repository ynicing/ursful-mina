package com.weitu.framework.component.orm.support;

public class Express {

    private Column left;
	private ExpressionType type;
	private Object value;

    //solo.
    public Express(String left, Object value, ExpressionType type){
        this.left = new Column(left);
        this.type = type;
        this.value = value;
    }

    public Expression getExpression(){
        return new Expression(this.left, value, type);
    }

    public Express(String function, String left, Object value, ExpressionType type){
        this.left = new Column(function, null, left, null);
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