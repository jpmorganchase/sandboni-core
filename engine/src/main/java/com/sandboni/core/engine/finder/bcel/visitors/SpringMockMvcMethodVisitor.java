package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.contract.HttpConsts;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.LDC;

import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;

public class SpringMockMvcMethodVisitor extends TestMethodVisitor {
    private final ConstantPoolGen cp;
    private String value;

    private static final String MOCK_HTTP_REQUEST_BUILDER = "MockHttpServletRequestBuilder";

    SpringMockMvcMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
        cp = new ConstantPoolGen(javaClass.getConstantPool());
    }

    @Override
    public void start() {
        if (method.isAbstract() || method.isNative() || !testMethod) {
            return;
        }
        visitInstructions(method);
    }

    @Override
    public void visitLDC(LDC obj) {
        if (obj.getValue(cp) instanceof String){
           String tmpValue = (String)obj.getValue(cp);
           if (tmpValue.contains("/")){
               this.value = tmpValue;
           }
        }
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC obj) {
        if (obj.getType(cp).getSignature().contains(MOCK_HTTP_REQUEST_BUILDER)){
            context.addLink(LinkFactory.createInstance(
                    context.getApplicationId(),
                    new Vertex.Builder(javaClass.getClassName(), formatMethod(method)).build(),
                    new Vertex.Builder(obj.getMethodName(cp).toUpperCase() + " " + HttpConsts.HTTP_LOCALHOST, value, context.getCurrentLocation())
                            .markSpecial()
                            .build(),
                    LinkType.HTTP_REQUEST));
        }
    }


}
