package Utils;


import Pojos.SpellEffect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtil {
    public static List<Method> findAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Method[] methods = clazz.getMethods();
        List<Method> annotatedMethods = new ArrayList<>(methods.length);
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotationClass)){
                annotatedMethods.add(method);
            }
        }
        return annotatedMethods;
    }

    public static Map<String, Method> findWebSocketTypeMethods(
            Class<?> clazz,
            Class<? extends Annotation> annotationClass) {
        List<Method> methods = findAnnotatedMethods(clazz, annotationClass);
        Map<String, Method> map = new ConcurrentHashMap<>();
        methods.forEach(method -> {
            HandleWebSocketType handler = method.getAnnotation(HandleWebSocketType.class);
            String type = handler.value();
            map.put(type, method);
        });
        return map;
    }

    // Could probably make a template for these?
    public static Map<SpellEffect, Method> findSpellEffectMethods(
            Class<?> clazz,
            Class<? extends Annotation> annotationClass) {
        List<Method> methods = findAnnotatedMethods(clazz, annotationClass);
        Map<SpellEffect, Method> map = new ConcurrentHashMap<>();
        methods.forEach(method -> {
            HandleSpellEffect handler = method.getAnnotation(HandleSpellEffect.class);
            SpellEffect effect = handler.value();
            map.put(effect, method);
        });
        return map;
    }
}
