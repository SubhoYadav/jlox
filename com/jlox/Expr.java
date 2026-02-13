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
      T visitVariable(Variable expression);
      T visitAssignment(Assignment assignment);
   }

   // Below method is an abstract one, so all the sub classes that extends this base class, needs to define its body
   // and this will select the proper visitor method based on the 'Expr' object it is called from. 
   abstract<R> R accept(Visitor<R> visitor);

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

   static class Variable extends Expr {
      public <R> R accept(Visitor<R> visitor) {
         return visitor.visitVariable(this);
      }

      Variable(Token _name) {
         this.name = _name;
      }

      final Token name;
   }

   static class Assignment extends Expr {
      public <R> R accept(Visitor<R> visitor) {
         return visitor.visitAssignment(this);
      }

      Assignment(Token _name, Expr _expression) {
         this.name = _name;
         this.expression = _expression;
      }

      final Token name;
      final Expr expression;
   }
}
