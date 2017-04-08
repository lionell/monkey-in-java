package monkey;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileReader;

import monkey.repl.Repl;

public class Main {
  public static void main(String[] args) throws IOException {
    if (args.length == 1) {
      Repl.run(args[0]);
    } else {
      Repl.start(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
    }
  }
}
