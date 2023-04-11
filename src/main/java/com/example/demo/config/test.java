package com.example.restservice;

import com.example.restservice.services.DBSerevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service()
public class DataBaseOperation<k> {


    @Autowired()
    private DBSerevice _DBSerevice;


    public List<k> getList(Class cls) throws Exception {

        String TableName = GetTableName(cls);
        List<FieldInformation> FieldsList = getFieldsInformationList(cls, false, false);
        ArrayList<k> reList = new ArrayList<k>();
        int FieldsListSize = (FieldsList != null) ? FieldsList.size() : 0;
        if (FieldsListSize == 0) {
            throw new Exception("Fields Information is Missing ");
        }
        String DBFieldList = FieldsList.get(0).DBFieldName;
        for (int i = 1; i < FieldsListSize; i++) {
            DBFieldList += "," + FieldsList.get(i).DBFieldName;
        }
        ResultSet rs = _DBSerevice.ExecuteSql("Select " + DBFieldList + " from " + TableName);

        while (rs.next()) {

            Object row = cls.newInstance();

            for (int i = 0; i < FieldsListSize; i++) {
                FieldInformation field = FieldsList.get(i);
                Object Value = null;
                if (field.FieldType == FieldInformation.FieldTypes.longType) {
                    Value = rs.getLong(field.DBFieldName);
                } else {
                    Value = rs.getString(field.DBFieldName);
                }

                Method m = this.getFieldSetterMethod(field, cls);
                m.invoke(row, new Object[]{Value});


            }


            reList.add((k) row);
        }


        this._DBSerevice.FreeConnection();


        return reList;
    }

    public void Update(k row) throws Exception {
        Class cls = row.getClass();
        String TableName = this.GetTableName(cls);
        String WhereCondation = getWhereFilter(row);
        StringBuffer FieldData = new StringBuffer("");

        List<FieldInformation> FieldsList = getFieldsInformationList(cls, false, true);
        int size = FieldsList.size();

        if (size == 0) {
            throw new Exception("No Fields to Update");
        }

        for (int i = 0; i < size; i++) {
            FieldInformation f = FieldsList.get(i);
            getFieldgetterMethod(f, cls);

            FieldData.append( ((i == 0) ? "" : ",") + f.DBFieldName + "=");
            if (f.FieldType == FieldInformation.FieldTypes.longType) {
                FieldData.append( f.getMethod.invoke(row, null) );
            } else {
                Object v = f.getMethod.invoke(row, null);
                FieldData.append( (v == null) ? "null" : "'" + v.toString().replaceAll("'", "''") + "'");
            }
        }


        String Sql = "Update " + TableName + " Set " + FieldData.toString();
        Sql += " Where " + WhereCondation;


        this._DBSerevice.ExecuteUpdateSql(Sql);

    }

    private String getWhereFilter(k row) throws Exception {
        Class cls = row.getClass();
        List<FieldInformation> FieldsList = getFieldsInformationList(cls, true, false);

        int NFields = FieldsList.size();
        if (NFields == 0) {
            throw new Exception("No Primary Found !");
        }
        String WhereCondation = null;
        for (int i = 0; i < NFields; i++) {
            FieldInformation f = FieldsList.get(i);
            WhereCondation = ((WhereCondation == null) ? "" : " And ") + f.FieldName + "=";
            getFieldgetterMethod(f, cls);

            if (f.FieldType == FieldInformation.FieldTypes.longType) {
                long Val = -1;

                Val = (long) f.getMethod.invoke(row, null);

                WhereCondation += Val;


            } else {

                String Val = "";

                Val = (String) f.getMethod.invoke(row, null);

                WhereCondation += "'" + Val.replaceAll("'", "''") + "'";

            }
        }


        return WhereCondation;
    }

    public void Delete(k row) throws Exception {
        Class cls = row.getClass();
        String TableName = this.GetTableName(cls);
        String Sql = "Delete From " + TableName + " Where " + getWhereFilter(row);
        this._DBSerevice.ExecuteUpdateSql(Sql);
    }

    private List<FieldInformation> getFieldsInformationList(Class cls
            , boolean ReturnPrimaryOnly, boolean ReturnNonPrimaryOnly) throws Exception {
        List<FieldInformation> re = new ArrayList<>();


        Field[] flist = cls.getDeclaredFields();

        if (flist != null) {
            for (int i = 0; i < flist.length; i++) {

                Field f = flist[i];

                Annotation[] AList = f.getAnnotations();
                for (int j = 0; j < AList.length; j++)
                    if (AList[j] instanceof com.example.restservice.Field) {
                        com.example.restservice.Field a = (com.example.restservice.Field) AList[j];
                        FieldInformation info = new FieldInformation();
                        info.FieldName = f.getName();
                        info.FieldType = a.FieldType();
                        info.DBFieldName = (a.DataBaseFieldName().equals("")) ? info.FieldName : a.DataBaseFieldName();
                        info.IsPrimary = a.isPrimaryKey();
                        if (ReturnPrimaryOnly) {
                            if (info.IsPrimary) {
                                re.add(info);
                            }
                        } else if (ReturnNonPrimaryOnly) {
                            if (info.IsPrimary == false) {
                                re.add(info);
                            }

                        } else {
                            re.add(info);
                        }
                        break;
                    }

            }

        }


        return re;
    }

    private String GetTableName(Class cls) throws Exception {
        Annotation[] list = cls.getAnnotations();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i] instanceof TableName) {
                    return ((TableName) list[i]).tablename();
                }
            }
        }
        throw new Exception("Table Name is Not Avilable please user @TableName");
    }

    private Method getFieldSetterMethod(FieldInformation field, Class cls) throws Exception {
        if (field.setMethod == null) {


            String FieldName = field.FieldName;
            Class[] Arg;
            switch (field.FieldType) {

                case FieldInformation.FieldTypes.longType:
                    Arg = new Class[]{Long.TYPE};
                    break;

                default:
                    Arg = new Class[]{String.class};

            }
            FieldName = FieldName.substring(0, 1).toUpperCase() + FieldName.substring(1);
            field.setMethod = cls.getMethod("set" + FieldName, Arg);
        }
        return field.setMethod;
    }


    private Method getFieldgetterMethod(FieldInformation field, Class cls) throws Exception {
        if (field.getMethod == null) {


            String FieldName = field.FieldName;
            Class[] Arg = null;

            FieldName = FieldName.substring(0, 1).toUpperCase() + FieldName.substring(1);
            field.getMethod = cls.getMethod("get" + FieldName, Arg);
        }
        return field.getMethod;
    }
}
