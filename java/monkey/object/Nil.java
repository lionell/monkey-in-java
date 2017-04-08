package monkey.object;

public class Nil implements Obj {
  @Override
  public String typeName() {
    return "NIL";
  }

  @Override
  public String toString() {
    return "nil";
  }
}
