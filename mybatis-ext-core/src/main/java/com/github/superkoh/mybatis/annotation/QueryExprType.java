package com.github.superkoh.mybatis.annotation;

public enum QueryExprType {

  EQUAL("="),
  GREATER_THAN("&gt;"),
  GREATER_AND_EQUAL_THAN("&gt;="),
  LESS_THAN("&lt;"),
  LESS_AND_EQUAL_THAN("&lt;=");

  private String expr;

  QueryExprType(String expr) {
    this.expr = expr;
  }

  public String getExpr() {
    return expr;
  }
}
