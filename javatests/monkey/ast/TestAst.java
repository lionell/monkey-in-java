package monkey.ast;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import org.junit.Test;
import monkey.token.Token;

public class TestAst {
  @Test
  public void testToString() {
    Program p = new Program(
        Arrays.asList(
          new LetStatement(
            new Token(Token.Type.LET, "let"),
            new Identifier(new Token(Token.Type.IDENT, "myVar"), "myVar"),
            new Identifier(new Token(Token.Type.IDENT, "anotherVar"), "anotherVar")
          )
        )
      );
    assertEquals("let myVar = anotherVar;", p.toString());
  }
}
