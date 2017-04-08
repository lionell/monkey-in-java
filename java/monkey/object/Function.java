package monkey.object;

import java.util.List;

import com.google.common.base.Joiner;

import monkey.ast.Identifier;
import monkey.ast.BlockStatement;

public class Function implements Obj {
  private List<Identifier> parameters;
  private BlockStatement body;
  private Environment env;

  public Function(List<Identifier> parameters, BlockStatement body, Environment env) {
    this.parameters = parameters;
    this.body = body;
    this.env = env;
  }

  public List<Identifier> getParameters() {
    return parameters;
  }

  public BlockStatement getBody() {
    return body;
  }

  public Environment getEnv() {
    return env;
  }

  @Override
  public String typeName() {
    return "FUNCTION";
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("fn (");
    sb.append(Joiner.on(", ").join(parameters));
    sb.append(") { ");
    sb.append(body.toString());
    sb.append(" }");
    return sb.toString();
  }
}
