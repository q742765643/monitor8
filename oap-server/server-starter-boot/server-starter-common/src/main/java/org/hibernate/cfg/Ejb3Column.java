//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.hibernate.cfg;

import com.piesat.common.config.DatabseType;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.ColumnTransformers;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.naming.*;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.annotations.Nullability;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.*;
import org.jboss.logging.Logger;

import java.util.Map;

public class Ejb3Column {
    public static final int DEFAULT_COLUMN_LENGTH = 255;
    private static final CoreMessageLogger LOG = (CoreMessageLogger) Logger.getMessageLogger(CoreMessageLogger.class, Ejb3Column.class.getName());
    public String sqlType;
    protected Map<String, Join> joins;
    protected PropertyHolder propertyHolder;
    private MetadataBuildingContext context;
    private Column mappingColumn;
    private boolean insertable = true;
    private boolean updatable = true;
    private String explicitTableName;
    private boolean isImplicit;
    private int length = 255;
    private int precision;
    private int scale;
    private String logicalColumnName;
    private String propertyName;
    private boolean unique;
    private boolean nullable = true;
    private String formulaString;
    private Formula formula;
    private Table table;
    private String readExpression;
    private String writeExpression;
    private String defaultValue;

    public Ejb3Column() {
    }

    public static Ejb3Column[] buildColumnFromAnnotation(javax.persistence.Column[] anns, org.hibernate.annotations.Formula formulaAnn, Nullability nullability, PropertyHolder propertyHolder, PropertyData inferredData, Map<String, Join> secondaryTables, MetadataBuildingContext context) {
        return buildColumnFromAnnotation(anns, formulaAnn, nullability, propertyHolder, inferredData, (String) null, secondaryTables, context);
    }

    public static Ejb3Column[] buildColumnFromAnnotation(javax.persistence.Column[] anns, org.hibernate.annotations.Formula formulaAnn, Nullability nullability, PropertyHolder propertyHolder, PropertyData inferredData, String suffixForDefaultColumnName, Map<String, Join> secondaryTables, MetadataBuildingContext context) {
        Ejb3Column[] columns;
        if (formulaAnn != null) {
            Ejb3Column formulaColumn = new Ejb3Column();
            formulaColumn.setFormula(formulaAnn.value());
            formulaColumn.setImplicit(false);
            formulaColumn.setBuildingContext(context);
            formulaColumn.setPropertyHolder(propertyHolder);
            formulaColumn.bind();
            columns = new Ejb3Column[]{formulaColumn};
        } else {
            javax.persistence.Column[] actualCols = anns;
            javax.persistence.Column[] overriddenCols = propertyHolder.getOverriddenColumn(StringHelper.qualify(propertyHolder.getPath(), inferredData.getPropertyName()));
            if (overriddenCols != null) {
                if (anns != null && overriddenCols.length != anns.length) {
                    throw new AnnotationException("AttributeOverride.column() should override all columns for now");
                }

                actualCols = overriddenCols.length == 0 ? null : overriddenCols;
                LOG.debugf("Column(s) overridden for property %s", inferredData.getPropertyName());
            }

            if (actualCols == null) {
                columns = buildImplicitColumn(inferredData, suffixForDefaultColumnName, secondaryTables, propertyHolder, nullability, context);
            } else {
                int length = actualCols.length;
                columns = new Ejb3Column[length];

                for (int index = 0; index < length; ++index) {
                    ObjectNameNormalizer normalizer = context.getObjectNameNormalizer();
                    Database database = context.getMetadataCollector().getDatabase();
                    ImplicitNamingStrategy implicitNamingStrategy = context.getBuildingOptions().getImplicitNamingStrategy();
                    PhysicalNamingStrategy physicalNamingStrategy = context.getBuildingOptions().getPhysicalNamingStrategy();
                    javax.persistence.Column col = actualCols[index];
                    String sqlType;
                    if (col.columnDefinition().equals("")) {
                        sqlType = null;
                    } else {
                        String columnDefinition = col.columnDefinition();
                        if (columnDefinition.indexOf("TEXT") != -1 && "xugu".equals(DatabseType.type)) {
                            columnDefinition = columnDefinition.replace("TEXT", "CLOB");
                        }
                        sqlType = normalizer.applyGlobalQuoting(columnDefinition);
                    }

                    String tableName;
                    if (StringHelper.isEmpty(col.table())) {
                        tableName = "";
                    } else {
                        tableName = database.getJdbcEnvironment().getIdentifierHelper().toIdentifier(col.table()).render();
                    }

                    String columnName;
                    if ("".equals(col.name())) {
                        columnName = null;
                    } else {
                        columnName = database.getJdbcEnvironment().getIdentifierHelper().toIdentifier(col.name()).render();
                    }

                    Ejb3Column column = new Ejb3Column();
                    if (length == 1) {
                        applyColumnDefault(column, inferredData);
                    }

                    column.setImplicit(false);
                    column.setSqlType(sqlType);
                    column.setLength(col.length());
                    column.setPrecision(col.precision());
                    column.setScale(col.scale());
                    if (StringHelper.isEmpty(columnName) && !StringHelper.isEmpty(suffixForDefaultColumnName)) {
                        column.setLogicalColumnName(inferredData.getPropertyName() + suffixForDefaultColumnName);
                    } else {
                        column.setLogicalColumnName(columnName);
                    }

                    column.setPropertyName(BinderHelper.getRelativePath(propertyHolder, inferredData.getPropertyName()));
                    column.setNullable(col.nullable());
                    column.setUnique(col.unique());
                    column.setInsertable(col.insertable());
                    column.setUpdatable(col.updatable());
                    column.setExplicitTableName(tableName);
                    column.setPropertyHolder(propertyHolder);
                    column.setJoins(secondaryTables);
                    column.setBuildingContext(context);
                    column.extractDataFromPropertyData(inferredData);
                    column.bind();
                    columns[index] = column;
                }
            }
        }

        return columns;
    }

    private static void applyColumnDefault(Ejb3Column column, PropertyData inferredData) {
        XProperty xProperty = inferredData.getProperty();
        if (xProperty != null) {
            ColumnDefault columnDefaultAnn = (ColumnDefault) xProperty.getAnnotation(ColumnDefault.class);
            if (columnDefaultAnn != null) {
                column.setDefaultValue(columnDefaultAnn.value());
            }
        } else {
            LOG.trace("Could not perform @ColumnDefault lookup as 'PropertyData' did not give access to XProperty");
        }

    }

    private static Ejb3Column[] buildImplicitColumn(PropertyData inferredData, String suffixForDefaultColumnName, Map<String, Join> secondaryTables, PropertyHolder propertyHolder, Nullability nullability, MetadataBuildingContext context) {
        Ejb3Column column = new Ejb3Column();
        Ejb3Column[] columns = new Ejb3Column[]{column};
        if (nullability != Nullability.FORCED_NULL && inferredData.getClassOrElement().isPrimitive() && !inferredData.getProperty().isArray()) {
            column.setNullable(false);
        }

        column.setLength(255);
        String propertyName = inferredData.getPropertyName();
        column.setPropertyName(BinderHelper.getRelativePath(propertyHolder, propertyName));
        column.setPropertyHolder(propertyHolder);
        column.setJoins(secondaryTables);
        column.setBuildingContext(context);
        if (!StringHelper.isEmpty(suffixForDefaultColumnName)) {
            column.setLogicalColumnName(propertyName + suffixForDefaultColumnName);
            column.setImplicit(false);
        } else {
            column.setImplicit(true);
        }

        applyColumnDefault(column, inferredData);
        column.extractDataFromPropertyData(inferredData);
        column.bind();
        return columns;
    }

    public static void checkPropertyConsistency(Ejb3Column[] columns, String propertyName) {
        int nbrOfColumns = columns.length;
        if (nbrOfColumns > 1) {
            for (int currentIndex = 1; currentIndex < nbrOfColumns; ++currentIndex) {
                if (!columns[currentIndex].isFormula() && !columns[currentIndex - 1].isFormula()) {
                    if (columns[currentIndex].isInsertable() != columns[currentIndex - 1].isInsertable()) {
                        throw new AnnotationException("Mixing insertable and non insertable columns in a property is not allowed: " + propertyName);
                    }

                    if (columns[currentIndex].isNullable() != columns[currentIndex - 1].isNullable()) {
                        throw new AnnotationException("Mixing nullable and non nullable columns in a property is not allowed: " + propertyName);
                    }

                    if (columns[currentIndex].isUpdatable() != columns[currentIndex - 1].isUpdatable()) {
                        throw new AnnotationException("Mixing updatable and non updatable columns in a property is not allowed: " + propertyName);
                    }

                    if (!columns[currentIndex].getTable().equals(columns[currentIndex - 1].getTable())) {
                        throw new AnnotationException("Mixing different tables in a property is not allowed: " + propertyName);
                    }
                }
            }
        }

    }

    public String getLogicalColumnName() {
        return this.logicalColumnName;
    }

    public void setLogicalColumnName(String logicalColumnName) {
        this.logicalColumnName = logicalColumnName;
    }

    public String getSqlType() {
        return this.sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return this.scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isFormula() {
        return StringHelper.isNotEmpty(this.formulaString);
    }

    public void setFormula(String formula) {
        this.formulaString = formula;
    }

    public String getFormulaString() {
        return this.formulaString;
    }

    public String getExplicitTableName() {
        return this.explicitTableName;
    }

    public void setExplicitTableName(String explicitTableName) {
        if ("``".equals(explicitTableName)) {
            this.explicitTableName = "";
        } else {
            this.explicitTableName = explicitTableName;
        }

    }

    public boolean isImplicit() {
        return this.isImplicit;
    }

    public void setImplicit(boolean implicit) {
        this.isImplicit = implicit;
    }

    protected MetadataBuildingContext getBuildingContext() {
        return this.context;
    }

    public void setBuildingContext(MetadataBuildingContext context) {
        this.context = context;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isNullable() {
        return this.isFormula() ? true : this.mappingColumn.isNullable();
    }

    public void setNullable(boolean nullable) {
        if (this.mappingColumn != null) {
            this.mappingColumn.setNullable(nullable);
        } else {
            this.nullable = nullable;
        }

    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void bind() {
        if (StringHelper.isNotEmpty(this.formulaString)) {
            LOG.debugf("Binding formula %s", this.formulaString);
            this.formula = new Formula();
            this.formula.setFormula(this.formulaString);
        } else {
            this.initMappingColumn(this.logicalColumnName, this.propertyName, this.length, this.precision, this.scale, this.nullable, this.sqlType, this.unique, true);
            if (this.defaultValue != null) {
                this.mappingColumn.setDefaultValue(this.defaultValue);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debugf("Binding column: %s", this.toString());
            }
        }

    }

    protected void initMappingColumn(String columnName, String propertyName, int length, int precision, int scale, boolean nullable, String sqlType, boolean unique, boolean applyNamingStrategy) {
        if (StringHelper.isNotEmpty(this.formulaString)) {
            this.formula = new Formula();
            this.formula.setFormula(this.formulaString);
        } else {
            this.mappingColumn = new Column();
            this.redefineColumnName(columnName, propertyName, applyNamingStrategy);
            this.mappingColumn.setLength(length);
            if (precision > 0) {
                this.mappingColumn.setPrecision(precision);
                this.mappingColumn.setScale(scale);
            }

            this.mappingColumn.setNullable(nullable);
            this.mappingColumn.setSqlType(sqlType);
            this.mappingColumn.setUnique(unique);
            if (this.writeExpression != null && !this.writeExpression.matches("[^?]*\\?[^?]*")) {
                throw new AnnotationException("@WriteExpression must contain exactly one value placeholder ('?') character: property [" + propertyName + "] and column [" + this.logicalColumnName + "]");
            }

            if (this.readExpression != null) {
                this.mappingColumn.setCustomRead(this.readExpression);
            }

            if (this.writeExpression != null) {
                this.mappingColumn.setCustomWrite(this.writeExpression);
            }
        }

    }

    public boolean isNameDeferred() {
        return this.mappingColumn == null || StringHelper.isEmpty(this.mappingColumn.getName());
    }

    public void redefineColumnName(String columnName, String propertyName, boolean applyNamingStrategy) {
        ObjectNameNormalizer normalizer = this.context.getObjectNameNormalizer();
        Database database = this.context.getMetadataCollector().getDatabase();
        ImplicitNamingStrategy implicitNamingStrategy = this.context.getBuildingOptions().getImplicitNamingStrategy();
        PhysicalNamingStrategy physicalNamingStrategy = this.context.getBuildingOptions().getPhysicalNamingStrategy();
        if (applyNamingStrategy) {
            Identifier implicitName;
            if (StringHelper.isEmpty(columnName)) {
                if (propertyName != null) {
                    final AttributePath attributePath = AttributePath.parse(propertyName);
                    implicitName = normalizer.normalizeIdentifierQuoting(implicitNamingStrategy.determineBasicColumnName(new ImplicitBasicColumnNameSource() {
                        public AttributePath getAttributePath() {
                            return attributePath;
                        }

                        public boolean isCollectionElement() {
                            return !Ejb3Column.this.propertyHolder.isComponent() && !Ejb3Column.this.propertyHolder.isEntity();
                        }

                        public MetadataBuildingContext getBuildingContext() {
                            return Ejb3Column.this.context;
                        }
                    }));
                    if (implicitName.getText().contains("_collection&&element_")) {
                        implicitName = Identifier.toIdentifier(implicitName.getText().replace("_collection&&element_", "_"), implicitName.isQuoted());
                    }

                    Identifier physicalName = physicalNamingStrategy.toPhysicalColumnName(implicitName, database.getJdbcEnvironment());
                    this.mappingColumn.setName(physicalName.render(database.getDialect()));
                }
            } else {
                Identifier explicitName = database.toIdentifier(columnName);
                implicitName = physicalNamingStrategy.toPhysicalColumnName(explicitName, database.getJdbcEnvironment());
                this.mappingColumn.setName(implicitName.render(database.getDialect()));
            }
        } else if (StringHelper.isNotEmpty(columnName)) {
            this.mappingColumn.setName(normalizer.toDatabaseIdentifierText(columnName));
        }

    }

    public String getName() {
        return this.mappingColumn.getName();
    }

    public Column getMappingColumn() {
        return this.mappingColumn;
    }

    protected void setMappingColumn(Column mappingColumn) {
        this.mappingColumn = mappingColumn;
    }

    public boolean isInsertable() {
        return this.insertable;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public boolean isUpdatable() {
        return this.updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public void setJoins(Map<String, Join> joins) {
        this.joins = joins;
    }

    public PropertyHolder getPropertyHolder() {
        return this.propertyHolder;
    }

    public void setPropertyHolder(PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    public void linkWithValue(SimpleValue value) {
        if (this.formula != null) {
            value.addFormula(this.formula);
        } else {
            this.getMappingColumn().setValue(value);
            value.addColumn(this.getMappingColumn(), this.insertable, this.updatable);
            value.getTable().addColumn(this.getMappingColumn());
            this.addColumnBinding(value);
            this.table = value.getTable();
        }

    }

    protected void addColumnBinding(SimpleValue value) {
        String logicalColumnName;
        if (StringHelper.isNotEmpty(this.logicalColumnName)) {
            logicalColumnName = this.logicalColumnName;
        } else {
            ObjectNameNormalizer normalizer = this.context.getObjectNameNormalizer();
            Database database = this.context.getMetadataCollector().getDatabase();
            ImplicitNamingStrategy implicitNamingStrategy = this.context.getBuildingOptions().getImplicitNamingStrategy();
            Identifier implicitName = normalizer.normalizeIdentifierQuoting(implicitNamingStrategy.determineBasicColumnName(new ImplicitBasicColumnNameSource() {
                public AttributePath getAttributePath() {
                    return AttributePath.parse(Ejb3Column.this.propertyName);
                }

                public boolean isCollectionElement() {
                    return false;
                }

                public MetadataBuildingContext getBuildingContext() {
                    return Ejb3Column.this.context;
                }
            }));
            logicalColumnName = implicitName.render(database.getDialect());
        }

        this.context.getMetadataCollector().addColumnNameBinding(value.getTable(), logicalColumnName, this.getMappingColumn());
    }

    public Table getTable() {
        return this.table != null ? this.table : (this.isSecondary() ? this.getJoin().getTable() : this.propertyHolder.getTable());
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public boolean isSecondary() {
        if (this.propertyHolder == null) {
            throw new AssertionFailure("Should not call getTable() on column w/o persistent class defined");
        } else {
            return StringHelper.isNotEmpty(this.explicitTableName) && !this.propertyHolder.getTable().getName().equals(this.explicitTableName);
        }
    }

    public Join getJoin() {
        Join join = (Join) this.joins.get(this.explicitTableName);
        if (join == null) {
            String physicalTableName = this.getBuildingContext().getMetadataCollector().getPhysicalTableName(this.explicitTableName);
            if (physicalTableName != null) {
                join = (Join) this.joins.get(physicalTableName);
            }
        }

        if (join == null) {
            throw new AnnotationException("Cannot find the expected secondary table: no " + this.explicitTableName + " available for " + this.propertyHolder.getClassName());
        } else {
            return join;
        }
    }

    public void forceNotNull() {
        if (this.mappingColumn == null) {
            throw new CannotForceNonNullableException("Cannot perform #forceNotNull because internal org.hibernate.mapping.Column reference is null: likely a formula");
        } else {
            this.mappingColumn.setNullable(false);
        }
    }

    private void extractDataFromPropertyData(PropertyData inferredData) {
        if (inferredData != null) {
            XProperty property = inferredData.getProperty();
            if (property != null) {
                this.processExpression((ColumnTransformer) property.getAnnotation(ColumnTransformer.class));
                ColumnTransformers annotations = (ColumnTransformers) property.getAnnotation(ColumnTransformers.class);
                if (annotations != null) {
                    ColumnTransformer[] var4 = annotations.value();
                    int var5 = var4.length;

                    for (int var6 = 0; var6 < var5; ++var6) {
                        ColumnTransformer annotation = var4[var6];
                        this.processExpression(annotation);
                    }
                }
            }
        }

    }

    private void processExpression(ColumnTransformer annotation) {
        if (annotation != null) {
            String nonNullLogicalColumnName = this.logicalColumnName != null ? this.logicalColumnName : "";
            if (StringHelper.isEmpty(annotation.forColumn()) || annotation.forColumn().equals(nonNullLogicalColumnName)) {
                this.readExpression = annotation.read();
                if (StringHelper.isEmpty(this.readExpression)) {
                    this.readExpression = null;
                }

                this.writeExpression = annotation.write();
                if (StringHelper.isEmpty(this.writeExpression)) {
                    this.writeExpression = null;
                }
            }

        }
    }

    public void addIndex(Index index, boolean inSecondPass) {
        if (index != null) {
            String indexName = index.name();
            this.addIndex(indexName, inSecondPass);
        }
    }

    void addIndex(String indexName, boolean inSecondPass) {
        IndexOrUniqueKeySecondPass secondPass = new IndexOrUniqueKeySecondPass(indexName, this, this.context, false);
        if (inSecondPass) {
            secondPass.doSecondPass(this.context.getMetadataCollector().getEntityBindingMap());
        } else {
            this.context.getMetadataCollector().addSecondPass(secondPass);
        }

    }

    void addUniqueKey(String uniqueKeyName, boolean inSecondPass) {
        IndexOrUniqueKeySecondPass secondPass = new IndexOrUniqueKeySecondPass(uniqueKeyName, this, this.context, true);
        if (inSecondPass) {
            secondPass.doSecondPass(this.context.getMetadataCollector().getEntityBindingMap());
        } else {
            this.context.getMetadataCollector().addSecondPass(secondPass);
        }

    }

    public String toString() {
        return "Ejb3Column{table=" + this.getTable() + ", mappingColumn=" + this.mappingColumn.getName() + ", insertable=" + this.insertable + ", updatable=" + this.updatable + ", unique=" + this.unique + '}';
    }
}
