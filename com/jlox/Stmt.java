package com.jlox;

import java.util.List;

// The child classes needs to be static so that they can be instantiated without the reference of the outer class 'Stmt'

public abstract class Stmt {
  public interface Visitor<T> {
    T visitExprStmt(ExprStmt expressionStatement);
    T visitPrintStmt(PrintStmt printStmttatement);
    T visitVarDecStmt(VarDecStmt varDecStatement);
    T visitBlockStmt(BlockStmt  blockStatement);
  }

  abstract<R> R accept(Visitor<R> visitor);

  static class ExprStmt extends Stmt {
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExprStmt(this);
    }

    ExprStmt (Expr _expression) {
      this.expression = _expression;
    }

    final Expr expression;
  }

  static class PrintStmt extends Stmt {
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    PrintStmt(Expr _expression) {
      this.expression = _expression;
    }

    final Expr expression;
  }

  static class VarDecStmt extends Stmt {
    public <R> R accept (Visitor<R> visitor) {
      return visitor.visitVarDecStmt(this);
    }

    VarDecStmt(Token _name, Expr _initialiser) {
      this.name = _name;
      this.initialiser = _initialiser;
    }

    final Token name;
    final Expr initialiser;
  }

  static class BlockStmt extends Stmt {
    public <R> R accept (Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    BlockStmt(List<Stmt> _blockStatementList) {
      this.blockStatementList = _blockStatementList;
    }

    final List<Stmt> blockStatementList;
  }

}
