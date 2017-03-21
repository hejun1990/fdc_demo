package com.hejun.demo.service.inter.plugin;

import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  @ProjectName: sns
 *  @Description: mybatis 代码自动生成插件
 */
public class SimpleMybatisPlugin extends PluginAdapter {

    public static void generate() {

        String config = SimpleMybatisPlugin.class.getClassLoader().getResource(
                "generatorConfig.xml").getFile();
        String[] arg = {"-configfile", config, "-overwrite"};
        ShellRunner.main(arg);

    }

    public static void main(String[] args) {
        generate();
    }


    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getAllColumns();

        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
        }

        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {
        /**
         * mybaties自动生成mapper的时候是追加操作,但是我们平常都是删除重新生成,
         * 所以需要扩展删除原来的mapper让mybaties再生成一个
         **/
        List<GeneratedXmlFile> generatedXmlFiles = introspectedTable.getGeneratedXmlFiles();
        for (GeneratedFile generatedFile : generatedXmlFiles) {
            generatedFile.getFormattedContent();

            File project = new File(generatedFile.getTargetProject() + generatedFile.getTargetPackage());
            File file = new File(project, generatedFile.getFileName());
            if (file.exists()) {
                file.delete();
            }
        }
        addDomainExampleClassFields(topLevelClass, introspectedTable);

        return super.modelExampleClassGenerated(topLevelClass,
                introspectedTable);
    }

    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element,
                                                         IntrospectedTable introspectedTable) {
        Attribute attribute1 = new Attribute("useGeneratedKeys", "true");
        Attribute attribute2 = new Attribute("keyProperty", "id");
        element.addAttribute(attribute1);
        element.addAttribute(attribute2);
        return true;
    }

    public boolean sqlMapInsertElementGenerated(XmlElement element,
                                                IntrospectedTable introspectedTable) {
        Attribute attribute1 = new Attribute("useGeneratedKeys", "true");
        Attribute attribute2 = new Attribute("keyProperty", "id");
        element.addAttribute(attribute1);
        element.addAttribute(attribute2);
        return true;
    }

    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method,
                                                                    TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return true;
    }

    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method,
                                                                    Interface interfaze, IntrospectedTable introspectedTable) {
        method.getParameters().get(0).addAnnotation("@Param(\"record\")");
        return super.clientUpdateByPrimaryKeySelectiveMethodGenerated(method,
                interfaze, introspectedTable);
    }

    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element,
                                                         IntrospectedTable introspectedTable) {
        generateSqlMapDelete(element, introspectedTable);
        return super.sqlMapDeleteByExampleElementGenerated(element, introspectedTable);
    }

    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        generateSqlMapDelete(element, introspectedTable);
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    private void generateSqlMapDelete(XmlElement element, IntrospectedTable introspectedTable) {
        element.getElements().remove(0);
        element.getElements().add(0, new TextElement("delete from " + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName()));

    }

    /**
     * 在ExampleClass中追加属性
     *
     * @param topLevelClass
     */
    private void addDomainExampleClassFields(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        /**
         * 增加forUpdate属性
         **/
        Field fieldNew = new Field();
        fieldNew.setName("forUpdate");
        fieldNew.setVisibility(JavaVisibility.PROTECTED);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType("boolean");
        fieldNew.setType(type);
        fieldNew.addJavaDocLine("/** 在查询中增加for update ");
        fieldNew.addJavaDocLine(" * 目前用来支持对查询记录加锁");
        fieldNew.addJavaDocLine(" **/");
        topLevelClass.addField(fieldNew);

        /**
         * 增加setForUpdate方法
         **/
        Method setMethod = new Method();
        setMethod.setName("setForUpdate");
        setMethod.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("void");
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("boolean");
        Parameter parameter = new Parameter(parameterType, "forUpdate");
        setMethod.addParameter(parameter);
        setMethod.addBodyLine("this.forUpdate = forUpdate;");
        setMethod.setReturnType(returnType);


        /**
         * 增加isForUpdate方法
         **/
        Method isMethod = new Method();
        isMethod.setName("isForUpdate");
        isMethod.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType isMethodReturnType = new FullyQualifiedJavaType("boolean");
        isMethod.setReturnType(isMethodReturnType);
        isMethod.addBodyLine("return forUpdate;");


        Field definedQueryColumns = new Field();
        definedQueryColumns.setName("definedQueryColumns");
        definedQueryColumns.setVisibility(JavaVisibility.PROTECTED);
        FullyQualifiedJavaType definedQueryColumnsType = new FullyQualifiedJavaType("boolean");
        definedQueryColumns.setType(definedQueryColumnsType);
        definedQueryColumns.addJavaDocLine("/** 是否自定义设置查询字段 */");
        topLevelClass.addField(definedQueryColumns);

        Method setDefinedQueryColumnsMethod = new Method();
        setDefinedQueryColumnsMethod.setName("setDefinedQueryColumns");
        setDefinedQueryColumnsMethod.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType setDefinedQueryColumnsReturnType = new FullyQualifiedJavaType("void");
        FullyQualifiedJavaType setDefinedQueryColumnsParameterType = new FullyQualifiedJavaType("boolean");
        Parameter setDefinedQueryColumnsParameter = new Parameter(setDefinedQueryColumnsParameterType, "definedQueryColumns");
        setDefinedQueryColumnsMethod.addParameter(setDefinedQueryColumnsParameter);
        setDefinedQueryColumnsMethod.addBodyLine("this.definedQueryColumns = definedQueryColumns;");
        setDefinedQueryColumnsMethod.setReturnType(setDefinedQueryColumnsReturnType);

        Method isDefinedQueryColumnsMethod = new Method();
        isDefinedQueryColumnsMethod.setName("isDefinedQueryColumns");
        isDefinedQueryColumnsMethod.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType isDefinedQueryColumnsMethodReturnType = new FullyQualifiedJavaType("boolean");
        isDefinedQueryColumnsMethod.setReturnType(isDefinedQueryColumnsMethodReturnType);
        isDefinedQueryColumnsMethod.addBodyLine("return definedQueryColumns;");

        Field queryColumns = new Field();
        queryColumns.setName("queryColumns");
        queryColumns.setVisibility(JavaVisibility.PROTECTED);
        FullyQualifiedJavaType queryColumnsType = new FullyQualifiedJavaType("List<String>");
        queryColumns.setType(queryColumnsType);
        queryColumns.addJavaDocLine("/** 设置查询字段 */");
        topLevelClass.addField(queryColumns);
        topLevelClass.addMethod(setMethod);
        topLevelClass.addMethod(isMethod);

        topLevelClass.addMethod(setDefinedQueryColumnsMethod);
        topLevelClass.addMethod(isDefinedQueryColumnsMethod);

        List<IntrospectedColumn> baseColumns = introspectedTable.getAllColumns();

        if (!CollectionUtils.isEmpty(baseColumns)) {
            for (IntrospectedColumn baseColumn : baseColumns) {

            }
        }

        List<Method> methods = topLevelClass.getMethods();
        if (CollectionUtils.isEmpty(methods)) {
            return;
        }

        for (Method method : methods) {
            if ("clear".equals(method.getName())) {
                method.addBodyLine("forUpdate = false;");
                method.addBodyLine("definedQueryColumns = false;");
                method.addBodyLine("queryColumns.clear();");
            }

            if ((introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "Example").equals(method.getName())) {
                method.addBodyLine("queryColumns = new ArrayList<String>();");
            }
        }
    }

    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        if (element != null) {
            List<Element> elements = element.getElements();
            if (!CollectionUtils.isEmpty(elements)) {
                XmlElement xmlElement = ((XmlElement) ((XmlElement) ((XmlElement) elements.get(0)).getElements().get(0)).getElements().get(0));
                List<Element> forEchElements = ((XmlElement) xmlElement.getElements().get(0)).getElements();
                xmlElement.getElements().remove(0);
                List<Element> childElements = xmlElement.getElements();
                for (Element elementTmp : forEchElements) {
                    childElements.add(elementTmp);
                }
                TextElement textElement = new TextElement("");
                XmlElement whereChildElement = new XmlElement("trim");
                Attribute trimAttribute1 = new Attribute("prefix", "(");
                Attribute trimAttribute2 = new Attribute("suffix", ")");
                Attribute trimAttribute3 = new Attribute("prefixOverrides", "and");
                whereChildElement.addAttribute(trimAttribute1);
                whereChildElement.addAttribute(trimAttribute2);
                whereChildElement.addAttribute(trimAttribute3);
                List<Element> xmlElementList = ((XmlElement) elements.get(0)).getElements();
                for (Element elementTmp : xmlElementList) {
                    whereChildElement.addElement(elementTmp);
                }
                whereChildElement.addElement(0, textElement);
                ((XmlElement) elements.get(0)).getElements().remove(0);
                ((XmlElement) elements.get(0)).getElements().add(whereChildElement);
            }
        }
        return super.sqlMapExampleWhereClauseElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType(
                "org.springframework.stereotype.Repository"));
        interfaze.addAnnotation("@Repository");
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        generateSetExpression(element, introspectedTable);
        TextElement textElement = (TextElement) element.getElements().get(element.getElements().size() - 1);
        element.getElements().remove(element.getElements().size() - 1);

        element.getElements().add(new TextElement(textElement.getContent().replace("#{", "#{record.")));
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement xmlElement = (XmlElement) element.getElements().get(2);
        element.getElements().remove(2);
        XmlElement ifNotDefinedQueryColumnsXmlElement = new XmlElement("if");
        Attribute attribute = new Attribute("test", "!definedQueryColumns");
        ifNotDefinedQueryColumnsXmlElement.addAttribute(attribute);
        ifNotDefinedQueryColumnsXmlElement.addElement(xmlElement);

        XmlElement ifDefinedQueryColumns = new XmlElement("if");
        Attribute attributeDefinedQueryColumns = new Attribute("test", "definedQueryColumns");
        ifDefinedQueryColumns.addAttribute(attributeDefinedQueryColumns);

        XmlElement forEachQueryColumns = new XmlElement("foreach");
        Attribute collection = new Attribute("collection", "queryColumns");
        Attribute item = new Attribute("item", "queryColumn");
        Attribute separator = new Attribute("separator", ",");
        forEachQueryColumns.addAttribute(collection);
        forEachQueryColumns.addAttribute(item);
        forEachQueryColumns.addAttribute(separator);
        forEachQueryColumns.addElement(new TextElement("${queryColumn}"));
        ifDefinedQueryColumns.addElement(forEachQueryColumns);

        element.getElements().add(2, ifNotDefinedQueryColumnsXmlElement);
        element.getElements().add(3, ifDefinedQueryColumns);

        generateForUpdateSqlMapSelectByExample(element);

        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {

        generateForUpdateSqlMapSelectByExample(element);

        return super.sqlMapSelectByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {

        generateSetExpression(element, introspectedTable);

        return super.sqlMapUpdateByExampleSelectiveElementGenerated(element, introspectedTable);
    }

    /**
     * mapper.xml 增加Set表达式场景,支持如update set 字段=表达式(如:'字段+1')这种场景
     *
     * @param element
     * @param introspectedTable
     */
    private void generateSetExpression(XmlElement element, IntrospectedTable introspectedTable) {
        element.getElements().remove(1);
        List<IntrospectedColumn> baseColumns = introspectedTable.getBaseColumns();

        if (CollectionUtils.isEmpty(baseColumns)) {
            return;
        }

        XmlElement xmlElement = new XmlElement("set");

        XmlElement setChildElement = new XmlElement("trim");
        Attribute trimAttribute = new Attribute("suffixOverrides", ",");
        setChildElement.addAttribute(trimAttribute);
        for (IntrospectedColumn baseColumn : baseColumns) {
            XmlElement baseXmlElement = new XmlElement("if");
            Attribute attribute = new Attribute("test", "record." + baseColumn.getJavaProperty()
                    + "!=null and record." + baseColumn.getJavaProperty() + "Expression==null");
            baseXmlElement.addAttribute(attribute);
            baseXmlElement.addElement(new TextElement(baseColumn.getActualColumnName() + " = #{record."
                    + baseColumn.getJavaProperty() + ",jdbcType=" + baseColumn.getJdbcTypeName() + "},"));

            XmlElement expressionXmlElement = new XmlElement("if");
            Attribute expressionAttribute = new Attribute("test", "record." + baseColumn.getJavaProperty()
                    + "Expression!=null");
            expressionXmlElement.addAttribute(expressionAttribute);
            expressionXmlElement.addElement(new TextElement(baseColumn.getActualColumnName() + " = ${record."
                    + baseColumn.getJavaProperty() + "Expression},"));

            setChildElement.addElement(baseXmlElement);
            setChildElement.addElement(expressionXmlElement);
        }
        xmlElement.addElement(setChildElement);
        element.addElement(1, xmlElement);
    }

    private void generateForUpdateSqlMapSelectByExample(XmlElement element) {
        XmlElement baseXmlElement = new XmlElement("if");
        Attribute attribute = new Attribute("test", "forUpdate");
        baseXmlElement.addAttribute(attribute);
        baseXmlElement.addElement(new TextElement("for update"));
        element.getElements().add(baseXmlElement);

    }

    private void addDomainClassFields(TopLevelClass topLevelClass) {

        topLevelClass.getMethods().clear();
        List<Field> fields = topLevelClass.getFields();
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        List<Field> fieldList = new ArrayList<Field>();

        for (Field field : fields) {
            Field fieldNew = new Field();
            fieldNew.setName(field.getName() + "Expression");
            fieldNew.setVisibility(field.getVisibility());
            FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.lang.String");
            fieldNew.setType(type);
            fieldNew.addJavaDocLine("/** " + field.getName() + "属性对应的表达式属性，");
            fieldNew.addJavaDocLine(" * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景");
            fieldNew.addJavaDocLine(" **/");
            fieldList.add(fieldNew);
        }

        for (Field field : fieldList) {
            //给某些属性添加 @JsonIgnore
            String fieldName = field.getName();
            if (StringUtils.isNotBlank(fieldName)) {
                if (fieldName.indexOf("Expression") >= 0 || fieldName.startsWith("max") || fieldName.startsWith("min") ||
                        fieldName.startsWith("avg") || fieldName.startsWith("count") || fieldName.startsWith("sum") ||
                        fieldName.startsWith("sum")) {
                    field.addAnnotation("@JsonIgnore");
                    topLevelClass.addImportedType(new FullyQualifiedJavaType("com.fasterxml.jackson.annotation.JsonIgnore"));
                }
            }
            topLevelClass.addField(field);
        }
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document,
                                           IntrospectedTable introspectedTable) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        addDomainClassFields(topLevelClass);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        addDomainClassFields(topLevelClass);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        addDomainClassFields(topLevelClass);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    private void generateToString(IntrospectedTable introspectedTable, TopLevelClass topLevelClass) {

        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.fdc.platform.common.validator.annotation.Domain"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.ToString"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.Data"));
        topLevelClass.addAnnotation("@Domain");
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@ToString");
        List<Field> fields = topLevelClass.getFields();
        Map<String, Field> map = new HashMap<String, Field>();
        for (Field field : fields) {
            map.put(field.getName(), field);
        }
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        for (IntrospectedColumn column : columns) {
            Field f = map.get(column.getJavaProperty());
            if (f != null) {
                f.getJavaDocLines().clear();
                if (column.getRemarks() != null) {
                    f.addJavaDocLine("/** " + column.getRemarks() + " */");
                    if (column.getRemarks().indexOf("@BizId") != -1 || column.getRemarks().indexOf("BizId") != -1
                            || column.getRemarks().indexOf("bizid") != -1 || column.getRemarks().indexOf("BIZID") != -1) {
                        f.addAnnotation("@BizId");
                        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.fdc.platform.common.validator.annotation.BizId"));
                    }

                }
                //解决tinyint 对应成 Boolean的问题
                if (column.getJdbcTypeName().equals("BIT")) {
                    f.setType(FullyQualifiedJavaType.getIntInstance());
                }
            }
        }
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

}
