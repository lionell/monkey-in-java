package monkey.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import org.junit.Test;
import monkey.lexer.Lexer;
import monkey.parser.Parser;
import monkey.ast.Program;
import monkey.ast.Statement;
import monkey.ast.LetStatement;
import monkey.ast.ReturnStatement;

public class TestParser {
  @Test
  public void testLetStatements() {
    String input = String.join("\n",
        "let x = 5;",
        "let foobar = 12344321;"
    );

    Lexer l = new Lexer(input);
    Parser p = new Parser(l);

    Program program = p.parseProgram();
    assertTrue(p.getErrors().isEmpty());
    assertNotNull(program);
    assertEquals(2, program.getStatements().size());

    Statement s = program.getStatements().get(0);
    assertEquals("let", s.tokenLiteral());
    LetStatement ls = (LetStatement)s;
    assertEquals("x", ls.getName().tokenLiteral());

    s = program.getStatements().get(1);
    assertEquals("let", s.tokenLiteral());
    ls = (LetStatement)s;
    assertEquals("foobar", ls.getName().tokenLiteral());
  }

  @Test
  public void testReturnStatements() {
    String input = String.join("\n",
        "return 5;",
        "return 1234;"
    );

    Lexer l = new Lexer(input);
    Parser p = new Parser(l);

    Program program = p.parseProgram();
    assertTrue(p.getErrors().isEmpty());
    assertNotNull(program);
    assertEquals(2, program.getStatements().size());

    for (Statement s : program.getStatements()) {
      ReturnStatement rs = (ReturnStatement)s;
      assertEquals("return", rs.tokenLiteral());
    }
  }
}
