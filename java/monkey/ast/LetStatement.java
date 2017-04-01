package monkey.ast;

import monkey.token.Token;

public class LetStatement implements Statement {
  private Token token; // Token.Type.LET
  private Identifier name;
  private Expression value;

  public LetStatement(Token token, Identifier name, Expression value) {
    this.token = token;
    this.name = name;
    this.value = value;
  }

  public Identifier getName() {
    return name;
  }

  public Expression getValue() {
    return value;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }
}
