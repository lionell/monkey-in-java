package monkey.ast;

import java.util.List;

import com.google.common.base.Joiner;

import monkey.token.Token;

public class FunctionLiteral implements Expression {
  private Token token; // Token.Type.FUNCTION
  private List<Identifier> parameters;
  private BlockStatement body;

  public FunctionLiteral(Token token, List<Identifier> parameters, BlockStatement body) {
    this.token = token;
    this.parameters = parameters;
    this.body = body;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("fn (");
    sb.append(Joiner.on(", ").join(parameters));
    sb.append(")");
    sb.append(body);
    return sb.toString();
  }
}
