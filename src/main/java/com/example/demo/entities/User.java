package com.example.demo.entities;


import com.example.demo.annotations.FieldAnnotation;
import com.example.demo.annotations.TableAnnotation;
import com.example.demo.utils.FieldTypes;

@TableAnnotation(tableName = "customer")
public class User {

    @FieldAnnotation(dbFieldName = "recid", fieldType = FieldTypes.longType ,isPrimaryKey = true)
    private long recID;

    @FieldAnnotation(dbFieldName = "user_name")
    private String username;

    @FieldAnnotation(dbFieldName = "full_name")
    private String fullname;

    @FieldAnnotation(fieldType = FieldTypes.longType)
    private long manager_id;

    @FieldAnnotation()
    private String password;

    public long getRecID() {
        return recID;
    }

    public void setRecID(long recID) {
        this.recID = recID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public long getManager_id() {
        return manager_id;
    }

    public void setManager_id(long manager_id) {
        this.manager_id = manager_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
