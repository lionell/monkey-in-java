package monkey.object;

public class Int implements Obj {
  private long value;

  public Int(long value) {
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  @Override
  public String typeName() {
    return "INT";
  }

  @Override
  public String toString() {
    return Long.toString(value);
  }
}
