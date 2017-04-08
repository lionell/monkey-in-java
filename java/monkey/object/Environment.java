package monkey.object;

import java.util.Map;
import java.util.HashMap;

public class Environment {
  private Map<String, Obj> store;
  private Environment outer;

  public Environment(Map<String, Obj> store, Environment outer) {
    this.store = store;
    this.outer = outer;
  }

  public Environment(Environment e) {
    this(new HashMap<>(e.store), e.outer);
  }

  public Environment() {
    this(new HashMap<>(), null);
  }

  public static Environment enclose(Environment e) {
    return new Environment(new HashMap<>(), e);
  }

  public Obj get(String name) {
    Obj value = store.get(name);
    if (value == null && outer != null) {
      return outer.get(name);
    }
    return value;
  }

  public void set(String name, Obj value) {
    store.put(name, value);
  }
}
