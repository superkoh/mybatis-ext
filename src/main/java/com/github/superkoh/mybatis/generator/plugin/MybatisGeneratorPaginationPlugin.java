package com.github.superkoh.mybatis.generator.plugin;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.PrimitiveTypeWrapper;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class MybatisGeneratorPaginationPlugin extends PluginAdapter {

  @Override
  public boolean validate(List<String> warnings) {
    return true;
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
    Field field = new Field();
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
}
