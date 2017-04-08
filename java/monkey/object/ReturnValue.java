package monkey.object;

public class ReturnValue implements Obj {
  private Obj value;

  public ReturnValue(Obj value) {
    this.value = value;
  }

  public Obj getValue() {
    return value;
  }

  @Override
  public String typeName() {
    return "RETURN";
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
