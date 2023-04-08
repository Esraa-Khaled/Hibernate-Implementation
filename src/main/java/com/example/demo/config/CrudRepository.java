package com.example.demo.config;


import com.example.demo.annotations.FieldAnnotation;
import com.example.demo.annotations.TableAnnotation;
import com.example.demo.utils.FieldInfo;
import com.example.demo.utils.FieldTypes;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CrudRepository<T, ID> implements crudOperations<T, ID> {


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.empty();
    }

    @Override
    public Iterable<T> findAll(Class T) {
        return null;
    }

    public Iterable<T> getList(Class cls) throws Exception {

        ArrayList<T> returnedList = new ArrayList<T>();

        String tableName = getTableName(cls);

        List<FieldInfo> fieldsList = getFieldsInfo(cls);
        int fieldsListSize = (fieldsList != null) ? fieldsList.size() : 0;
        if(fieldsListSize == 0) {
            throw new Exception("Fields Information is Missing");
        }
        List<String> dbFieldsList = fieldsList.stream().map( f -> f.dbFieldName).collect(Collectors.toList());
        String dbFieldsName = StringUtils.join(dbFieldsList, ',');
        String sql = "select " + dbFieldsName + " from " + tableName;

        Iterable<T> values = jdbcTemplate.query(sql, new ResultSetExtractor<Iterable<T>>() {
            @Override
            public Iterable<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
                while(rs.next()) {
                    try {
                        Object row = cls.newInstance();
                        for(int i=0; i<fieldsListSize; i ++) {
                            FieldInfo field = fieldsList.get(i);
                            Object val = getFieldValueBasedOnFieldType(rs, field);
                            Method m = getFieldsetterMethod(cls, field);
                            m.invoke(row, new Object[]{val});
                        }
                        returnedList.add((T) row);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
                return returnedList;
            }
        });

        return returnedList;
    }

    //username >> getUsername
    private Method getFieldGetterMethod(Class cls, FieldInfo field) throws NoSuchMethodException {
        if(field.getMethod == null) {
            String fieldName = field.fieldName;
            Class[] args = null;
            fieldName = fieldName.substring(0,1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
            field.getMethod = cls.getMethod("get" + fieldName, args);
        }
        return field.getMethod;
    }

    private Method getFieldsetterMethod(Class cls, FieldInfo field) throws NoSuchMethodException {
        if(field.setMethod == null) {
            String fieldName = field.fieldName;
            Class[] args = getFieldClassType(field.fieldType);
            fieldName = fieldName.substring(0,1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
            field.setMethod = cls.getMethod("set" + fieldName, args);
        }
        return field.setMethod;
    }

    private Class[] getFieldClassType(int fieldType) {
        switch (fieldType) {
            case FieldTypes.longType:
                return new Class[]{Long.TYPE};
            case FieldTypes.stringType:
                return new Class[]{String.class};
            default:
                return new Class[] {Object.class};
        }
    }

    private Object getFieldValueBasedOnFieldType(ResultSet rs, FieldInfo field) throws SQLException {
        int fieldType = field.fieldType;
        switch (fieldType) {
            case FieldTypes.longType:
                return rs.getLong(field.dbFieldName);
            case FieldTypes.stringType:
                return rs.getString(field.dbFieldName);
            default:
                return rs.getObject(field.dbFieldName);
        }
    }

    private List<FieldInfo> getFieldsInfo(Class cls) {
        List<FieldInfo> fields = new ArrayList<>();
        Field[] fList = cls.getDeclaredFields();
        Arrays.stream(fList).forEach(fl -> fields.add(getFieldInfo(fl)));
        return fields;
    }

    private FieldInfo getFieldInfo(Field fl) {
        Annotation[] aList = fl.getAnnotations();
        Optional<Annotation> fieldAnnotation = Arrays.stream(aList).filter(FieldAnnotation.class::isInstance).findFirst();
        FieldInfo info = new FieldInfo();
        if(fieldAnnotation.isPresent()) {
            FieldAnnotation annotationInfo = (FieldAnnotation) fieldAnnotation.get();
            info.fieldName = fl.getName();
            info.fieldType = annotationInfo.fieldType();
            info.isPrimary = annotationInfo.isPrimaryKey();
            info.dbFieldName = (annotationInfo.dbFieldName()).equals("")? info.fieldName: annotationInfo.dbFieldName();
        }
        return info;
    }

    private String getTableName(Class cls) {
        Annotation[] annotations = cls.getAnnotations();
        Optional<Annotation> tableAnnotation = Arrays.stream(annotations).filter(TableAnnotation.class::isInstance).findFirst();
        return tableAnnotation.isPresent()? ((TableAnnotation)tableAnnotation.get()).tableName(): cls.getName();
    }
}
