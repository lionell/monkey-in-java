package monkey.evaluator;

import java.util.List;

import monkey.ast.Node;
import monkey.ast.Statement;
import monkey.ast.Program;
import monkey.ast.ExpressionStatement;
import monkey.ast.IntegerLiteral;
import monkey.object.Obj;
import monkey.object.Int;

public class Evaluator {
  public Obj eval(Node node) {
    if (node instanceof Program) {
      Program p = (Program)node;
      return evalStatements(p.getStatements());
    } else if (node instanceof ExpressionStatement) {
      ExpressionStatement es = (ExpressionStatement)node;
      return eval(es.getExpression());
    } else if (node instanceof IntegerLiteral) {
      IntegerLiteral il = (IntegerLiteral)node;
      return new Int(il.getValue());
    }
    return null;
  }

  private Obj evalStatements(List<Statement> statements) {
    Obj result = null;
    for (Statement st : statements) {
      result = eval(st);
    }
    return result;
  }
}
