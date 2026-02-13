package com.jlox;

import com.jlox.Expr.Assignment;

// Polish(prefix) Notation printer visitor
// This visitor is used to verify whether the Parser is generating trees that follow the correct precedence and associativity of arithmetic expressions
public class PNPrinter implements Expr.Visitor<String> {
    public String print(Expr expression) {
        return expression.accept(this); // "this" => PNPrinter visitor class object
    }

    @Override
    public String visitBinary(Expr.Binary expression) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(expression.operator.lexeme);

        builder.append(parenthesise(expression.left, expression.right));

        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitUnary(Expr.Unary expression) {
        
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(expression.operator.lexeme);

        builder.append(parenthesise(expression.expression));

        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitGrouping(Expr.Grouping expression) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append("group");

        builder.append(parenthesise(expression.expression));

        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitLiteral(Expr.Literal expression) {
        if (expression.value == null) return "nil";
        else return " " + expression.value.toString() + " ";
    }

    /*
      This is similar to the Literal expression but we will use the 'lexeme' from the token, except of 'value' from the token
    */
    @Override
    public String visitVariable(Expr.Variable expression) {
        return " " + expression.name.lexeme + " ";
    }

    @Override
    public String visitAssignment(Assignment assignment) {
        return "To be implemented";
    }
    private String parenthesise(Expr ...exprs) {
        StringBuilder builder = new StringBuilder();

        for (Expr expr : exprs) {
            builder.append(expr.accept(this));
        }

        return builder.toString();
    }

    @Override
    public String visitTernary(Expr.Ternary expression) {
        StringBuilder builder = new StringBuilder();
        builder.append("ternary { ");

        builder.append(parenthesise(expression.conditional));
        builder.append(" ? ");

        builder.append(parenthesise(expression.trueBranch));
        builder.append(" : ");

        builder.append(parenthesise(expression.falseBranch));

        builder.append(" }");

        return builder.toString();
    }
}


