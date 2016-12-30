package com.cus.clazz;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/30
 */
public class MainTest {

    private static Map<Class, Field[]> map = new HashMap<>();
    private static ConcurrentMap<Class, Field[]> map2 = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 15000; i++) {
            getClassFields(MainTest.class);
        }
        long end = System.currentTimeMillis();
        System.out.println("cache cast:"+(end - start)+" ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < 15000; i++) {
            getClassFields2(MainTest.class);
        }
        end = System.currentTimeMillis();
        System.out.println("cache2 cast:"+(end - start)+" ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < 15000; i++) {
            Add.class.getDeclaredFields();
        }
        end = System.currentTimeMillis();
        System.out.println("nocache cast:"+(end - start)+" ms");
    }

    public static Field[] getClassFields(Class clazz) {
        Field[] fields = map.get(clazz);
        if (fields == null) {
            fields = clazz.getDeclaredFields();
            map.put(clazz, fields);
        }
        return fields;
    }

    public static Field[] getClassFields2(Class clazz) {
        Field[] fields = map2.get(clazz);
        if (fields == null) {
            fields = clazz.getDeclaredFields();
            map2.put(clazz, fields);
        }
        return fields;
    }
}
