package monkey.ast;

import monkey.token.Token;

public class Identifier implements Expression {
  private Token token; // Token.Type.IDENT
  private String value;

  public Identifier(Token token, String value) {
    this.token = token;
    this.value = value;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }
}
