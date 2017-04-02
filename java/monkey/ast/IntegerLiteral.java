package monkey.ast;

import monkey.token.Token;

public class IntegerLiteral implements Expression {
  private Token token;
  private long value;

  public IntegerLiteral(Token token, long value) {
    this.token = token;
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }

  @Override
  public String toString() {
    return Long.toString(value);
  }
}
