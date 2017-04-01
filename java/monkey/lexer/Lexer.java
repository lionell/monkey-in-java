package monkey.lexer;

import monkey.token.Token;

public class Lexer {
  private String input;
  private int position;
  private int readPosition;
  private char ch;

  public Lexer(String input) {
    this.input = input;
    this.readPosition = 0;
    readChar();
  }

  public Token nextToken() {
    skipWhitespace();

    Token t;
    switch (ch) {
      case '=':
        if (peekChar() == '=') {
          readChar();
          t = new Token(Token.Type.EQ, "==");
        } else {
          t = new Token(Token.Type.ASSIGN, "=");
        }
        break;
      case '+':
        t = new Token(Token.Type.PLUS, "+");
        break;
      case '-':
        t = new Token(Token.Type.MINUS, "-");
        break;
      case '!':
        if (peekChar() == '=') {
          readChar();
          t = new Token(Token.Type.NOT_EQ, "!=");
        } else {
          t = new Token(Token.Type.BANG, "!");
        }
        break;
      case '*':
        t = new Token(Token.Type.ASTERISK, "*");
        break;
      case '/':
        t = new Token(Token.Type.SLASH, "/");
        break;
      case '<':
        t = new Token(Token.Type.LT, "<");
        break;
      case '>':
        t = new Token(Token.Type.GT, ">");
        break;
      case ';':
        t = new Token(Token.Type.SEMICOLON, ";");
        break;
      case ',':
        t = new Token(Token.Type.COMMA, ",");
        break;
      case '(':
        t = new Token(Token.Type.LPAREN, "(");
        break;
      case ')':
        t = new Token(Token.Type.RPAREN, ")");
        break;
      case '{':
        t = new Token(Token.Type.LBRACE, "{");
        break;
      case '}':
        t = new Token(Token.Type.RBRACE, "}");
        break;
      case 0:
        t = new Token(Token.Type.EOF, "");
        break;
      default:
        if (isLetter(ch)) {
          String ident = readIdentifier();
          Token.Type type = Token.Type.lookupIdent(ident);
          return new Token(type, ident);
        } else if (Character.isDigit(ch)) {
          String number = readNumber();
          return new Token(Token.Type.INT, number);
        } else {
          t = new Token(Token.Type.ILLEGAL, Character.toString(ch));
        }
        break;
    }
    readChar();
    return t;
  }

  public void readChar() {
    if (readPosition >= input.length()) {
      ch = 0;
    } else {
      ch = input.charAt(readPosition);
    }
    position = readPosition;
    readPosition += 1;
  }

  public char peekChar() {
    if (readPosition >= input.length()) {
      return 0;
    } else {
      return input.charAt(readPosition);
    }
  }

  public String readIdentifier() {
    int start = position;
    while (isLetter(ch)) {
      readChar();
    }
    return input.substring(start, position);
  }

  public boolean isLetter(char ch) {
    return Character.isLetter(ch) || ch == '_';
  }

  public String readNumber() {
    int start = position;
    while (Character.isDigit(ch)) {
      readChar();
    }
    return input.substring(start, position);
  }

  public void skipWhitespace() {
    while (Character.isWhitespace(ch)) {
      readChar();
    }
  }
}
