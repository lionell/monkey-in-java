package monkey.object;

import monkey.object.Obj.Type;

public class Bool implements Obj {
  private boolean value;

  public Bool(boolean value) {
    this.value = value;
  }

  @Override
  public Type type() {
    return Type.BOOL;
  }

  @Override
  public String inspect() {
    return Boolean.toString(value);
  }
}
