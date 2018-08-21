package com.github.superkoh.mybatis.annotation;

public enum QueryExprType {

  EQUAL("="),
  GREATER_THAN(">"),
  GREATER_AND_EQUAL_THAN(">="),
  LESS_THAN("<"),
  LESS_AND_EQUAL_THAN("<=");

  private String expr;

  QueryExprType(String expr) {
    this.expr = expr;
  }

  public String getExpr() {
    return expr;
  }
}
