package com.d401f17.Visitors.CodeGenerator;

import org.objectweb.asm.ClassWriter;

public class ClassDescriptor {
    String name;
    ClassWriter writer;

    public ClassDescriptor(String name, ClassWriter writer) {
        this.name = name;
        this.writer = writer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassWriter getWriter() {
        return writer;
    }

    public void setWriter(ClassWriter writer) {
        this.writer = writer;
    }
}
