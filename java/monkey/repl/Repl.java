package monkey.repl;

import java.util.List;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

import com.google.common.base.Joiner;

import monkey.ast.Program;
import monkey.lexer.Lexer;
import monkey.parser.Parser;
import monkey.object.Obj;
import monkey.object.Environment;
import monkey.evaluator.Evaluator;

public class Repl {
  private static final String PROMPT = ">> ";
  private static final String MONKEY_FACE =
    "  /~\\ \n" +
    " C oo \n" +
    " _( ^) \n" +
    "/   ~\\ \n";

  public static void start(InputStreamReader in, OutputStreamWriter out) throws IOException {
    try (BufferedReader bs = new BufferedReader(in)) {
      Environment env = new Environment();
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
          continue;
        }

        Obj result = Evaluator.eval(program, env);
        if (result != null) {
          out.write(result.toString());
          out.write("\n");
        }
      }
    }
  }

  public static void run(String path) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
    String text = Joiner.on("").join(lines);
    Environment env = new Environment();
    Parser p = new Parser(new Lexer(text));
    Program program = p.parseProgram();
    if (!p.getErrors().isEmpty()) {
      printParseErrors(new OutputStreamWriter(System.out), p.getErrors());
    }
    Obj result = Evaluator.eval(program, env);
    if (result != null) {
      System.out.println(result);
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
