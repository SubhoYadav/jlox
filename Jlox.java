import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Jlox {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    private static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        /*
            for (Token token : tokens) {
                System.out.println(token.toString());
            }
        */

        Parser parser = new Parser(tokens);
        Expr parsedExpression = parser.startParsing();

        // interpret the AST
        new Expr.Interpreter().interpret(parsedExpression);

        /*
            Expr parsedExpression = new Expr.Binary(
                new Expr.Literal(2), 
                new Token(TokenType.PLUS, null, 1, "+"), 
                new Expr.Literal(3)
            );
        */

        /*
            parsedExpression = new Expr.Binary(
                new Expr.Unary(
                    new Token(TokenType.MINUS, null, 1, "-"),
                    new Expr.Literal(123)),
                new Token(TokenType.STAR, null, 1, "*"),
                new Expr.Grouping(
                    new Expr.Literal(45.67)));
        */

        // Expr.PNPrinter pnPrinter = new Expr.PNPrinter();
        // System.out.println(pnPrinter.print(parsedExpression));
    }

    private static void runFile(String path) throws IOException {
        byte bytes[] = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) System.exit(65);

        if (hadRuntimeError) System.exit(70); 
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }

            run(line);
            hadError = false;
        }
    }

    /**
     * This method is used to report errors in the lexer/tokenizer
     */
    static void error (int line, String message) {
        report(line, "", message);
    }

    /**
     * This is the polymorphered error function that reports errors in the parser
     */
    static void error(Token token, String message) {
        if (token.tokenType == TokenType.EOF) {
            report(token.line, " at end ", message);
        }
        else {
            report(token.line, " at " + token.lexeme + "'", message);
        }
    }

    static void runtimeError (RuntimeError error) {
        System.err.println(error.getMessage() +
        "\n" + "[line: " + error.token.line + "]");

        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        // output e.g. [line: 10] Error: unexpected ,
        System.out.println("[line: " + line + "]" + "Error: " + where + message);
        hadError = true;
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("usage: jlox [file_path]");
            System.exit(64);
        }
        else if (args.length == 1) {
            runFile(args[0]);
        }
        else {
            runPrompt();
        }
    }
}
