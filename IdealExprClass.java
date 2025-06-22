// This is the sample output file that the tools/ASTGenerator.java should generate and is kept here just to refer to the final file the code generator generates.
// These are the V tupple of the CGG for lox

import java.sql.Struct;

public abstract class IdealExprClass {
    public interface Visitor<T> {
        T visitBinaryExpr(Binary binaryExpr);
        T visitUnaryExpr(Unary unaryExpr);
        T visitGroupingExpr(Grouping groupingExpr);
        T visitLiteralExpr(Literal literalExpr);
    }

    abstract<R> R accept(Visitor<R> visitor);

    // functionalities/visitors
    /**
     * AstPrinter
     * Converts the infix expresion into a prefix expression
     */
    static class AstPrinter implements IdealExprClass.Visitor<String> {
        public String print(IdealExprClass expression) {
            return expression.accept(this);
        }

        @Override
        public String visitBinaryExpr(Binary binaryExpr) {
           return parenthesise(binaryExpr.operator.lexeme, binaryExpr.left, binaryExpr.right);
        }

        @Override
        public String visitUnaryExpr(Unary unaryExpr) {
            return parenthesise(unaryExpr.operator.lexeme, unaryExpr.expression);
        }

        @Override
        public String visitGroupingExpr(Grouping groupingExpr) {
            return parenthesise("group", groupingExpr.expression);
        }

        @Override
        public String visitLiteralExpr(IdealExprClass.Literal literalExpr) {
          if (literalExpr.value == null) return "nil";
          return literalExpr.value.toString();
        }

        private String parenthesise (String name, IdealExprClass ...expres) {
            StringBuilder builder = new StringBuilder();
            builder.append("(").append(name);

            for (IdealExprClass exp : expres) {
                builder.append(" ");
                builder.append(exp.accept(this));
            }

            builder.append(")");

            return builder.toString();
        }   
    }

    /**
     * RPNPrinter
     * Converts an infix expression into a Postfix expresion aka Reverse Polish Notation (RPN)
     */
    static class RPNPrinter implements Visitor<String> {
        public String print(IdealExprClass expression) {
            return expression.accept(this);
        }

        @Override
        public String visitBinaryExpr(IdealExprClass.Binary binaryExpr) {
            return parenthesise(binaryExpr.operator.lexeme, binaryExpr.left, binaryExpr.right);
        }

        @Override
        public String visitUnaryExpr(IdealExprClass.Unary unaryExpr) {
           return parenthesise(unaryExpr.operator.lexeme, unaryExpr.expression);
        }

        @Override
        public String visitGroupingExpr(IdealExprClass.Grouping groupingExpr) {
           return parenthesise("group", groupingExpr.expression);
        }

        @Override
        public String visitLiteralExpr(IdealExprClass.Literal literalExpr) {
            if (literalExpr.value == null) return "nil";
            return literalExpr.value.toString();
        }

        private String parenthesise (String name, IdealExprClass ...expres) {
            StringBuilder builder = new StringBuilder();
            builder.append("(");

            for (IdealExprClass exp : expres) {
                builder.append(exp.accept(this));
            }

            builder.append(name).append(")");

            return builder.toString();
        }
    }

    static class Binary extends IdealExprClass{
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
 
        Binary(IdealExprClass left, Token operator, IdealExprClass right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        final IdealExprClass left;
        final IdealExprClass right;
        final Token operator;
    }

    static class Unary extends IdealExprClass {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        Unary (Token operator, IdealExprClass expression) {
            this.operator = operator;
            this.expression = expression;
        }

        final Token operator;
        final IdealExprClass expression;
    }

    static class Grouping extends IdealExprClass {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        Grouping (IdealExprClass expression) {
            this.expression = expression;
        }

        final IdealExprClass expression;
    }

    static class Literal extends IdealExprClass {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        Literal (Object value) {
            this.value = value;
        }

        final Object value;
    }

    public static void main(String[] args) {
        /*
            Binary expression = new IdealExprClass.Binary(
                new IdealExprClass.Literal(1),
                new Token(TokenType.PLUS, null, 1, "+"),
                new IdealExprClass.Literal(2)
            );
        */

         IdealExprClass expression = new IdealExprClass.Binary(
            new IdealExprClass.Unary(
                new Token(TokenType.MINUS, null, 1, "-"),
                new IdealExprClass.Literal(123)),
            new Token(TokenType.STAR, null, 1, "*"),
            new IdealExprClass.Grouping(
                new IdealExprClass.Literal(45.67)));
        /*
            IdealExprClass expression = new IdealExprClass.Binary(
            new IdealExprClass.Unary(
                new Token(TokenType.MINUS, null, 1, "-"),
                new IdealExprClass.Literal(123)),
            new Token(TokenType.STAR, null, 1, "*"),
            new IdealExprClass.Grouping(
                new IdealExprClass.Literal(45.67)));
        */

        System.out.println(new AstPrinter().print(expression));

        // System.out.println(new RPNPrinter().print(expression));
    }
}
