package com.github.superkoh.mybatis.generator.plugin;

import com.github.superkoh.mybatis.annotation.QueryExpr;
import com.github.superkoh.mybatis.annotation.QueryExprType;
import com.github.superkoh.mybatis.annotation.QueryIgnored;
import com.github.superkoh.mybatis.annotation.QueryIndex;
import com.github.superkoh.mybatis.common.Limitable;
import com.github.superkoh.mybatis.common.PageableList;
import com.google.common.base.CaseFormat;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.PrimitiveTypeWrapper;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class MybatisExtGeneratorPlugin extends PluginAdapter {

  @Override
  public boolean validate(List<String> warnings) {
    boolean checkQueryType = this.getContext().getTableConfigurations().parallelStream()
        .allMatch(c -> {
          if (c.getProperties().containsKey("queryType")) {
            try {
              Class clazz = Class.forName(c.getProperties().getProperty("queryType"));
              if (!Limitable.class.isAssignableFrom(clazz)) {
                return false;
              }
            } catch (ClassNotFoundException e) {
              return false;
            }
          }
          return true;
        });
    if (!checkQueryType) {
      throw new IllegalArgumentException("query type need implements Pageable or Limitable");
    }
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    if (introspectedTable.getTableConfiguration().getProperties().containsKey("queryType")) {
      FullyQualifiedJavaType queryType = new FullyQualifiedJavaType(
          introspectedTable.getTableConfiguration().getProperties().getProperty("queryType"));
      interfaze.addImportedType(queryType);

      FullyQualifiedJavaType pageableListType = new FullyQualifiedJavaType(
          PageableList.class.getCanonicalName());
      interfaze.addImportedType(pageableListType);

      Method selectPageByQuery = new Method();
      clientSelectPageByQueryMethodGenerated(selectPageByQuery, interfaze, introspectedTable,
          queryType);
      interfaze.addMethod(selectPageByQuery);

      Method countByQuery = new Method();
      clientCountByQueryMethodGenerated(countByQuery, interfaze, introspectedTable, queryType);
      interfaze.addMethod(countByQuery);

      Method selectPageableListByQuery = new Method();
      clientSelectPageableListByQueryMethodGenerated(selectPageableListByQuery, interfaze,
          introspectedTable, queryType);
      interfaze.addMethod(selectPageableListByQuery);
    }
    Method selectOneByExample = new Method();
    clientSelectOneByExampleMethodGenerated(selectOneByExample, interfaze, introspectedTable);
    interfaze.addMethod(selectOneByExample);

    return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
  }

  public boolean clientSelectPageByQueryMethodGenerated(Method method, Interface interfaze,
      IntrospectedTable introspectedTable, FullyQualifiedJavaType queryType) {
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName("selectPageByQuery");
    Parameter parameter = new Parameter(queryType, "query");
    parameter.addAnnotation("@Param(\"query\")");
    method.addParameter(parameter);
    method.setReturnType(new FullyQualifiedJavaType(
        "List<" + introspectedTable.getTableConfiguration().getDomainObjectName() + ">"));

    return true;
  }

  public boolean clientCountByQueryMethodGenerated(Method method, Interface interfaze,
      IntrospectedTable introspectedTable, FullyQualifiedJavaType queryType) {
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName("countByQuery");
    Parameter parameter = new Parameter(queryType, "query");
    parameter.addAnnotation("@Param(\"query\")");
    method.addParameter(parameter);
    method.setReturnType(new FullyQualifiedJavaType("long"));

    return true;
  }

  public boolean clientSelectPageableListByQueryMethodGenerated(Method method, Interface interfaze,
      IntrospectedTable introspectedTable, FullyQualifiedJavaType queryType) {
    FullyQualifiedJavaType collectionsType = new FullyQualifiedJavaType(
        Collections.class.getCanonicalName());
    interfaze.addImportedType(collectionsType);

    String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();

    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName("selectPageableListByQuery");
    method.setDefault(true);
    Parameter parameter = new Parameter(queryType, "query");
    method.addParameter(parameter);
    method.addBodyLine("long count = this.countByQuery(query);");
    method.addBodyLine("List<" + domainObjectName + "> list;");
    method.addBodyLine("if (count > 0) {");
    method.addBodyLine("  list = this.selectPageByQuery(query);");
    method.addBodyLine("} else {");
    method.addBodyLine("  list = Collections.emptyList();");
    method.addBodyLine("}");
    method.addBodyLine("return new PageableList<>(list, query, count);");
    method.setReturnType(new FullyQualifiedJavaType(
        "PageableList<" + domainObjectName + ">"));

    return true;
  }

  public boolean clientSelectOneByExampleMethodGenerated(Method method, Interface interfaze,
      IntrospectedTable introspectedTable) {
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName("selectOneByExample");
    FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getExampleType());
    Parameter parameter = new Parameter(type, "example");
    method.addParameter(parameter);
    if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
      method.setReturnType(new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType()));
    } else {
      method.setReturnType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
    }

    return true;
  }

  @Override
  public boolean sqlMapGenerated(GeneratedXmlFile sqlMap,
      IntrospectedTable introspectedTable) {
    String sqlMapPath = sqlMap.getTargetProject() + File.separator
        + sqlMap.getTargetPackage().replaceAll("\\.", File.separator)
        + File.separator + sqlMap.getFileName();
    File sqlMapFile = new File(sqlMapPath);

    sqlMapFile.delete();

    return true;
  }

  @Override
  public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

    XmlElement rootElement = document.getRootElement();
    if (introspectedTable.getTableConfiguration().getProperties().containsKey("queryType")) {
      String queryTypeName = introspectedTable.getTableConfiguration().getProperties()
          .getProperty("queryType");
      XmlElement queryWhereClause = new XmlElement("sql");
      sqlMapQueryWhereClauseElementGenerated(queryWhereClause, introspectedTable, queryTypeName);
      rootElement.addElement(queryWhereClause);

      XmlElement selectPageByQuery = new XmlElement("select");
      sqlMapSelectPageByQueryElementGenerated(selectPageByQuery, introspectedTable);
      rootElement.addElement(selectPageByQuery);

      XmlElement countByQuery = new XmlElement("select");
      sqlMapCountByQueryElementGenerated(countByQuery, introspectedTable);
      rootElement.addElement(countByQuery);
    }

    XmlElement selectOneByExample = new XmlElement("select");
    sqlMapSelectOneByExampleElementGenerated(selectOneByExample, introspectedTable);
    rootElement.addElement(selectOneByExample);

    return super.sqlMapDocumentGenerated(document, introspectedTable);
  }

  public boolean sqlMapQueryWhereClauseElementGenerated(XmlElement element,
      IntrospectedTable introspectedTable, String queryTypeName) {
    Class clazz;
    try {
      clazz = Class.forName(queryTypeName);
    } catch (ClassNotFoundException e) {
      return false;
    }
    element.addAttribute(new Attribute("id", "queryWhereClause"));

    Arrays.stream(clazz.getDeclaredFields())
        .filter(f -> !f.isAnnotationPresent(QueryIgnored.class))
        .sorted((f1, f2) -> {
          boolean f1IsIndex = f1.isAnnotationPresent(QueryIndex.class);
          boolean f2IsIndex = f2.isAnnotationPresent(QueryIndex.class);
          if (f1IsIndex && !f2IsIndex) {
            return -1;
          }
          if (!f1IsIndex && f2IsIndex) {
            return 1;
          }
          if (!f1IsIndex && !f2IsIndex) {
            return 0;
          }
          int f1Index = f1.getAnnotation(QueryIndex.class).order();
          int f2Index = f2.getAnnotation(QueryIndex.class).order();
          return f1Index < f2Index ? -1 : 1;
        })
        .forEach(field -> genQueryWhereClause(element, field));

    return true;
  }

  public boolean sqlMapSelectPageByQueryElementGenerated(XmlElement element,
      IntrospectedTable introspectedTable) {
    element.addAttribute(new Attribute("id", "selectPageByQuery"));
    element.addAttribute(new Attribute("resultType", introspectedTable.getBaseRecordType()));
    element.addElement(new TextElement(
        "SELECT * FROM `" + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "`"));
    XmlElement selectPageByQueryWhere = new XmlElement("where");
    XmlElement selectPageByQueryWhereInclude = new XmlElement("include");
    selectPageByQueryWhereInclude.addAttribute(new Attribute("refid", "queryWhereClause"));
    selectPageByQueryWhere.addElement(selectPageByQueryWhereInclude);
    element.addElement(selectPageByQueryWhere);
    XmlElement ifOrderBy = new XmlElement("if");
    ifOrderBy.addAttribute(new Attribute("test", "query.orderBy != null"));
    ifOrderBy.addElement(new TextElement("ORDER BY ${query.orderBy}"));
    element.addElement(ifOrderBy);
    XmlElement ifLimit = new XmlElement("if");
    ifLimit.addAttribute(new Attribute("test", "query.limit != null"));
    XmlElement ifOffsetNotNull = new XmlElement("if");
    ifOffsetNotNull.addAttribute(new Attribute("test", "query.offset != null"));
    ifOffsetNotNull.addElement(new TextElement("LIMIT ${query.offset}, ${query.limit}"));
    ifLimit.addElement(ifOffsetNotNull);
    XmlElement ifOffsetNull = new XmlElement("if");
    ifOffsetNull.addAttribute(new Attribute("test", "query.offset == null"));
    ifOffsetNull.addElement(new TextElement("LIMIT ${query.limit}"));
    ifLimit.addElement(ifOffsetNull);
    element.addElement(ifLimit);

    return true;
  }

  public boolean sqlMapCountByQueryElementGenerated(XmlElement element,
      IntrospectedTable introspectedTable) {
    element.addAttribute(new Attribute("id", "countByQuery"));
    element.addAttribute(new Attribute("resultType", "long"));
    element.addElement(new TextElement(
        "SELECT COUNT(*) FROM `" + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "`"));
    XmlElement countByQueryWhere = new XmlElement("where");
    XmlElement countByQueryWhereInclude = new XmlElement("include");
    countByQueryWhereInclude.addAttribute(new Attribute("refid", "queryWhereClause"));
    countByQueryWhere.addElement(countByQueryWhereInclude);
    element.addElement(countByQueryWhere);

    return true;
  }

  public boolean sqlMapSelectOneByExampleElementGenerated(XmlElement element,
      IntrospectedTable introspectedTable) {
    element.addAttribute(new Attribute("id", "selectOneByExample"));
    element.addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
    element.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));
    if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
      element.addAttribute(new Attribute("resultType", introspectedTable.getRecordWithBLOBsType()));
    } else {
      element.addAttribute(new Attribute("resultType", introspectedTable.getBaseRecordType()));
    }
    element.addElement(new TextElement(
        "SELECT * FROM `" + introspectedTable.getTableConfiguration().getTableName() + "`"));
    XmlElement ifTestParam = new XmlElement("if");
    ifTestParam.addAttribute(new Attribute("test", "_parameter != null"));
    XmlElement includeExample = new XmlElement("include");
    includeExample.addAttribute(new Attribute("refid", "Example_Where_Clause"));
    ifTestParam.addElement(includeExample);
    element.addElement(ifTestParam);
    element.addElement(new TextElement("LIMIT 1"));

    return true;
  }

  @Override
  public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
      IntrospectedTable introspectedTable) {
    XmlElement ifLimitNotNullElement = new XmlElement("if");
    ifLimitNotNullElement.addAttribute(new Attribute("test", "limit != null"));

    XmlElement ifOffsetNotNullElement = new XmlElement("if");
    ifOffsetNotNullElement.addAttribute(new Attribute("test", "offset != null"));
    ifOffsetNotNullElement.addElement(new TextElement("limit ${offset}, ${limit}"));
    ifLimitNotNullElement.addElement(ifOffsetNotNullElement);

    XmlElement ifOffsetNullElement = new XmlElement("if");
    ifOffsetNullElement.addAttribute(new Attribute("test", "offset == null"));
    ifOffsetNullElement.addElement(new TextElement("limit ${limit}"));
    ifLimitNotNullElement.addElement(ifOffsetNullElement);

    element.addElement(ifLimitNotNullElement);

    return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
  }

  @Override
  public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element,
      IntrospectedTable introspectedTable) {
    XmlElement ifLimitNotNullElement = new XmlElement("if");
    ifLimitNotNullElement.addAttribute(new Attribute("test", "limit != null"));

    XmlElement ifOffsetNotNullElement = new XmlElement("if");
    ifOffsetNotNullElement.addAttribute(new Attribute("test", "offset != null"));
    ifOffsetNotNullElement.addElement(new TextElement("limit ${offset}, ${limit}"));
    ifLimitNotNullElement.addElement(ifOffsetNotNullElement);

    XmlElement ifOffsetNullElement = new XmlElement("if");
    ifOffsetNullElement.addAttribute(new Attribute("test", "offset == null"));
    ifOffsetNullElement.addElement(new TextElement("limit ${limit}"));
    ifLimitNotNullElement.addElement(ifOffsetNullElement);

    element.addElement(ifLimitNotNullElement);

    return super.sqlMapSelectByExampleWithBLOBsElementGenerated(element, introspectedTable);
  }

  @Override
  public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    PrimitiveTypeWrapper integerWrapper = FullyQualifiedJavaType.getIntInstance()
        .getPrimitiveTypeWrapper();

    addField(topLevelClass, "limit", integerWrapper);
    addField(topLevelClass, "offset", integerWrapper);

    return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
  }

  private void addField(TopLevelClass topLevelClass, String fieldName,
      FullyQualifiedJavaType type) {
    org.mybatis.generator.api.dom.java.Field field = new org.mybatis.generator.api.dom.java.Field();
    field.setName(fieldName);
    field.setType(type);
    field.setVisibility(JavaVisibility.PRIVATE);
    topLevelClass.addField(field);

    Method setMethod = new Method();
    setMethod.setVisibility(JavaVisibility.PUBLIC);
    setMethod.setName("set" + StringUtils.capitalize(fieldName));
    setMethod.addParameter(new Parameter(type, fieldName));
    setMethod.addBodyLine("this." + fieldName + " = " + fieldName + ";");
    topLevelClass.addMethod(setMethod);

    Method getMethod = new Method();
    getMethod.setVisibility(JavaVisibility.PUBLIC);
    getMethod.setReturnType(type);
    getMethod.setName("get" + StringUtils.capitalize(fieldName));
    getMethod.addBodyLine("return " + fieldName + ";");
    topLevelClass.addMethod(getMethod);
  }

  private void genQueryWhereClause(XmlElement queryWhereClause, Field field) {
    String p = field.getName();
    String a = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
    XmlElement ifElement = new XmlElement("if");
    if (field.getType().equals(String.class)) {
      ifElement
          .addAttribute(
              new Attribute("test", "query." + p + " != null and query." + p + " != ''"));
      XmlElement bindElement = new XmlElement("bind");
      bindElement.addAttribute(new Attribute("name", p));
      bindElement.addAttribute(
          new Attribute("value", "'%' + query.get" + StringUtils.capitalize(p) + "() + '%'"));
      ifElement.addElement(bindElement);
      ifElement.addElement(new TextElement("AND `" + a + "` like #{" + p + "}"));
    } else {
      ifElement.addAttribute(new Attribute("test", "query." + p + " != null"));
      if (field.isAnnotationPresent(QueryExpr.class)) {
        QueryExpr exprAnnotation = field.getAnnotation(QueryExpr.class);
        QueryExprType exprType = exprAnnotation.type();
        String targetColumn = exprAnnotation.targetColumn();
        ifElement.addElement(new TextElement(
            "AND `" + targetColumn + "` " + exprType.getExpr() + " #{query." + p + "}"));
      } else {
        ifElement.addElement(new TextElement("AND `" + a + "` = #{query." + p + "}"));
      }
    }
    queryWhereClause.addElement(ifElement);
  }
}
