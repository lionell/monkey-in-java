package monkey;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import monkey.repl.Repl;

public class Main {
  public static void main(String[] args) {
    try {
      Repl.start(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
