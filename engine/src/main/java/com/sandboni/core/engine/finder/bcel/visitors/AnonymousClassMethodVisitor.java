package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.EnclosingMethod;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.util.SyntheticRepository;

import java.util.Arrays;
import java.util.Optional;

import static com.sandboni.core.engine.common.StreamHelper.ofType;
import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;

public class AnonymousClassMethodVisitor extends CallerFieldOrMethodVisitor {
    AnonymousClassMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {

        String referencedClass = i.getReferenceType(cp).toString();
        if (!this.javaClass.getClassName().equals(referencedClass) && referencedClass.indexOf('$') >= 0) {
            JavaClass nestedClass;
            try {
                SyntheticRepository repository;
                synchronized (SyntheticRepository.class) {
                    repository = SyntheticRepository.getInstance(ClassUtils.getClassPathObject(context.getClassPath()));
                }
                nestedClass = repository.loadClass(referencedClass);
            } catch (ClassNotFoundException e) {
                return;
            }

            Optional<EnclosingMethod> enclosingMethod = Arrays.stream(nestedClass.getAttributes()).flatMap(ofType(EnclosingMethod.class)).filter(em -> em.getEnclosingMethod() != null).findFirst();
            if (enclosingMethod.isPresent()) {
                String enclosingMethodName = enclosingMethod.get().getEnclosingMethod().getName(nestedClass.getConstantPool());
                String enclosingClassName = ((ConstantUtf8) nestedClass.getConstantPool().getConstant(enclosingMethod.get().getEnclosingClass().getNameIndex())).getBytes().replace('/', '.');

                //TODO: add signature to comparision?
                if (this.javaClass.getClassName().equals(enclosingClassName) && this.method.getName().equals(enclosingMethodName)) {
                    for (Method method : nestedClass.getMethods()) {
                        addLink(LinkFactory.createInstance(
                                context.getApplicationId(), currentMethodVertex,
                                new Vertex.Builder(nestedClass.getClassName(), formatMethod(method)).build(),
                                LinkType.METHOD_CALL));
                    }
                }
            }
        }
    }
}