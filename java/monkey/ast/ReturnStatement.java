package monkey.ast;

import monkey.token.Token;

public class ReturnStatement implements Statement {
  private Token token; // Token.Type.RETURN
  private Expression value;

  public ReturnStatement(Token token, Expression value) {
    this.token = token;
    this.value = value;
  }

  public Expression getValue() {
    return value;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(tokenLiteral() + " ");
    if (value != null) {
      sb.append(value);
    }
    sb.append(";");

    return sb.toString();
  }
}
