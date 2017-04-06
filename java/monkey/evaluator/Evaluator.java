package monkey.evaluator;

import java.util.List;

import monkey.ast.Node;
import monkey.ast.Statement;
import monkey.ast.Program;
import monkey.ast.ExpressionStatement;
import monkey.ast.IntegerLiteral;
import monkey.ast.BoolLiteral;
import monkey.ast.PrefixExpression;
import monkey.ast.InfixExpression;
import monkey.object.Obj;
import monkey.object.Obj.Type;
import monkey.object.Int;
import monkey.object.Bool;
import monkey.object.Nil;

public class Evaluator {
  private static Bool TRUE = new Bool(true);
  private static Bool FALSE = new Bool(false);
  private static Nil NIL = new Nil();

  public static Obj eval(Node node) {
    if (node instanceof Program) {
      Program p = (Program)node;
      return evalStatements(p.getStatements());
    } else if (node instanceof ExpressionStatement) {
      ExpressionStatement es = (ExpressionStatement)node;
      return eval(es.getExpression());
    } else if (node instanceof IntegerLiteral) {
      IntegerLiteral il = (IntegerLiteral)node;
      return new Int(il.getValue());
    } else if (node instanceof BoolLiteral) {
      BoolLiteral bl = (BoolLiteral)node;
      return nativeBool(bl.getValue());
    } else if (node instanceof PrefixExpression) {
      PrefixExpression pe = (PrefixExpression)node;
      Obj right = eval(pe.getRight());
      return evalPrefixExpression(pe.getOperator(), right);
    } else if (node instanceof InfixExpression) {
      InfixExpression ie = (InfixExpression)node;
      Obj left = eval(ie.getLeft());
      Obj right = eval(ie.getRight());
      return evalInfixExpression(ie.getOperator(), left, right);
    }
    return NIL;
  }

  private static Obj evalInfixExpression(String operator, Obj left, Obj right) {
    if (left instanceof Int && right instanceof Int) {
      return evalIntegerInfixExpression(operator, (Int)left, (Int)right);
    } else if (operator == "==") {
      return nativeBool(left == right);
    } else if (operator == "!=") {
      return nativeBool(left != right);
    } else {
      return NIL;
    }
  }

  private static Obj evalIntegerInfixExpression(String operator, Int left, Int right) {
    switch (operator) {
      case "+":
        return new Int(left.getValue() + right.getValue());
      case "-":
        return new Int(left.getValue() - right.getValue());
      case "*":
        return new Int(left.getValue() * right.getValue());
      case "/":
        return new Int(left.getValue() / right.getValue());
      case "<":
        return nativeBool(left.getValue() < right.getValue());
      case ">":
        return nativeBool(left.getValue() > right.getValue());
      case "==":
        return nativeBool(left.getValue() == right.getValue());
      case "!=":
        return nativeBool(left.getValue() != right.getValue());
      default:
        return NIL;
    }
  }

  private static Obj evalPrefixExpression(String operator, Obj right) {
    switch (operator) {
      case "!":
        return evalBangOperatorExpression(right);
      case "-":
        return evalMinusPrefixOperatorExpression(right);
      default:
        return NIL;
    }
  }

  private static Obj evalMinusPrefixOperatorExpression(Obj right) {
    if (!(right instanceof Int)) {
      return NIL;
    }

    Int i = (Int)right;
    return new Int(-i.getValue());
  }

  private static Obj evalBangOperatorExpression(Obj right) {
    if (right.equals(TRUE)) {
      return FALSE;
    } else if (right.equals(FALSE)) {
      return TRUE;
    } else if (right.equals(NIL)) {
      return TRUE;
    } else {
      return FALSE;
    }
  }

  private static Obj evalStatements(List<Statement> statements) {
    Obj result = NIL;
    for (Statement st : statements) {
      result = eval(st);
    }
    return result;
  }

  private static Obj nativeBool(boolean value) {
    return value ? TRUE : FALSE;
  }
}
