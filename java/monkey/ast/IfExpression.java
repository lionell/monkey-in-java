package monkey.ast;

import monkey.token.Token;

public class IfExpression implements Expression {
  private Token token; // Token.Type.IF
  private Expression condition;
  private BlockStatement consequence;
  private BlockStatement alternative;

  public IfExpression(Token token, Expression condition, BlockStatement consequence,
      BlockStatement alternative) {
    this.token = token;
    this.condition = condition;
    this.consequence = consequence;
    this.alternative = alternative;
  }

  public Expression getCondition() {
    return condition;
  }

  public BlockStatement getConsequence() {
    return consequence;
  }

  public BlockStatement getAlternative() {
    return alternative;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("if");
    sb.append(condition);
    sb.append(" ");
    sb.append(consequence);
    if (alternative != null) {
      sb.append("else ");
      sb.append(alternative);
    }
    return sb.toString();
  }
}
