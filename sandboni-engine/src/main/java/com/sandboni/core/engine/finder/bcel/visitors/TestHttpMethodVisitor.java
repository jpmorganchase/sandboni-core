package com.sandboni.core.engine.finder.bcel.visitors;

import com.sandboni.core.engine.contract.HttpConsts;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;

import java.util.regex.Pattern;

import static com.sandboni.core.engine.finder.bcel.visitors.MethodUtils.formatMethod;
import static org.apache.bcel.Const.CONSTANT_String;

// duck-typing style heuristics to define test method as 'http client'
// 1. mentions http
// 2. link a method starting with http verb
// 4. references url-like constant string
// only one (first) link is detected
public class TestHttpMethodVisitor extends TestMethodVisitor {

    private static final Pattern HTTP_CLIENTS = Pattern.compile("(?i).*(http|mvc).*");
    private String httpReference = null;
    private String verbReference = null;
    private String verb = null;
    private String urlReference = null;

    private ConstantPoolGen cp;

    TestHttpMethodVisitor(Method m, JavaClass jc, Context c) {
        super(m, jc, c);
        cp = new ConstantPoolGen(jc.getConstantPool());
    }

    private static boolean guessVerbMatchByMethodName(String verb, String methodName) {
        return "POST".equals(verb) && methodName.contains("Upload") ||
                methodName.toUpperCase().startsWith(verb);
    }

    @Override
    public void start() {
        if (method.isAbstract() || method.isNative() || !testMethod) {
            return;
        }

        visitInstructions(method);

        if (httpReference != null && verbReference != null && urlReference != null) {
            context.addLink(LinkFactory.createInstance(
                    new Vertex.Builder(javaClass.getClassName(), MethodUtils.formatMethod(method)).build(),
                    new Vertex.Builder(verb + " " + HttpConsts.HTTP_LOCALHOST, urlReference, context.getCurrentLocation())
                            .markSpecial()
                            .build(),
                    LinkType.HTTP_REQUEST));
        }
    }

    @Override
    public void visitInvokeInstruction(InvokeInstruction invokeInstruction) {
        String referencedType = invokeInstruction.getReferenceType(cp).toString();
        if (httpReference == null && HTTP_CLIENTS.matcher(referencedType).matches()) {
            String referencedMethod = HttpConsts.getHttpVerb().stream()
                    .filter(v -> guessVerbMatchByMethodName(v, invokeInstruction.getMethodName(cp)))
                    .findFirst().orElse(null);
            if (referencedMethod != null) {
                httpReference = referencedType;
                verbReference = invokeInstruction.getMethodName(cp);
                verb = referencedMethod;
            }
        }
    }

    @Override
    public void visitCPInstruction(CPInstruction i) {
        Constant constant = cp.getConstant(i.getIndex());
        if (constant.getTag() == CONSTANT_String) {
            String path = ((ConstantString) constant).getBytes(cp.getConstantPool());
            if (httpReference == null && verbReference == null && HttpConsts.RELATIVE_URL.matcher(path).matches()) {
                urlReference = path;
            }
        }
    }
}