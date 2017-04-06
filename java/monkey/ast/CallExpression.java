package monkey.ast;

import java.util.List;

import com.google.common.base.Joiner;

import monkey.token.Token;

public class CallExpression implements Expression {
  private Token token; // Token.Type.(
  private Expression function; // Identifier or FunctionalLiteral
  private List<Expression> arguments;

  public CallExpression(Token token, Expression function, List<Expression> arguments) {
    this.token = token;
    this.function = function;
    this.arguments = arguments;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(function);
    sb.append("(");
    sb.append(Joiner.on(", ").join(arguments));
    sb.append(")");
    return sb.toString();
  }
}
