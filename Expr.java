public abstract class Expr {
   public interface Visitor<T> {
      T visitBinary(Binary expression);
      T visitUnary(Unary expression);
      T visitGrouping(Grouping expression);
      T visitLiteral(Literal expression);
   }

   abstract<R> R accept(Visitor<R> visitor);

   // Polish(prefix) Notation printer visitor
   static class PNPrinter implements Visitor<String> {
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
         this.value=_value;
      }
      final Object value;
   }
}
