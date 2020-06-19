package com.sandboni.core.engine.finder.bcel.visitors;

import org.apache.bcel.classfile.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AnnotationUtils {
    private static final String[] IGNORE_ANNOTATIONS = {Annotations.TEST.IGNORE.getDesc(), Annotations.TEST.DISABLED.getDesc()};
    private static final String[] BEFORE_ANNOTATIONS = {Annotations.TEST.BEFORE.getDesc(), Annotations.TEST.BEFORE_EACH.getDesc()};
    private static final String[] AFTER_ANNOTATIONS = {Annotations.TEST.AFTER.getDesc(), Annotations.TEST.AFTER_EACH.getDesc()};

    private AnnotationUtils() {
    }

    public static AnnotationEntry getAnnotation(ConstantPool constantPool, Supplier<AnnotationEntry[]> annotationSupplier, String... annotationName) {
        return Arrays.stream(annotationSupplier.get())
                .filter(e -> Arrays.stream(annotationName).anyMatch(getTypeSignature(constantPool, e.getTypeIndex())::contains))
                .findFirst().orElse(null);
    }

    public static String getTypeSignature(ConstantPool constantPool, int typeIndex) {
        ConstantUtf8 utf8 = (ConstantUtf8) constantPool.getConstant(typeIndex);
        return utf8.getBytes();
    }

    public static String getAnnotationParameter(ConstantPool constantPool, Supplier<AnnotationEntry[]> annotationSupplier, String annotationName, String... parameterNames) {
        AnnotationEntry annotation = getAnnotation(constantPool, annotationSupplier, annotationName);

        return annotation == null ? "" : getAnnotationParameter(annotation, parameterNames);
    }

    public static String getAnnotationParameter(AnnotationEntry annotationEntry, String... parameters) {
        String result = "";
        if (annotationEntry != null) {
            Optional<ElementValuePair> pathPair = Arrays.stream(annotationEntry.getElementValuePairs())
                    .filter(p -> Arrays.stream(parameters).anyMatch(param -> param.equals(p.getNameString())))
                    .findFirst();

            if (pathPair.isPresent()) {
                ElementValue elementValue = pathPair.get().getValue();
                if (elementValue.getElementValueType() == 91) { //ArrayElementValue
                    ElementValue[] elementValuesArray = ((ArrayElementValue) elementValue).getElementValuesArray();
                    result = Arrays.stream(elementValuesArray).map(AnnotationUtils::getValueByType).collect(Collectors.joining(","));
                } else if (elementValue.getElementValueType() == 99) { //ClassElementValue
                    result = trim(elementValue);
                } else {
                    result = elementValue.stringifyValue();
                }
            }
        }
        return result;
    }

    private static String getValueByType(ElementValue e){
        if(e.getElementValueType() == 99) return trim(e);
        else return e.stringifyValue();
    }
    private static String trim(ElementValue e) {
        String result = e.stringifyValue();
        // strings are stored as arrays of chars: [xxxx]
        return result.substring(1, result.length() - 1);
    }

    public static boolean isIgnore(JavaClass jc, Supplier<AnnotationEntry[]> getAnnotationEntries) {
        return Objects.nonNull(getAnnotation(jc.getConstantPool(), getAnnotationEntries, IGNORE_ANNOTATIONS));
    }

    public static boolean isBefore(JavaClass jc, Supplier<AnnotationEntry[]> getAnnotationEntries) {
        return Objects.nonNull(getAnnotation(jc.getConstantPool(), getAnnotationEntries, BEFORE_ANNOTATIONS));
    }

    public static boolean isAfter(JavaClass jc, Supplier<AnnotationEntry[]> getAnnotationEntries) {
        return Objects.nonNull(getAnnotation(jc.getConstantPool(), getAnnotationEntries, AFTER_ANNOTATIONS));
    }

    public static class SpringAnnotations {
        private SpringAnnotations() {
        }

        public static String[] getAvailableRequestMappingAnnotations() {
            return new String[]{com.sandboni.core.engine.finder.bcel.visitors.Annotations.SPRING.REQUEST_MAPPING.getDesc(), com.sandboni.core.engine.finder.bcel.visitors.Annotations.SPRING.GET_MAPPING.getDesc(),
                    com.sandboni.core.engine.finder.bcel.visitors.Annotations.SPRING.DELETE_MAPPING.getDesc(), com.sandboni.core.engine.finder.bcel.visitors.Annotations.SPRING.POST_MAPPING.getDesc(),
                    com.sandboni.core.engine.finder.bcel.visitors.Annotations.SPRING.PUT_MAPPING.getDesc(), Annotations.SPRING.PATCH_MAPPING.getDesc()};
        }
    }
}
