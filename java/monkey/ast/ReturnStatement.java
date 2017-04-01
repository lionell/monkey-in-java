package monkey.ast;

import monkey.token.Token;

public class ReturnStatement implements Statement {
  private Token token; // Token.Type.RETURN
  private Expression value;

  public ReturnStatement(Token token, Expression value) {
    this.token = token;
    this.value = value;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }
}
