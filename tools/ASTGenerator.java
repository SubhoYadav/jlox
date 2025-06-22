package tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class ASTGenerator {
    private static void generateASTClass(List<String> specs, String baseClsName, String ouptutDir) throws IOException {
        String path = ouptutDir + '\\'  + baseClsName + ".java"; 
        System.out.println(path);
        PrintWriter printWriter = new PrintWriter(path);

        // Adding a comment
        printWriter.println("// These are the V tupple of the CGG for lox");
        printWriter.println("public abstract class " + baseClsName + "{ ");

        defineVisitorInterface(printWriter, specs);
        
        printWriter.println("abstract<R> R accept(Visitor<R> visitor);");

        for (String spec : specs) {
            String nestedClsName = spec.split(":")[0].trim();
            String fieldStr =  spec.split(":")[1].trim();
            generateNestedClasses(printWriter, baseClsName, nestedClsName, fieldStr);
        }

        printWriter.println("}");


        printWriter.close();
        System.out.println("File written!");
    }

    static private void defineVisitorInterface(PrintWriter printWriter, List<String> specs) {
        printWriter.println("       public interface Visitor<T> {");

        for (String spec : specs) {
            printWriter.println("T visit" + spec.split(":")[0].trim() + "(" + spec.split(":")[0].trim() + " expression);");
        }

        printWriter.println("       }");
    }

    static private void generateNestedClasses(PrintWriter printWriter, String baseClsName, String nestedClsName, String fieldStr) {
       printWriter.println("    static class " + nestedClsName + " extends " + baseClsName + "{");
        
       // generating the implementation of the abstract method accept
       printWriter.println("@Override");
       printWriter.println("public <R> R accept(Visitor<R> visitor) {");
       printWriter.println("return visitor.visit" + nestedClsName + "(this);");
       printWriter.println("}");

       // generating the constructor
       printWriter.println("        " + nestedClsName + "(" + fieldStr + ") {");
       for (String field : fieldStr.split(", ")) {
        String instanceVarName = field.split(" ")[1];
        printWriter.println("           this." + instanceVarName.substring(1) + "=" + instanceVarName + ";"); // done to convert _opertor to operator
       }
       printWriter.println("        }");

       // generating the instance variables
       for (String field : fieldStr.split(", ")) {
        String instanceVarName = field.split(" ")[1];
        printWriter.println("       final " + field.split(" ")[0] + " " + instanceVarName.substring(1) + ";"); // done to convert _opertor to operator
       }

       printWriter.println("    }");
    }
    
    public static void main(String[] args) throws IOException {
        List<String> specs = Arrays.asList(
            "Binary: Expr _left, Token _operator, Expr _right",
                "Unary: Token _operator, Expr _expression",
                "Grouping: Expr _expression",
                "Literal: Object _value"
        );

        generateASTClass(specs, "Expr", "C:\\learn\\jlox");
    }
}
