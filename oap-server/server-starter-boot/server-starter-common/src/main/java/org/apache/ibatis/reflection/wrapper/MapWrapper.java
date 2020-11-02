//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.ibatis.reflection.wrapper;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapWrapper extends BaseWrapper {
    private final Map<String, Object> map;

    public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject);
        this.map = map;
    }

    public Object get(PropertyTokenizer prop) {
        if (prop.getIndex() != null) {
            Object collection = this.resolveCollection(prop, this.map);
            return this.getCollectionValue(prop, collection);
        } else {
            return this.map.get(prop.getName());
        }
    }

    public void set(PropertyTokenizer prop, Object value) {
        if (prop.getIndex() != null) {
            Object collection = this.resolveCollection(prop, this.map);
            this.setCollectionValue(prop, collection, value);
        } else {
            this.map.put(prop.getName(), value);
        }

    }

    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name == null ? "" : name.toUpperCase();
    }

    public String[] getGetterNames() {
        return (String[]) this.map.keySet().toArray(new String[this.map.keySet().size()]);
    }

    public String[] getSetterNames() {
        return (String[]) this.map.keySet().toArray(new String[this.map.keySet().size()]);
    }

    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = this.metaObject.metaObjectForProperty(prop.getIndexedName());
            return metaValue == SystemMetaObject.NULL_META_OBJECT ? Object.class : metaValue.getSetterType(prop.getChildren());
        } else {
            return this.map.get(name) != null ? this.map.get(name).getClass() : Object.class;
        }
    }

    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = this.metaObject.metaObjectForProperty(prop.getIndexedName());
            return metaValue == SystemMetaObject.NULL_META_OBJECT ? Object.class : metaValue.getGetterType(prop.getChildren());
        } else {
            return this.map.get(name) != null ? this.map.get(name).getClass() : Object.class;
        }
    }

    public boolean hasSetter(String name) {
        return true;
    }

    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (this.map.containsKey(prop.getIndexedName())) {
                MetaObject metaValue = this.metaObject.metaObjectForProperty(prop.getIndexedName());
                return metaValue == SystemMetaObject.NULL_META_OBJECT ? true : metaValue.hasGetter(prop.getChildren());
            } else {
                return false;
            }
        } else {
            return this.map.containsKey(prop.getName());
        }
    }

    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        HashMap<String, Object> map = new HashMap();
        this.set(prop, map);
        return MetaObject.forObject(map, this.metaObject.getObjectFactory(), this.metaObject.getObjectWrapperFactory(), this.metaObject.getReflectorFactory());
    }

    public boolean isCollection() {
        return false;
    }

    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    public <E> void addAll(List<E> element) {
        throw new UnsupportedOperationException();
    }
}
