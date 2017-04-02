package monkey.ast;

import monkey.token.Token;

public class InfixExpression implements Expression {
  private Token token;
  private Expression left;
  private String operator;
  private Expression right;

  public InfixExpression(Token token, Expression left, String operator, Expression right) {
    this.token = token;
    this.left = left;
    this.operator = operator;
    this.right = right;
  }

  public Expression getLeft() {
    return left;
  }

  public String getOperator() {
    return operator;
  }

  public Expression getRight() {
    return right;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    sb.append(left);
    sb.append(" " + operator + " ");
    sb.append(right);
    sb.append(")");
    return sb.toString();
  }
}
