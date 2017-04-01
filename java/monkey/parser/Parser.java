package monkey.parser;

import java.util.ArrayList;
import monkey.token.Token;
import monkey.lexer.Lexer;
import monkey.ast.Program;
import monkey.ast.Statement;
import monkey.ast.Expression;
import monkey.ast.Identifier;
import monkey.ast.LetStatement;
import monkey.ast.ReturnStatement;

public class Parser {
  private Lexer lexer;
  private Token curToken;
  private Token peekToken;
  private ArrayList<String> errors; // TODO: Use something instead of String for error handling.

  public Parser(Lexer lexer) {
    this.lexer = lexer;
    curToken = lexer.nextToken();
    peekToken = lexer.nextToken();
    errors = new ArrayList<>();
  }

  public Program parseProgram() {
    ArrayList<Statement> statements = new ArrayList<>();
    while (!curTokenIs(Token.Type.EOF)) {
      Statement st = parseStatement();
      if (st != null) {
        statements.add(st);
      }
      nextToken();
    }
    return new Program(statements);
  }

  private Statement parseStatement() {
    switch (curToken.getType()) {
      case LET:
        return parseLetStatement();
      case RETURN:
        return parseReturnStatement();
      default:
        return null;
    }
  }

  private ReturnStatement parseReturnStatement() {
    Token token = curToken;
    nextToken(); // Skip return

    Expression value = null; // TODO: Parse value

    while (!curTokenIs(Token.Type.SEMICOLON)) {
      nextToken();
    }
    return new ReturnStatement(token, value);
  }

  private LetStatement parseLetStatement() {
    Token token = curToken;

    if (!expectPeek(Token.Type.IDENT)) {
      return null;
    }
    Identifier name = new Identifier(curToken, curToken.getLiteral());

    if (!expectPeek(Token.Type.ASSIGN)) {
      return null;
    }

    Expression value = null; // TODO: Parse value

    while (!curTokenIs(Token.Type.SEMICOLON)) {
      nextToken();
    }
    return new LetStatement(token, name, value);
  }

  private boolean expectPeek(Token.Type type) {
    if (peekTokenIs(type)) {
      nextToken();
      return true;
    }
    peekError(type);
    return false;
  }

  private void peekError(Token.Type type) {
    errors.add(String.format("expected next token to be %s, got %s instead", type,
          peekToken.getType()));
  }

  private boolean curTokenIs(Token.Type type) {
    return curToken.getType() == type;
  }

  private boolean peekTokenIs(Token.Type type) {
    return peekToken.getType() == type;
  }

  private void nextToken() {
    curToken = peekToken;
    peekToken = lexer.nextToken();
  }

  public ArrayList<String> getErrors() {
    return errors;
  }
}
