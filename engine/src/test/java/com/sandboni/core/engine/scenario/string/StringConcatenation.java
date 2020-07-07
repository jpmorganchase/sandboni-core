package com.sandboni.core.engine.scenario.string;

public class StringConcatenation {

    // Until Java 8, the above code generates this bytecode:

    // L2
    //    LINENUMBER 8 L2
    //    NEW java/lang/StringBuilder
    //    DUP
    //    INVOKESPECIAL java/lang/StringBuilder.<init> ()V
    //    ALOAD 1
    //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //    ALOAD 2
    //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //    INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
    //    ASTORE 3

    // and Sandboni creates 3 links

    // "Special call com.sandboni.core.engine.scenario.string.StringConcatenation/basicStringConcatenation() -> java.lang.StringBuilder/<init>()"
    // "Method call com.sandboni.core.engine.scenario.string.StringConcatenation/basicStringConcatenation() -> java.lang.StringBuilder/append(java.lang.String)"
    // "Method call com.sandboni.core.engine.scenario.string.StringConcatenation/basicStringConcatenation() -> java.lang.StringBuilder/toString()"

    // With Java 11 String concatenation changes, the bytecode generated is:

    // L2
    // LINENUMBER 9 L2
    // ALOAD 1
    // ALOAD 2
    // INVOKEDYNAMIC makeConcatWithConstants(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String; [
    // // handle kind 0x6 : INVOKESTATIC
    // java/lang/invoke/StringConcatFactory.makeConcatWithConstants(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;
    // // arguments:
    // "\u0001\u0001"
    // ]
    // ASTORE 3

    // With latest changes, Sandboni must create this link:

    // "Dynamic call com.sandboni.core.engine.scenario.string.StringConcatenation/basicStringConcatenation() -> java.lang.invoke.StringConcatFactory/makeConcatWithConstants(java.lang.invoke.MethodHandles$Lookup,java.lang.String,java.lang.invoke.MethodType,java.lang.String,java.lang.Object[])"

    public void basicStringConcatenation() {
        String hello = "Hello ";
        String world = "world!";
        String message = hello + world;
    }


}
