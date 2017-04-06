package monkey.object;

public interface Obj {
  Type type();

  static enum Type {
    INT,
    BOOL,
    NIL;
  }
}
