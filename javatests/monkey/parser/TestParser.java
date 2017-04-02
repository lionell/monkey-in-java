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
import monkey.ast.LetStatement;
import monkey.ast.ReturnStatement;
import monkey.ast.ExpressionStatement;
import monkey.ast.Identifier;
import monkey.ast.IntegerLiteral;
import monkey.ast.PrefixExpression;
import monkey.ast.InfixExpression;

@RunWith(JUnitParamsRunner.class)
public class TestParser {
  @Test
  @Parameters({
    "let x = 5;, x",
    "let foobar = 1234;, foobar"
  })
  public void testLetStatement(String input, String name) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();
    assertThat(program.getStatements().size()).isEqualTo(1);

    Statement s = program.getStatements().get(0);

    assertThat(s.tokenLiteral()).isEqualTo("let");
    LetStatement ls = (LetStatement)s;
    assertThat(ls.getName().tokenLiteral()).isEqualTo(name);
  }

  @Test
  @Parameters({
    "return 5;",
    "return 1234;"
  })
  public void testReturnStatement(String input) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();
    assertThat(program.getStatements().size()).isEqualTo(1);

    ReturnStatement rs = (ReturnStatement)program.getStatements().get(0);

    assertThat(rs.tokenLiteral()).isEqualTo("return");
  }

  @Test
  @Parameters({
    "x;, x",
    "foobar;, foobar"
  })
  public void testIdentifierExpression(String input, String value) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();
    assertThat(program.getStatements().size()).isEqualTo(1);

    ExpressionStatement es = (ExpressionStatement)program.getStatements().get(0);
    Identifier id = (Identifier)es.getExpression();

    assertThat(id.getValue()).isEqualTo(value);
  }

  @Test
  @Parameters({
    "5;, 5",
    "10;, 10"
  })
  public void testIntegerLiteralExpression(String input, Long value) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();
    assertThat(program.getStatements().size()).isEqualTo(1);

    ExpressionStatement es = (ExpressionStatement)program.getStatements().get(0);
    IntegerLiteral il = (IntegerLiteral)es.getExpression();

    assertThat(il.getValue()).isEqualTo(value);
  }

  @Test
  @Parameters({
    "-10;, -, 10",
    "!5;, !, 5"
  })
  public void testPrefixExpression(String input, String operator, Long right) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();
    assertThat(program.getStatements().size()).isEqualTo(1);

    ExpressionStatement es = (ExpressionStatement)program.getStatements().get(0);
    PrefixExpression pe = (PrefixExpression)es.getExpression();

    assertThat(pe.getOperator()).isEqualTo(operator);
    IntegerLiteral il = (IntegerLiteral)pe.getRight();
    assertThat(il.getValue()).isEqualTo(right);
  }

  @Test
  @Parameters({
    "5 + 5;, 5, +, 5",
    "10 + 123;, 10, +, 123"
  })
  public void testInfixExpression(String input, Long left, String operator, Long right) {
    Lexer l = new Lexer(input);
    Parser p = new Parser(l);

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();
    assertThat(program.getStatements().size()).isEqualTo(1);

    ExpressionStatement es = (ExpressionStatement)program.getStatements().get(0);
    InfixExpression ie = (InfixExpression)es.getExpression();

    assertThat(ie.getOperator()).isEqualTo(operator);
    IntegerLiteral il = (IntegerLiteral)ie.getLeft();
    assertThat(il.getValue()).isEqualTo(left);
    il = (IntegerLiteral)ie.getRight();
    assertThat(il.getValue()).isEqualTo(right);
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
  })
  public void testOperatorPrecedence(String input, String expected) {
    Parser p = new Parser(new Lexer(input));

    Program program = p.parseProgram();
    assertThat(p.getErrors()).isEmpty();
    assertThat(program.toString()).isEqualTo(expected);
  }
}
