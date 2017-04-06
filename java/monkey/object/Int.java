package monkey.object;

import monkey.object.Obj.Type;

public class Int implements Obj {
  private long value;

  public Int(long value) {
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  @Override
  public Type type() {
    return Type.INT;
  }

  @Override
  public String toString() {
    return Long.toString(value);
  }
}
