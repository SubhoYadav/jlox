// The child classes needs to be static so that they can be instantiated without the reference of the outer class 'Expr'
package com.jlox;
public abstract class Expr {
   // Below interface is implemented by the Visitor classes
   public interface Visitor<T> {
      T visitBinary(Binary expression);
      T visitUnary(Unary expression);
      T visitGrouping(Grouping expression);
      T visitLiteral(Literal expression);
      T visitTernary(Ternary expression);
   }

   // Below method is an abstract one, so all the sub classes that extends this base class, needs to define its body
   // and this will select the proper visitor method based on the 'Expr' object it is called from. 
   abstract<R> R accept(Visitor<R> visitor);

   // Polish(prefix) Notation printer visitor
   static class PNPrinter implements Expr.Visitor<String> {
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

   static class Binary extends Expr {
      @Override
      public <R> R accept(Visitor<R> visitor) {
         return visitor.visitBinary(this);
      }
      Binary(Expr _left, Token _operator, Expr _right) {
         this.left=_left;
         this.operator=_operator;
         this.right=_right;
      }
      final Expr left;
      final Token operator;
      final Expr right;
   }


   static class Unary extends Expr {
      @Override
      public <R> R accept(Visitor<R> visitor) {
         return visitor.visitUnary(this);
      }

      Unary(Token _operator, Expr _expression) {
         this.operator=_operator;
         this.expression=_expression;
      }

      final Token operator;
      final Expr expression;
   }
    
   static class Grouping extends Expr {
      @Override
      public <R> R accept(Visitor<R> visitor) {
         return visitor.visitGrouping(this);
      }
      Grouping(Expr _expression) {
         this.expression=_expression;
      }
      final Expr expression;
   }

   static class Literal extends Expr {
      @Override
      public <R> R accept(Visitor<R> visitor) {
         return visitor.visitLiteral(this);
      }
      Literal(Object _value) {
         this.value = _value;
      }
      final Object value;
   }

   static class Ternary extends Expr {
      @Override
      <R> R accept(Expr.Visitor<R> visitor) {
         return visitor.visitTernary(this);
      }

      Ternary(Expr _conditional, Expr _trueBranch, Expr _falseBranch) {
         this.conditional = _conditional;
         this.trueBranch = _trueBranch;
         this.falseBranch = _falseBranch;
      }

      final Expr conditional;
      final Expr trueBranch;
      final Expr falseBranch;
   }
}
