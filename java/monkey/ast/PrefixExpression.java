package monkey.ast;

import monkey.token.Token;

public class PrefixExpression implements Expression {
  private Token token;
  private String operator;
  private Expression right;

  public PrefixExpression(Token token, String operator, Expression right) {
    this.token = token;
    this.operator = operator;
    this.right = right;
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
    sb.append(operator);
    sb.append(right);
    sb.append(")");
    return sb.toString();
  }
}
