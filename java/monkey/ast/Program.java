package monkey.ast;

import java.util.List;

public class Program implements Node {
  private List<Statement> statements;

  public Program(List<Statement> statements) {
    this.statements = statements;
  }

  public List<Statement> getStatements() {
    return statements;
  }

  @Override
  public String tokenLiteral() {
    if (!statements.isEmpty()) {
      return statements.get(0).tokenLiteral();
    } else {
      return "";
    }
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
