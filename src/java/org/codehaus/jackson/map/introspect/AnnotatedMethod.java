package org.codehaus.jackson.map.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.util.ClassUtil;

public final class AnnotatedMethod
    extends Annotated
{
    final Method _method;

    final AnnotationIntrospector _annotationIntrospector;

    final AnnotationMap _annotations = new AnnotationMap();

    // // Simple lazy-caching:

    public Class<?>[] _paramTypes;

    /*
    //////////////////////////////////////////////////////
    // Life-cycle
    //////////////////////////////////////////////////////
     */

    public AnnotatedMethod(Method method, AnnotationIntrospector intr)
    {
        _method = method;
        _annotationIntrospector = intr;
        // Also, let's find annotations we already have
        for (Annotation a : method.getDeclaredAnnotations()) {
            if (_annotationIntrospector.isHandled(a)) {
                _annotations.add(a);
            }
        }
    }

    /**
     * Method called to add annotations that have not yet been
     * added to this instance.
     */
    public void addAnnotationsNotPresent(Method method)
    {
        for (Annotation a : method.getDeclaredAnnotations()) {
            if (_annotationIntrospector.isHandled(a)) {
                _annotations.addIfNotPresent(a);
            }
        }
    }

    /*
    //////////////////////////////////////////////////////
    // Annotated impl
    //////////////////////////////////////////////////////
     */

    public Method getAnnotated() { return _method; }

    public int getModifiers() { return _method.getModifiers(); }

    public String getName() { return _method.getName(); }

    public <A extends Annotation> A getAnnotation(Class<A> acls)
    {
        return _annotations.get(acls);
    }

    /*
    //////////////////////////////////////////////////////
    // Extended API, generic
    //////////////////////////////////////////////////////
     */

    public Type[] getGenericParameterTypes() {
        return _method.getGenericParameterTypes();
    }

    public Class<?>[] getParameterTypes()
    {
        if (_paramTypes == null) {
            _paramTypes = _method.getParameterTypes();
        }
        return _paramTypes;
    }

    public int getParameterCount() {
        return getParameterTypes().length;
    }

    public Type getGenericReturnType() { return _method.getGenericReturnType(); }

    public Class<?> getReturnType() { return _method.getReturnType(); }

    public Class<?> getDeclaringClass() { return _method.getDeclaringClass(); }

    public String getFullName() {
        return getDeclaringClass().getName() + "#" + getName() + "("
            +getParameterCount()+" params)";
    }

    public int getAnnotationCount() { return _annotations.size(); }

    /**
     * Method that can be called to modify access rights, by calling
     * {@link java.lang.reflect.AccessibleObject#setAccessible} on
     * the underlying annotated element.
     */
    public void fixAccess()
    {
        ClassUtil.checkAndFixAccess(_method);
    }

    /*
    //////////////////////////////////////////////////////
    // Extended API, specific annotations
    //////////////////////////////////////////////////////
     */

   public boolean willWriteNullProperties(boolean defValue)
    {
        JsonWriteNullProperties ann = getAnnotation(JsonWriteNullProperties.class);
        return (ann == null) ? defValue : ann.value();
    }

    public String toString()
    {
        return "[method "+getName()+", annotations: "+_annotations+"]";
    }
}

