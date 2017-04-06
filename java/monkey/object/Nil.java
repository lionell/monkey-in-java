package monkey.object;

import monkey.object.Obj.Type;

public class Nil implements Obj {
  @Override
  public Type type() {
    return Type.NIL;
  }

  @Override
  public String inspect() {
    return "nil";
  }
}
