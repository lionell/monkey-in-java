package monkey.object;

public class Bool implements Obj {
  private boolean value;

  public Bool(boolean value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return Boolean.toString(value);
  }
}
