package monkey.token;

import com.google.common.collect.ImmutableMap;
import com.google.common.base.MoreObjects;

public class Token {
  private Type type;
  private String literal;

  public Token(Type type, String literal) {
    this.type = type;
    this.literal = literal;
  }

  public Token() {
    this(Type.ILLEGAL, "");
  }

  public Type getType() {
    return type;
  }

  public String getLiteral() {
    return literal;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("type", type)
      .add("literal", literal)
      .toString();
  }

  public enum Type {
    ILLEGAL,
    EOF,

    // Identifiers + literals
    IDENT,
    INT,

    // Operators
    ASSIGN,
    PLUS,
    MINUS,
    BANG,
    ASTERISK,
    SLASH,

    // Relations
    LT,
    GT,
    EQ,
    NOT_EQ,

    // Delimiters
    COMMA,
    SEMICOLON,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,

    // Keywords
    FUNCTION,
    LET,
    TRUE,
    FALSE,
    IF,
    ELSE,
    RETURN;

    private static final ImmutableMap<String, Type> keywords = ImmutableMap.<String, Type>builder()
        .put("fn", FUNCTION)
        .put("let", LET)
        .put("true", TRUE)
        .put("false", FALSE)
        .put("if", IF)
        .put("else", ELSE)
        .put("return", RETURN)
        .build();

    public static Type lookupIdent(String ident) {
      if (keywords.containsKey(ident)) {
        return keywords.get(ident);
      }
      return IDENT;
    }
  }
}
