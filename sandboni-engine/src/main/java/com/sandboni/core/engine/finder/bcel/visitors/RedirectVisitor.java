package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.contract.HttpConsts;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.finder.bcel.visitors.http.HttpLinkHelper;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.LDC;

import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static com.sandboni.core.engine.finder.bcel.visitors.http.HttpLinkHelper.addHttpLinks;

public class RedirectVisitor extends MethodVisitorBase {
    private final ConstantPoolGen cp;
    private boolean controllerSide;

    public RedirectVisitor(Method m, JavaClass jc, Context c, boolean controllerSide) {
        super(m, jc, c);
        cp = new ConstantPoolGen(javaClass.getConstantPool());
        this.controllerSide = controllerSide;
    }

    public void start() {
        if (method.isAbstract() || method.isNative()) {
            return;
        }
        visitInstructions(method);
    }

    @Override
    public void visitLDC(LDC obj) {
        if (obj.getValue(cp) instanceof String){
            String url = (String)obj.getValue(cp);
            if (url.contains("/")){
                HttpLinkHelper.addHttpLinks(HttpConsts.GET_METHOD, context, url, javaClass.getClassName(), MethodUtils.formatMethod(method), controllerSide);
            }
        }
    }
}
