package monkey.object;

public interface Obj {
  Type type();
  String inspect();

  static enum Type {
    INT,
    BOOL,
    NIL;
  }
}
