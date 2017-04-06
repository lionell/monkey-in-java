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
import monkey.ast.BoolLiteral;
import monkey.ast.IfExpression;
import monkey.ast.BlockStatement;
import monkey.ast.FunctionLiteral;
import monkey.ast.CallExpression;

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
    registerPrefix(Token.Type.TRUE, this::parseBoolLiteral);
    registerPrefix(Token.Type.FALSE, this::parseBoolLiteral);
    registerPrefix(Token.Type.LPAREN, this::parseGroupedExpression);
    registerPrefix(Token.Type.IF, this::parseIfExpression);
    registerPrefix(Token.Type.FUNCTION, this::parseFunctionLiteral);

    infixParseFns = new HashMap<>();
    registerInfix(Token.Type.PLUS, this::parseInfixExpression);
    registerInfix(Token.Type.MINUS, this::parseInfixExpression);
    registerInfix(Token.Type.SLASH, this::parseInfixExpression);
    registerInfix(Token.Type.ASTERISK, this::parseInfixExpression);
    registerInfix(Token.Type.EQ, this::parseInfixExpression);
    registerInfix(Token.Type.NOT_EQ, this::parseInfixExpression);
    registerInfix(Token.Type.LT, this::parseInfixExpression);
    registerInfix(Token.Type.GT, this::parseInfixExpression);
    registerInfix(Token.Type.LPAREN, this::parseCallExpression);
    errors = new ArrayList<>();
  }

  private Expression parseCallExpression(Expression function) {
    Token token = curToken;
    List<Expression> arguments = parseCallArguments();

    return new CallExpression(curToken, function, arguments);
  }

  private List<Expression> parseCallArguments() {
    List<Expression> args = new ArrayList<>();

    if (peekTokenIs(Token.Type.RPAREN)) {
      nextToken();
      return args;
    }

    nextToken();
    args.add(parseExpression(Precedence.LOWEST));

    while (peekTokenIs(Token.Type.COMMA)) {
      nextToken();
      nextToken();
      args.add(parseExpression(Precedence.LOWEST));
    }

    if (!expectPeek(Token.Type.RPAREN)) {
      return null;
    }

    return args;
  }

  private Expression parseFunctionLiteral() {
    Token token = curToken;

    if (!expectPeek(Token.Type.LPAREN)) {
      return null;
    }

    List<Identifier> parameters = parseFunctionParameters();

    if (!expectPeek(Token.Type.LBRACE)) {
      return null;
    }

    BlockStatement body = parseBlockStatement();

    return new FunctionLiteral(token, parameters, body);
  }

  private List<Identifier> parseFunctionParameters() {
    List<Identifier> identifiers = new ArrayList<>();

    if (peekTokenIs(Token.Type.RPAREN)) {
      nextToken();
      return identifiers;
    }

    nextToken();
    identifiers.add((Identifier)parseIdentifier());

    while (peekTokenIs(Token.Type.COMMA)) {
      nextToken();
      nextToken();
      identifiers.add((Identifier)parseIdentifier());
    }

    if (!expectPeek(Token.Type.RPAREN)) {
      return null;
    }

    return identifiers;
  }

  private Expression parseIfExpression() {
    Token token = curToken;

    if (!expectPeek(Token.Type.LPAREN)) {
      return null;
    }
    nextToken(); // Eat (
    Expression condition = parseExpression(Precedence.LOWEST);
    if (!expectPeek(Token.Type.RPAREN)) {
      return null;
    }
    if (!expectPeek(Token.Type.LBRACE)) {
      return null;
    }
    BlockStatement consequence = parseBlockStatement();

    BlockStatement alternative = null;
    if (peekTokenIs(Token.Type.ELSE)) {
      nextToken();
      if (!expectPeek(Token.Type.LBRACE)) {
        return null;
      }
      alternative = parseBlockStatement();
    }

    return new IfExpression(token, condition, consequence, alternative);
  }

  private BlockStatement parseBlockStatement() {
    Token token = curToken;
    List<Statement> statements = new ArrayList<>();

    nextToken(); // Eat {
    while (!curTokenIs(Token.Type.RBRACE)) {
      Statement st = parseStatement();
      if (st != null) {
        statements.add(st);
      }
      nextToken();
    }

    return new BlockStatement(token, statements);
  }

  private Expression parseGroupedExpression() {
    nextToken(); // Eat (
    Expression e = parseExpression(Precedence.LOWEST);

    if (!expectPeek(Token.Type.RPAREN)) {
      return null;
    }
    return e;
  }

  private Expression parseBoolLiteral() {
    return new BoolLiteral(curToken, curTokenIs(Token.Type.TRUE));
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
    long value = Long.parseLong(curToken.getLiteral());
    // TODO: Check for parsing errors
    return new IntegerLiteral(curToken, value);
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
        break;
      }

      nextToken();
      leftExp = infix.apply(leftExp);
    }

    return leftExp;
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
    .put(Token.Type.LPAREN, Precedence.CALL)
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

    Expression value = parseExpression(Precedence.LOWEST);

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
    nextToken();

    Expression value = parseExpression(Precedence.LOWEST);

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
}
