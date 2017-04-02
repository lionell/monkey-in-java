package monkey.ast;

import monkey.token.Token;

public class Bool implements Expression {
  private Token token;
  private boolean value;

  public Bool(Token token, boolean value) {
    this.token = token;
    this.value = value;
  }

  public boolean getValue() {
    return value;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }

  @Override
  public String toString() {
    return Boolean.toString(value);
  }
}
