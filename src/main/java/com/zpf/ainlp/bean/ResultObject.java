package com.zpf.ainlp.bean;

import com.zpf.ainlp.exception.ErrorCode;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResultObject<T> extends Result {
    public T data = null;

    public ResultObject() {
        super();
    }

    public ResultObject(T data) {
        this();
        this.data = data;
    }


    public static ResultObject returnResultObject(int code) {
        String value = ErrorCode.getByCode(code).getMessage();
        return new ResultObject(code, value);
    }


    private Map<String, Object> enumToMap(Enum<?> en) {
        Map<String, Object> map = new HashMap();
        try {
            BeanInfo info = Introspector.getBeanInfo(en.getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor property : descriptors) {
                if (property.getPropertyType().getName().equals("java.lang.Class"))
                    continue;
                Object value = property.getReadMethod().invoke(en);
                map.put(property.getName(), value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return map;
    }


    public ResultObject(int code, Exception e) {
        super(code, e.toString());
    }


    public ResultObject(int code, String message) {
        super(code, message);
    }

    public ResultObject(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap();
        map.put("code", this.code);
        map.put("message", this.message);
        map.put("data", data);
        return map;
    }
}
