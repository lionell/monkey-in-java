package monkey.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import monkey.token.Token;
import monkey.lexer.Lexer;
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

public class Parser {
  private Lexer lexer;
  private Token curToken;
  private Token peekToken;
  private Map<Token.Type, Supplier<Expression>> prefixParseFns;
  private Map<Token.Type, Function<Expression, Expression>> infixParseFns;
  private List<String> errors; // TODO: Use something instead of String for error handling.

  public Parser(Lexer lexer) {
    this.lexer = lexer;
    curToken = lexer.nextToken();
    peekToken = lexer.nextToken();

    prefixParseFns = new HashMap<>();
    registerPrefix(Token.Type.IDENT, this::parseIdentifier);
    registerPrefix(Token.Type.INT, this::parseIntegerLiteral);
    registerPrefix(Token.Type.BANG, this::parsePrefixExpression);
    registerPrefix(Token.Type.MINUS, this::parsePrefixExpression);

    infixParseFns = new HashMap<>();
    registerInfix(Token.Type.PLUS, this::parseInfixExpression);
    registerInfix(Token.Type.MINUS, this::parseInfixExpression);
    registerInfix(Token.Type.SLASH, this::parseInfixExpression);
    registerInfix(Token.Type.ASTERISK, this::parseInfixExpression);
    registerInfix(Token.Type.EQ, this::parseInfixExpression);
    registerInfix(Token.Type.NOT_EQ, this::parseInfixExpression);
    registerInfix(Token.Type.LT, this::parseInfixExpression);
    registerInfix(Token.Type.GT, this::parseInfixExpression);
    errors = new ArrayList<>();
  }

  private Expression parseInfixExpression(Expression left) {
    Token token = curToken;
    String operator = curToken.getLiteral();

    Precedence p = curPrecedence();
    nextToken();
    Expression right = parseExpression(p);

    return new InfixExpression(token, left, operator, right);
  }

  private Expression parsePrefixExpression() {
    Token token = curToken;
    String operator = curToken.getLiteral();

    nextToken();
    Expression right = parseExpression(Precedence.PREFIX);

    return new PrefixExpression(curToken, operator, right);
  }

  private Expression parseIntegerLiteral() {
    Token token = curToken;
    long value = Long.parseLong(curToken.getLiteral());
    // TODO: Check for parsing errors
    return new IntegerLiteral(token, value);
  }

  private Expression parseIdentifier() {
    return new Identifier(curToken, curToken.getLiteral());
  }

  public Program parseProgram() {
    List<Statement> statements = new ArrayList<>();
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
        return parseExpressionStatement();
    }
  }

  private Expression parseExpression(Precedence precedence) {
    Supplier<Expression> prefix = prefixParseFns.get(curToken.getType());
    if (prefix == null) {
      errors.add(String.format("No prefix parse function for %s found", curToken.getType()));
      return null;
    }
    Expression leftExp = prefix.get();

    while (!peekTokenIs(Token.Type.SEMICOLON) && precedence.compareTo(peekPrecedence()) < 0) {
      Function<Expression, Expression> infix = infixParseFns.get(peekToken.getType());
      if (infix == null) {
        return leftExp;
      }

      nextToken();
      leftExp = infix.apply(leftExp);
    }

    return leftExp;
  }

  private ExpressionStatement parseExpressionStatement() {
    Token token = curToken;

    Expression expression = parseExpression(Precedence.LOWEST);

    if (peekTokenIs(Token.Type.SEMICOLON)) {
      nextToken();
    }
    return new ExpressionStatement(token, expression);
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

  private void registerPrefix(Token.Type type, Supplier<Expression> fn) {
    prefixParseFns.put(type, fn);
  }

  private void registerInfix(Token.Type type, Function<Expression, Expression> fn) {
    infixParseFns.put(type, fn);
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

  public List<String> getErrors() {
    return errors;
  }

  private enum Precedence {
    LOWEST,
    EQUALS, // ==
    LESSGREATER, // > or <
    SUM, // +
    PRODUCT, // *
    PREFIX, // -x or !x
    CALL; // myFunction(x)
  }

  private static final Map<Token.Type, Precedence> precedences =
    ImmutableMap.<Token.Type, Precedence>builder()
    .put(Token.Type.EQ, Precedence.EQUALS)
    .put(Token.Type.NOT_EQ, Precedence.EQUALS)
    .put(Token.Type.LT, Precedence.LESSGREATER)
    .put(Token.Type.GT, Precedence.LESSGREATER)
    .put(Token.Type.PLUS, Precedence.SUM)
    .put(Token.Type.MINUS, Precedence.SUM)
    .put(Token.Type.SLASH, Precedence.PRODUCT)
    .put(Token.Type.ASTERISK, Precedence.PRODUCT)
    .build();

  private Precedence peekPrecedence() {
    Precedence p = precedences.get(peekToken.getType());
    if (p != null) {
      return p;
    }
    return Precedence.LOWEST;
  }

  private Precedence curPrecedence() {
    Precedence p = precedences.get(curToken.getType());
    if (p != null) {
      return p;
    }
    return Precedence.LOWEST;
  }
}
