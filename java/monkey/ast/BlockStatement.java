package monkey.ast;

import java.util.List;

import monkey.token.Token;

public class BlockStatement implements Node {
  private Token token;
  private List<Statement> statements;

  public BlockStatement(Token token, List<Statement> statements) {
    this.token = token;
    this.statements = statements;
  }

  public List<Statement> getStatements() {
    return statements;
  }

  @Override
  public String tokenLiteral() {
    return token.getLiteral();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Statement st : statements) {
      sb.append(st.toString());
    }
    return sb.toString();
  }
}
