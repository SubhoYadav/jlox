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

   // Interpreter to evaluate syntax tree nodes of each type
   static class Interpreter implements Visitor<Object> {
      public void interpret (Expr expression) {
         try {
            Object result = evaluate(expression);
            System.out.println(stringify(result));
         } 
         catch (RuntimeError error) {
            Jlox.runtimeError(error);
         }
      } 

      private String stringify(Object result) {
         if (result == null) return "nil";
         else if (result instanceof Double) {
            String text = result.toString();
            if (text.endsWith(".0")) {
               return text.substring(0, text.length() - 2);
            }
            return text;
         }

         return result.toString();
      }

      @Override
      public Object visitBinary(Expr.Binary expression) {
         Object left = evaluate(expression.left);
         Object right = evaluate(expression.right);

         switch (expression.operator.tokenType) {
            case TokenType.MINUS:
               checkNumberOperand(expression.operator, expression.left, expression.right);
               return (double)left - (double)right;   
            case TokenType.STAR:
               checkNumberOperand(expression.operator, expression.left, expression.right);
               return (double)left * (double)right;
            case TokenType.SLASH:
               checkNumberOperand(expression.operator, expression.left, expression.right);
               return (double)left / (double)right;
            case TokenType.PLUS:
               if (left instanceof String && right instanceof String) {
                  return (String)left + (String)right;
               }
               if (left instanceof Double && right instanceof Double) {
                  return (double)left + (double)right;
               }
               throw new RuntimeError(expression.operator, "Operands must be two strings or two numbers");
            case TokenType.GREATER:
               return (double)left > (double)right;
            case TokenType.GREATER_EQUALS:
               return (double)left >= (double)right;
            case TokenType.LESSER:
               return (double)left < (double)right;
            case TokenType.LESSER_EQUALS:
               return (double)left <= (double)right;
            case TokenType.EQUALS_EQUALS:
               return isEqual(left, right);
            case TokenType.BANG_EQUALS:
               return !isEqual(left, right);
            default:
               break;
         }

         return null;
      }

      @Override
      public Object visitGrouping(Expr.Grouping expression) {
          return evaluate(expression.expression);
      }

      @Override
      public Object visitLiteral(Expr.Literal expression) {
         return expression.value;
      }

      @Override
      public Object visitUnary(Expr.Unary expression) {
         Object evaluatedExpression = evaluate(expression.expression);

         switch (expression.operator.tokenType) {
            case TokenType.MINUS:
               checkNumberOperand(expression.operator, evaluatedExpression);
               return -(double)evaluatedExpression;

            case TokenType.BANG:
               return !isTruthy(evaluatedExpression);
            default:
               break;
         }

         // unreachable code, but due to java's static type checking we need to return null from here
         return null;
      }

      /**
       * check number operand for unary operator
       */
      private void checkNumberOperand (Token operator, Object operand) {
         if (operand instanceof Double) return;
         throw new RuntimeError(operator, "Operand must be a number");
      }

      /**
       * overloaded method to check left & right operands must be a number for binary operators
       */
      private void checkNumberOperand (Token operator, Object left, Object right) {
         if (left instanceof Double && right instanceof Double) return;
         throw new RuntimeError(operator, "Operand must be a number");
      }

      private boolean isEqual (Object a, Object b) {
         if (a == null && b == null) return true;
         return a.equals(b);
      }

      /**
       * we are treating "nil" and false as falsy values other values which the expressions evaluates to are truthy
       */
      private boolean isTruthy(Object evaluatedExpression) {
         if (evaluatedExpression == null) return false;
         else if (evaluatedExpression instanceof Boolean) return (boolean) evaluatedExpression;
         
         return true;
      }

      /**
       * This method directs the expression to its visitor method in order to evaluate it
       */
      private Object evaluate(Expr expression) {
         return expression.accept(this);
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
}
