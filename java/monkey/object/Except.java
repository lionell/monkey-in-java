package monkey.object;

public class Except implements Obj {
  private String message;

  public Except(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String typeName() {
    return "EXCEPT";
  }

  @Override
  public String toString() {
    return "Error! " + message;
  }
}
