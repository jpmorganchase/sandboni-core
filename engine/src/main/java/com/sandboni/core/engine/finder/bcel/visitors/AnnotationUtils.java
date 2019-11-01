package com.sandboni.core.engine.finder.bcel.visitors;

import org.apache.bcel.classfile.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public class AnnotationUtils {

    private AnnotationUtils() {
    }

    public static AnnotationEntry getAnnotation(ConstantPool constantPool, Supplier<AnnotationEntry[]> annotationSupplier, String ... annotationName) {
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
                if (elementValue.getElementValueType() == 91 || elementValue.getElementValueType() == 99) {
                    // strings are stored as arrays of chars: [xxxx]
                    if(elementValue instanceof ArrayElementValue) {
                        result = "";
                        StringBuilder strBuilder = new StringBuilder();
                        ElementValue[] elementValuesArray = ((ArrayElementValue) elementValue).getElementValuesArray();
                        Arrays.stream(elementValuesArray).forEach(e -> strBuilder.append(formatValue(e)).append(","));
                        int length = strBuilder.length();
                        if(length > 0 && strBuilder.charAt(length-1) == ',') {
                            result = strBuilder.substring(0, length-1);
                        }
                    } else {
                        // strings are stored as arrays of chars: [xxxx]
                        result = formatValue(elementValue);
                    }
                } else {
                    result = elementValue.stringifyValue();
                }
            }
        }
        return result;
    }

    private static String formatValue(ElementValue e) {
        String result = e.stringifyValue();
        result = e.stringifyValue();
        // strings are stored as arrays of chars: [xxxx]
        return result.substring(1, result.length() - 1);
    }

    public static class SpringAnnotations {
        private SpringAnnotations() {}
        public static String[] getAvailableRequestMappingAnnotations() {
            return new String[]{com.sandboni.core.engine.finder.bcel.visitors.Annotations.SPRING.REQUEST_MAPPING.getDesc(), com.sandboni.core.engine.finder.bcel.visitors.Annotations.SPRING.GET_MAPPING.getDesc(),
                    com.sandboni.core.engine.finder.bcel.visitors.Annotations.SPRING.DELETE_MAPPING.getDesc(), com.sandboni.core.engine.finder.bcel.visitors.Annotations.SPRING.POST_MAPPING.getDesc(),
                    com.sandboni.core.engine.finder.bcel.visitors.Annotations.SPRING.PUT_MAPPING.getDesc(), Annotations.SPRING.PATCH_MAPPING.getDesc()};
        }
    }
}
