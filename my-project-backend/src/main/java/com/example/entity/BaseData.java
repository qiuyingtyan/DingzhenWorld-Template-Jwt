package com.example.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

// 定义一个接口BaseData
public interface BaseData {
    // 将当前对象转换为指定类型的视图对象，并执行传入的Consumer
    default <V> V asViewObject(Class<V> clazz, Consumer<V> consumer) {
        // 将当前对象转换为指定类型的视图对象
        V v = this.asViewObject(clazz);
        // 执行传入的Consumer
        consumer.accept(v);
        return v;
    }

    // 将当前对象转换为指定类型的视图对象
    default <V> V asViewObject(Class<V> clazz) {
        try {
            // 获取指定类型的所有字段
            Field[] fields = clazz.getDeclaredFields();
            // 获取指定类型的无参构造函数
            Constructor<V> constructor = clazz.getConstructor();
            // 创建指定类型的对象
            V v = constructor.newInstance();
            // 遍历指定类型的所有字段
            Arrays.asList(fields).forEach(field -> convert(field, v));
            return v;
        } catch (ReflectiveOperationException exception) {
            // 获取日志记录器
            Logger logger = LoggerFactory.getLogger(BaseData.class);
            // 记录错误日志
            logger.error("在VO与DTO转换时出现了一些错误", exception);
            // 抛出运行时异常
            throw new RuntimeException(exception.getMessage());
        }
    }

    // 将当前对象的字段值转换为指定类型的字段值
    private void convert(Field field, Object target){
        try {
            // 获取当前对象的指定字段
            Field source = this.getClass().getDeclaredField(field.getName());
            // 设置字段可访问
            field.setAccessible(true);
            source.setAccessible(true);
            // 将当前对象的字段值赋给指定类型的字段
            field.set(target, source.get(this));
        } catch (IllegalAccessException | NoSuchFieldException ignored) {}
    }
}
