package monkey.ast;

import java.util.ArrayList;

public class Program implements Node {
  private ArrayList<Statement> statements;

  public Program(ArrayList<Statement> statements) {
    this.statements = statements;
  }

  public ArrayList<Statement> getStatements() {
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
}
