package com.sandboni.core.engine.result;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FormattedChangeStats {

    private final String cname;
    private final Set<Method> methods;

    public FormattedChangeStats(String cname) {
        Objects.requireNonNull(cname);
        this.cname = cname;
        this.methods = new HashSet<>();
    }

    public String getCname() {
        return cname;
    }

    public void addMethod(String methodName, ChangeStats changeStats) {
        methods.add(new Method(methodName, changeStats));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        FormattedChangeStats stats = (FormattedChangeStats) obj;
        return this.cname.equals(stats.cname) &&
                this.methods.equals(stats.methods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cname, methods);
    }

    @SuppressWarnings("squid:S1068") // fields are only required to serialize this object
    private static class Method {
        private final String name;
        private long rt;
        private long ice;
        private double rtp;
        private double icep;

        private Method(String name, ChangeStats changeStats) {
            this.name = name;
            this.rt = changeStats.getRelatedTests();
            this.ice = changeStats.getImpactedCodeElements();
            this.rtp = changeStats.getRelatedTestsPercent();
            this.icep = changeStats.getImpactedCodeElementsPercent();
        }
    }
}
