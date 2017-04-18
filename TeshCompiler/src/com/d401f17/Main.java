package com.d401f17;

import com.d401f17.AST.Nodes.*;
import com.d401f17.AST.TypeSystem.Type;
import com.d401f17.Visitors.BuildAstVisitor;
import com.d401f17.Visitors.PrettyPrintASTVisitor;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.*;

public class Main {

    public static void main(String[] args) throws Exception {
        //InputStream is = new ByteArrayInputStream( "int a\nstring b\na = b".getBytes() );
        InputStream is = Main.class.getResourceAsStream("/channelExample.tsh");

        CharStream input = CharStreams.fromStream(is);
        TeshLexer lexer = new TeshLexer(input);
        CommonTokenStream tokenStream =new CommonTokenStream(lexer);
        TeshParser parser = new TeshParser(tokenStream);

        TeshParser.CompileUnitContext unit = parser.compileUnit();
        AST ast = new BuildAstVisitor().visitCompileUnit(unit);

        TypeCheckVisitor typeCheck = new TypeCheckVisitor();
        ast.accept(typeCheck);

        for (String err : typeCheck.getErrors()) {
            System.out.println(err);
        }

/*
        PrettyPrintASTVisitor p = new PrettyPrintASTVisitor();
        ast.accept(p);
        PrintWriter writer =
                new PrintWriter(
                        new File("/home/mathias/Desktop/output.dot"));
        writer.print("graph {\n" + p.toString() + "\n}\n");
        writer.flush();
        writer.close();
*/
    }
}