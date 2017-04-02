package monkey.ast;

import monkey.token.Token;

public class ExpressionStatement implements Statement {
  private Token token; // First token of the expression
  private Expression expression;

  public ExpressionStatement(Token token, Expression expression) {
    this.token = token;
    this.expression = expression;
  }

  public Expression getExpression() {
    return expression;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }

  @Override
  public String toString() {
    if (expression != null) {
      return expression.toString();
    }
    return "";
  }
}
