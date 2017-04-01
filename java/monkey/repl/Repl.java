package monkey.repl;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import monkey.token.Token;
import monkey.lexer.Lexer;

public class Repl {
  private static final String PROMPT = ">> ";

  public void start(InputStreamReader in, OutputStreamWriter out) throws IOException {
    try (BufferedReader bs = new BufferedReader(in)) {
      while (true) {
        out.write(PROMPT);
        out.flush();
        String line = bs.readLine();
        if (line == null) {
          return;
        }
        Lexer l = new Lexer(line);
        for (Token t = l.nextToken(); t.getType() != Token.Type.EOF; t = l.nextToken()) {
          out.write(t.toString());
          out.write("\n");
        }
      }
    }
  }
}
