package monkey.lexer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import monkey.token.Token;
import monkey.lexer.Lexer;

public class TestLexer {
  @Test
  public void testNextToken() {
    String input = String.join("\n",
        "let ten = 10;",
        "let add = fn(x, y) { x + y; };",
        "let result = add(ten, ten);",
        "!-/*5;",
        "if (5 < 10) { return true; }",
        "else { return false; }",
        "10 == 10;",
        "10 != 9;"
    );
    Token[] tokens = {
      new Token(Token.Type.LET, "let"),
      new Token(Token.Type.IDENT, "ten"),
      new Token(Token.Type.ASSIGN, "="),
      new Token(Token.Type.INT, "10"),
      new Token(Token.Type.SEMICOLON, ";"),

      new Token(Token.Type.LET, "let"),
      new Token(Token.Type.IDENT, "add"),
      new Token(Token.Type.ASSIGN, "="),
      new Token(Token.Type.FUNCTION, "fn"),
      new Token(Token.Type.LPAREN, "("),
      new Token(Token.Type.IDENT, "x"),
      new Token(Token.Type.COMMA, ","),
      new Token(Token.Type.IDENT, "y"),
      new Token(Token.Type.RPAREN, ")"),
      new Token(Token.Type.LBRACE, "{"),
      new Token(Token.Type.IDENT, "x"),
      new Token(Token.Type.PLUS, "+"),
      new Token(Token.Type.IDENT, "y"),
      new Token(Token.Type.SEMICOLON, ";"),
      new Token(Token.Type.RBRACE, "}"),
      new Token(Token.Type.SEMICOLON, ";"),

      new Token(Token.Type.LET, "let"),
      new Token(Token.Type.IDENT, "result"),
      new Token(Token.Type.ASSIGN, "="),
      new Token(Token.Type.IDENT, "add"),
      new Token(Token.Type.LPAREN, "("),
      new Token(Token.Type.IDENT, "ten"),
      new Token(Token.Type.COMMA, ","),
      new Token(Token.Type.IDENT, "ten"),
      new Token(Token.Type.RPAREN, ")"),
      new Token(Token.Type.SEMICOLON, ";"),

      new Token(Token.Type.BANG, "!"),
      new Token(Token.Type.MINUS, "-"),
      new Token(Token.Type.SLASH, "/"),
      new Token(Token.Type.ASTERISK, "*"),
      new Token(Token.Type.INT, "5"),
      new Token(Token.Type.SEMICOLON, ";"),

      new Token(Token.Type.IF, "if"),
      new Token(Token.Type.LPAREN, "("),
      new Token(Token.Type.INT, "5"),
      new Token(Token.Type.LT, "<"),
      new Token(Token.Type.INT, "10"),
      new Token(Token.Type.RPAREN, ")"),
      new Token(Token.Type.LBRACE, "{"),
      new Token(Token.Type.RETURN, "return"),
      new Token(Token.Type.TRUE, "true"),
      new Token(Token.Type.SEMICOLON, ";"),
      new Token(Token.Type.RBRACE, "}"),
      new Token(Token.Type.ELSE, "else"),
      new Token(Token.Type.LBRACE, "{"),
      new Token(Token.Type.RETURN, "return"),
      new Token(Token.Type.FALSE, "false"),
      new Token(Token.Type.SEMICOLON, ";"),
      new Token(Token.Type.RBRACE, "}"),

      new Token(Token.Type.INT, "10"),
      new Token(Token.Type.EQ, "=="),
      new Token(Token.Type.INT, "10"),
      new Token(Token.Type.SEMICOLON, ";"),

      new Token(Token.Type.INT, "10"),
      new Token(Token.Type.NOT_EQ, "!="),
      new Token(Token.Type.INT, "9"),
      new Token(Token.Type.SEMICOLON, ";"),

      new Token(Token.Type.EOF, "")
    };
    Lexer l = new Lexer(input);
    for (Token t : tokens) {
      Token nt = l.nextToken();
      assertEquals(t.getType(), nt.getType());
      assertEquals(t.getLiteral(), nt.getLiteral());
    }
  }
}
