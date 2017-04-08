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
import monkey.ast.BlockStatement;
import monkey.ast.IfExpression;
import monkey.ast.ReturnStatement;
import monkey.object.Obj;
import monkey.object.Int;
import monkey.object.Bool;
import monkey.object.Nil;
import monkey.object.ReturnValue;
import monkey.object.Except;

public class Evaluator {
  private static Bool TRUE = new Bool(true);
  private static Bool FALSE = new Bool(false);
  private static Nil NIL = new Nil();

  public static Obj eval(Node node) {
    if (node instanceof Program) {
      Program p = (Program)node;
      return evalProgram(p);
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
      if (right instanceof Except) {
        return right;
      }
      return evalPrefixExpression(pe.getOperator(), right);
    } else if (node instanceof InfixExpression) {
      InfixExpression ie = (InfixExpression)node;
      Obj left = eval(ie.getLeft());
      if (left instanceof Except) {
        return left;
      }
      Obj right = eval(ie.getRight());
      if (right instanceof Except) {
        return right;
      }
      return evalInfixExpression(ie.getOperator(), left, right);
    } else if (node instanceof BlockStatement) {
      BlockStatement bs = (BlockStatement)node;
      return evalBlockStatement(bs);
    } else if (node instanceof IfExpression) {
      IfExpression ie = (IfExpression)node;
      return evalIfExpression(ie);
    } else if (node instanceof ReturnStatement) {
      ReturnStatement rs = (ReturnStatement)node;
      Obj value = eval(rs.getValue());
      if (value instanceof Except) {
        return value;
      }
      return new ReturnValue(value);
    }
    return NIL;
  }

  private static Obj evalIfExpression(IfExpression ie) {
    Obj condition = eval(ie.getCondition());

    if (condition instanceof Except) {
      return condition;
    } else if (isTruthy(condition)) {
      return eval(ie.getConsequence());
    } else if (ie.getAlternative() != null) {
      return eval(ie.getAlternative());
    } else {
      return NIL;
    }
  }

  private static boolean isTruthy(Obj obj) {
    return !(obj == NIL || obj == FALSE);
  }

  private static Obj evalInfixExpression(String operator, Obj left, Obj right) {
    if (left instanceof Int && right instanceof Int) {
      return evalIntegerInfixExpression(operator, (Int)left, (Int)right);
    } else if (operator == "==") {
      return nativeBool(left == right);
    } else if (operator == "!=") {
      return nativeBool(left != right);
    } else if (left.getClass() != right.getClass()) {
      return new Except(String.format("type mistmatch: %s %s %s", left.typeName(), operator,
            right.typeName()));
    } else {
      return new Except(String.format("unknown operator: %s %s %s", left.typeName(), operator,
            right.typeName()));
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
        return new Except(String.format("unknown operator: INT %s INT", operator));
    }
  }

  private static Obj evalPrefixExpression(String operator, Obj right) {
    switch (operator) {
      case "!":
        return evalBangOperatorExpression(right);
      case "-":
        return evalMinusPrefixOperatorExpression(right);
      default:
        return new Except(String.format("unknown operator: %s%s", operator, right.typeName()));
    }
  }

  private static Obj evalMinusPrefixOperatorExpression(Obj right) {
    if (!(right instanceof Int)) {
      return new Except(String.format("unknown operator: -%s", right.typeName()));
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

  private static Obj evalProgram(Program p) {
    Obj result = null;
    for (Statement st : p.getStatements()) {
      result = eval(st);
      if (result instanceof ReturnValue) {
        ReturnValue rv = (ReturnValue)result;
        return rv.getValue();
      } else if(result instanceof Except) {
        return result;
      }
    }
    return result;
  }

  private static Obj evalBlockStatement(BlockStatement bs) {
    Obj result = null;
    for (Statement st : bs.getStatements()) {
      result = eval(st);
      if (result instanceof ReturnValue || result instanceof Except) {
        return result;
      }
    }
    return result;
  }

  private static Obj nativeBool(boolean value) {
    return value ? TRUE : FALSE;
  }
}
