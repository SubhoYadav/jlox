package com.jlox;

import java.util.List;

import com.jlox.Expr.Assignment;
import com.jlox.Expr.Variable;
import com.jlox.Stmt.BlockStmt;
import com.jlox.Stmt.VarDecStmt;

// The interpreter is implemented as a Visitor according to the Visitor pattern
// Interpreter to evaluate syntax tree nodes of each type
// Its tree-walking the interpreter in post-order traversal scheme
public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

  private Environment environment = new Environment();

  public void interpret (List<Stmt> statements) {
      try {
        for (Stmt statement : statements) {
          execute(statement);
        }
      } 
      catch (RuntimeError error) {
        Jlox.runtimeError(error);
      }
  }

  

  @Override
  public Void visitExprStmt(Stmt.ExprStmt expressionStmt) {
    evaluate(expressionStmt.expression);
    return null;
  }
 
  @Override
  public Void visitPrintStmt(Stmt.PrintStmt printStmt) {
    Object value = evaluate(printStmt.expression);
    System.out.println(stringify(value));
    return null;
  }

  @Override
  public Void visitVarDecStmt(VarDecStmt varDecStatement) {
      Object initialiser = null;
      if (varDecStatement.initialiser != null) {
        initialiser = evaluate(varDecStatement.initialiser);
      }

      environment.define(varDecStatement.name, initialiser);
      return null;
  }

  @Override
  public Void visitBlockStmt(BlockStmt blockStatement) {
    // create a new environment by passing the current environment as the "enclosing" parameter of the Environment class, thereby creating an environment chain, if the block statements nest
    Environment newEnvironment = new Environment(this.environment);
    executeBlock(blockStatement.blockStatementList, newEnvironment);
    return null;
  }

  @Override
  public Object visitVariable(Variable expression) {
      return environment.get(expression.name);
  }

  @Override
  public Object visitAssignment(Assignment assignment) {
    // evaluate the expression in the assignment statement and put the value in the target which may be a variable
    Object value = evaluate(assignment.expression);
    environment.assign(assignment.name, value);
    return value;
  }
  @Override
  public Object visitBinary(Expr.Binary expression) {
      Object left = evaluate(expression.left);
      Object right = evaluate(expression.right);

      switch (expression.operator.tokenType) {
        case TokenType.MINUS:
            checkNumberOperand(expression.operator, left, right);
            return (double)left - (double)right;   
        case TokenType.STAR:
            checkNumberOperand(expression.operator, left, right);
            return (double)left * (double)right;
        case TokenType.SLASH:
            checkNumberOperand(expression.operator, left, right);
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

  @Override
  public Object visitTernary(Expr.Ternary expression) {
      Object conditionalResult = evaluate(expression.conditional);
      boolean booleanConditionalResult = (boolean)conditionalResult;

      if (booleanConditionalResult) {
        return evaluate(expression.trueBranch);
      }
      else {
        return evaluate(expression.falseBranch);
      }
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

  /**
   * Executes LOX statements
   */
  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  /**
   * This method directs the expression to its visitor method in order to evaluate it
   */
  private Object evaluate(Expr expression) {
    return expression.accept(this);
  }

  /**
   * This method executes a block of statements by creating new environments and restring the previous environment when the block ends
   */
  private void executeBlock (List<Stmt> blockStatemetList, Environment newEnvironment) {
    Environment oldEnvironment = this.environment;
    try {
      this.environment = newEnvironment;
      for (Stmt statement : blockStatemetList) {
        execute(statement);
      }
    }
    finally {
      this.environment = oldEnvironment;
    }
  }
}
