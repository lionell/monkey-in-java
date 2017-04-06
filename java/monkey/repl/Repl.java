package monkey.repl;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import monkey.lexer.Lexer;
import monkey.parser.Parser;
import monkey.ast.Program;

public class Repl {
  private static final String PROMPT = ">> ";
  private static final String MONKEY_FACE =
    "  /~\\ \n" +
    " C oo \n" +
    " _( ^) \n" +
    "/   ~\\ \n";

  public static void start(InputStreamReader in, OutputStreamWriter out) throws IOException {
    try (BufferedReader bs = new BufferedReader(in)) {
      while (true) {
        out.write(PROMPT);
        out.flush();
        String line = bs.readLine();
        if (line == null) {
          return;
        }
        Parser p = new Parser(new Lexer(line));
        Program program = p.parseProgram();
        if (!p.getErrors().isEmpty()) {
          printParseErrors(out, p.getErrors());
        }
        out.write(program.toString());
        out.write("\n");
      }
    }
  }

  private static void printParseErrors(OutputStreamWriter out, List<String> errors)
    throws IOException {
    out.write(MONKEY_FACE);
    out.write("Woops! We ran into some monkey business here!\n");
    out.write(" parser errors:\n");
    for (String e : errors) {
      out.write("\t" + e + "\n");
    }
  }
}
