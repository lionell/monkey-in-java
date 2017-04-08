package monkey.evaluator;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import monkey.ast.Node;
import monkey.ast.Statement;
import monkey.ast.Expression;
import monkey.ast.Program;
import monkey.ast.ExpressionStatement;
import monkey.ast.IntegerLiteral;
import monkey.ast.BoolLiteral;
import monkey.ast.PrefixExpression;
import monkey.ast.InfixExpression;
import monkey.ast.BlockStatement;
import monkey.ast.IfExpression;
import monkey.ast.ReturnStatement;
import monkey.ast.LetStatement;
import monkey.ast.Identifier;
import monkey.ast.FunctionLiteral;
import monkey.ast.CallExpression;
import monkey.object.Obj;
import monkey.object.Environment;
import monkey.object.Int;
import monkey.object.Bool;
import monkey.object.Nil;
import monkey.object.ReturnValue;
import monkey.object.Except;
import monkey.object.Function;

public class Evaluator {
  private static final Bool TRUE = new Bool(true);
  private static final Bool FALSE = new Bool(false);
  public static final Nil NIL = new Nil();

  public static Obj eval(Node node, Environment env) {
    if (node instanceof Program) {
      Program p = (Program)node;
      return evalProgram(p, env);
    } else if (node instanceof ExpressionStatement) {
      ExpressionStatement es = (ExpressionStatement)node;
      return eval(es.getExpression(), env);
    } else if (node instanceof IntegerLiteral) {
      IntegerLiteral il = (IntegerLiteral)node;
      return new Int(il.getValue());
    } else if (node instanceof BoolLiteral) {
      BoolLiteral bl = (BoolLiteral)node;
      return nativeBool(bl.getValue());
    } else if (node instanceof PrefixExpression) {
      PrefixExpression pe = (PrefixExpression)node;
      Obj right = eval(pe.getRight(), env);
      if (right instanceof Except) {
        return right;
      }
      return evalPrefixExpression(pe.getOperator(), right);
    } else if (node instanceof InfixExpression) {
      InfixExpression ie = (InfixExpression)node;
      Obj left = eval(ie.getLeft(), env);
      if (left instanceof Except) {
        return left;
      }
      Obj right = eval(ie.getRight(), env);
      if (right instanceof Except) {
        return right;
      }
      return evalInfixExpression(ie.getOperator(), left, right);
    } else if (node instanceof BlockStatement) {
      BlockStatement bs = (BlockStatement)node;
      return evalBlockStatement(bs, env);
    } else if (node instanceof IfExpression) {
      IfExpression ie = (IfExpression)node;
      return evalIfExpression(ie, env);
    } else if (node instanceof ReturnStatement) {
      ReturnStatement rs = (ReturnStatement)node;
      Obj value = eval(rs.getValue(), env);
      if (value instanceof Except) {
        return value;
      }
      return new ReturnValue(value);
    } else if (node instanceof LetStatement) {
      LetStatement ls = (LetStatement)node;
      Obj value = eval(ls.getValue(), env);
      if (value instanceof Except) {
        return value;
      }
      if (value instanceof Function) { // Recursion
        Function fn = (Function)value;
        fn.getEnv().set(ls.getName().toString(), value);
      }
      env.set(ls.getName().toString(), value);
    } else if (node instanceof Identifier) {
      return evalIdentifier((Identifier)node, env);
    } else if (node instanceof FunctionLiteral) {
      FunctionLiteral fl = (FunctionLiteral)node;
      return new Function(fl.getParameters(), fl.getBody(), new Environment(env));
    } else if (node instanceof CallExpression) {
      CallExpression ce = (CallExpression)node;
      Obj function = eval(ce.getFunction(), env);
      if (function instanceof Except) {
        return function;
      }
      List<Obj> args = evalExpressions(ce.getArguments(), env);
      if (args.size() == 1 && args.get(0) instanceof Except) {
        return args.get(0);
      }
      return applyFunction(function, args);
    }
    return NIL;
  }

  private static Obj applyFunction(Obj func, List<Obj> args) {
    if (!(func instanceof Function)) {
      return new Except(String.format("not a function: %s", func.typeName()));
    }
    Function fn = (Function)func;
    Environment extendedEnv = extendFunctionEnv(fn, args);
    Obj result = eval(fn.getBody(), extendedEnv);
    return unwrapReturnValue(result);
  }

  private static Environment extendFunctionEnv(Function fn, List<Obj> args) {
    Environment env = Environment.enclose(fn.getEnv());

    List<Identifier> params = fn.getParameters();
    for (int i = 0; i < params.size(); i++) {
      env.set(params.get(i).getValue(), args.get(i));
    }

    return env;
  }

  private static Obj unwrapReturnValue(Obj value) {
    if (value instanceof ReturnValue) {
      ReturnValue rv = (ReturnValue)value;
      return rv.getValue();
    }
    return value;
  }

  private static List<Obj> evalExpressions(List<Expression> expressions, Environment env) {
    List<Obj> result = new ArrayList<>();
    for (Expression e : expressions) {
      Obj o = eval(e, env);
      if (o instanceof Except) {
        return Arrays.asList(o);
      }
      result.add(o);
    }
    return result;
  }

  private static Obj evalIdentifier(Identifier id, Environment env) {
    Obj value = env.get(id.getValue());
    if (value == null) {
      return new Except("identifier not found: " + id.getValue());
    }
    return value;
  }

  private static Obj evalIfExpression(IfExpression ie, Environment env) {
    Obj condition = eval(ie.getCondition(), env);

    if (condition instanceof Except) {
      return condition;
    } else if (isTruthy(condition)) {
      return eval(ie.getConsequence(), env);
    } else if (ie.getAlternative() != null) {
      return eval(ie.getAlternative(), env);
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

  private static Obj evalProgram(Program p, Environment env) {
    Obj result = NIL;
    for (Statement st : p.getStatements()) {
      result = eval(st, env);
      if (result instanceof ReturnValue) {
        ReturnValue rv = (ReturnValue)result;
        return rv.getValue();
      } else if(result instanceof Except) {
        return result;
      }
    }
    return result;
  }

  private static Obj evalBlockStatement(BlockStatement bs, Environment env) {
    Obj result = NIL;
    for (Statement st : bs.getStatements()) {
      result = eval(st, env);
      if (result instanceof ReturnValue || result instanceof Except) {
        return result;
      }
    }
    return result;
  }

  public static Obj nativeBool(boolean value) {
    return value ? TRUE : FALSE;
  }
}
