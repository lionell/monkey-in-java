package monkey.object;

import monkey.object.Obj.Type;

public class Int implements Obj {
  private long value;

  public Int(long value) {
    this.value = value;
  }

  @Override
  public Type type() {
    return Type.INT;
  }

  @Override
  public String inspect() {
    return Long.toString(value);
  }
}
