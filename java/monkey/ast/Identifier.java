package monkey.ast;

import monkey.token.Token;

public class Identifier implements Expression {
  private Token token; // Token.Type.IDENT
  private String value;

  public Identifier(Token token, String value) {
    this.token = token;
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }

  @Override
  public String toString() {
    return value;
  }
}
