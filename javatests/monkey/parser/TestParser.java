package monkey.parser;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import monkey.lexer.Lexer;
import monkey.parser.Parser;
import monkey.ast.Program;
import monkey.ast.Statement;
import monkey.ast.Expression;
import monkey.ast.LetStatement;
import monkey.ast.ReturnStatement;
import monkey.ast.ExpressionStatement;
import monkey.ast.Identifier;
import monkey.ast.IntegerLiteral;
import monkey.ast.PrefixExpression;
import monkey.ast.InfixExpression;
import monkey.ast.Bool;
import monkey.ast.IfExpression;
import monkey.ast.BlockStatement;

@RunWith(JUnitParamsRunner.class)
public class TestParser {
  @Test
  @Parameters({
    "let x = 5;, x",
    "let foobar = 1234;, foobar",
  })
  public void testLetStatement(String input, String name) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();

    Statement st = extractTheOnlyOneStatement(program);
    assertThat(st).isInstanceOf(LetStatement.class);
    LetStatement ls = (LetStatement)st;

    assertThat(ls.tokenLiteral()).isEqualTo("let");
    assertThat(ls.getName().tokenLiteral()).isEqualTo(name);
    // TODO: Test identifier
  }

  private Statement extractTheOnlyOneStatement(Program p) {
    assertThat(p.getStatements().size()).isEqualTo(1);
    return p.getStatements().get(0);
  }

  @Test
  @Parameters({
    "return 5;",
    "return 1234;",
  })
  public void testReturnStatement(String input) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();

    Statement st = extractTheOnlyOneStatement(program);
    assertThat(st).isInstanceOf(ReturnStatement.class);
    ReturnStatement rs = (ReturnStatement)st;

    assertThat(rs.tokenLiteral()).isEqualTo("return");
    // TODO: Test identifier
  }

  @Test
  @Parameters({
    "x;, x",
    "foobar;, foobar",
  })
  public void testIdentfierExpression(String input, String value) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();

    Statement st = extractTheOnlyOneStatement(program);
    assertThat(st).isInstanceOf(ExpressionStatement.class);
    ExpressionStatement es = (ExpressionStatement)st;

    assertIdentifier(es.getExpression(), value);
  }

  private void assertIdentifier(Expression exp, String value) {
    assertThat(exp).isInstanceOf(Identifier.class);
    Identifier id = (Identifier)exp;

    assertThat(id.tokenLiteral()).isEqualTo(value);
    assertThat(id.getValue()).isEqualTo(value);
  }

  @Test
  @Parameters({
    "5;, 5",
    "10;, 10",
  })
  public void testIntegerLiteralExpression(String input, Long value) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();

    Statement st = extractTheOnlyOneStatement(program);
    assertThat(st).isInstanceOf(ExpressionStatement.class);
    ExpressionStatement es = (ExpressionStatement)st;

    assertIntegerLiteral(es.getExpression(), value);
  }

  private void assertIntegerLiteral(Expression exp, Long value) {
    assertThat(exp).isInstanceOf(IntegerLiteral.class);
    IntegerLiteral il = (IntegerLiteral)exp;

    assertThat(il.tokenLiteral()).isEqualTo(Long.toString(value));
    assertThat(il.getValue()).isEqualTo(value);
  }

  @Test
  @Parameters({
    "true;, true",
    "false;, false",
  })
  public void testBoolExpression(String input, Boolean value) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();

    Statement st = extractTheOnlyOneStatement(program);
    assertThat(st).isInstanceOf(ExpressionStatement.class);
    ExpressionStatement es = (ExpressionStatement)st;

    assertBool(es.getExpression(), value);
  }

  private void assertBool(Expression exp, Boolean value) {
    assertThat(exp).isInstanceOf(Bool.class);
    Bool b = (Bool)exp;

    assertThat(b.tokenLiteral()).isEqualTo(Boolean.toString(value));
    assertThat(b.getValue()).isEqualTo(value);
  }

  @Test
  @Parameters({
    "-10;, -, 10",
    "!5;, !, 5",
  })
  public void testPrefixExpressionWithIntegerLiterals(String input, String operator, Long right) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();

    Statement st = extractTheOnlyOneStatement(program);
    assertThat(st).isInstanceOf(ExpressionStatement.class);
    ExpressionStatement es = (ExpressionStatement)st;
    assertThat(es.getExpression()).isInstanceOf(PrefixExpression.class);
    PrefixExpression pe = (PrefixExpression)es.getExpression();

    assertThat(pe.getOperator()).isEqualTo(operator);
    assertIntegerLiteral(pe.getRight(), right);
  }

  @Test
  @Parameters({
    "!true;, !, true",
    "!false;, !, false",
  })
  public void testPrefixExpressionWithBools(String input, String operator, Boolean right) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();

    Statement st = extractTheOnlyOneStatement(program);
    assertThat(st).isInstanceOf(ExpressionStatement.class);
    ExpressionStatement es = (ExpressionStatement)st;
    assertThat(es.getExpression()).isInstanceOf(PrefixExpression.class);
    PrefixExpression pe = (PrefixExpression)es.getExpression();

    assertThat(pe.getOperator()).isEqualTo(operator);
    assertBool(pe.getRight(), right);
  }

  @Test
  @Parameters({
    "5 + 5;, 5, +, 5",
    "10 + 123;, 10, +, 123",
  })
  public void testInfixExpression(String input, Long left, String operator, Long right) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();

    ExpressionStatement es = (ExpressionStatement)extractTheOnlyOneStatement(program);
    assertThat(es.getExpression()).isInstanceOf(InfixExpression.class);
    InfixExpression ie = (InfixExpression)es.getExpression();

    assertInfixExpression(ie, left, operator, right);
  }

  @Test
  @Parameters({
    "-a * b, ((-a) * b)",
    "!-a, (!(-a))",
    "a + b - c, ((a + b) - c)",
    "a * b * c, ((a * b) * c)",
    "a * b / c, ((a * b) / c)",
    "a + b / c, (a + (b / c))",
    "a + b * c + d / e - f, (((a + (b * c)) + (d / e)) - f)",
    "3 + 4; -5 * 5, (3 + 4)((-5) * 5)",
    "5 > 4 == 3 < 4, ((5 > 4) == (3 < 4))",
    "5 > 4 != 3 < 4, ((5 > 4) != (3 < 4))",
    "3 + 4 * 5 == 3 * 1 + 4 * 5, ((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))",
    "1 + (2 + 3) + 4, ((1 + (2 + 3)) + 4)",
    "(5 + 5) * 2, ((5 + 5) * 2)",
    "2 / (5 + 5), (2 / (5 + 5))",
    "-(5 + 5), (-(5 + 5))",
    "!(true == true), (!(true == true))",
  })
  public void testOperatorPrecedence(String input, String expected) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();
    assertThat(program.toString()).isEqualTo(expected);
  }

  @Test
  public void testIfExpression() {
    String input = "if (x < y) { x }";
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();

    Statement st = extractTheOnlyOneStatement(program);
    assertThat(st).isInstanceOf(ExpressionStatement.class);
    ExpressionStatement es = (ExpressionStatement)st;
    assertThat(es.getExpression()).isInstanceOf(IfExpression.class);
    IfExpression ie = (IfExpression)es.getExpression();
    assertInfixExpression(ie.getCondition(), "x", "<", "y");
    BlockStatement bs = ie.getConsequence();
    assertThat(bs.getStatements().size()).isEqualTo(1);
    st = bs.getStatements().get(0);
    assertThat(st).isInstanceOf(ExpressionStatement.class);
    es = (ExpressionStatement)st;
    assertIdentifier(es.getExpression(), "x");
    assertThat(ie.getAlternative()).isNull();
  }

  @Test
  public void testIfElseExpression() {
    String input = "if (x < y) { x } else { y }";
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();

    Statement st = extractTheOnlyOneStatement(program);
    assertThat(st).isInstanceOf(ExpressionStatement.class);
    ExpressionStatement es = (ExpressionStatement)st;
    assertThat(es.getExpression()).isInstanceOf(IfExpression.class);
    IfExpression ie = (IfExpression)es.getExpression();
    assertInfixExpression(ie.getCondition(), "x", "<", "y");
    BlockStatement bs = ie.getConsequence();
    assertThat(bs.getStatements().size()).isEqualTo(1);
    st = bs.getStatements().get(0);
    assertThat(st).isInstanceOf(ExpressionStatement.class);
    es = (ExpressionStatement)st;
    assertIdentifier(es.getExpression(), "x");
    bs = ie.getAlternative();
    assertThat(bs.getStatements().size()).isEqualTo(1);
    st = bs.getStatements().get(0);
    assertThat(st).isInstanceOf(ExpressionStatement.class);
    es = (ExpressionStatement)st;
    assertIdentifier(es.getExpression(), "y");
  }

  private void assertInfixExpression(Expression exp, Object left, String operator, Object right) {
    assertThat(exp).isInstanceOf(InfixExpression.class);
    InfixExpression ie = (InfixExpression)exp;

    assertThat(ie.getOperator()).isEqualTo(operator);
    assertLiteralExpression(ie.getLeft(), left);
    assertLiteralExpression(ie.getRight(), right);
  }

  private void assertLiteralExpression(Expression exp, Object value) {
    if (value instanceof Long) {
      assertIntegerLiteral(exp, (Long)value);
    } else if (value instanceof String) {
      assertIdentifier(exp, (String)value);
    } else if (value instanceof Boolean) {
      assertBool(exp, (Boolean)value);
    }
  }

  @Test
  public void testFunctionLiteral() { // Not a real test
    String input = "fn(x, y) { x + y; }";
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();
  }

  @Test
  public void testCallExpression() { // Not a real test
    String input = "add(1, 2 * 3, 4 + 5);";
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();
  }
}
