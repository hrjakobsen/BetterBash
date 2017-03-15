package com.d401f17;

import com.d401f17.AST.Nodes.*;
import com.d401f17.Visitors.BuildAstVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws Exception {
        InputStream is = new ByteArrayInputStream( "a += 2".getBytes() );
        ANTLRInputStream input = new ANTLRInputStream(is);
        TeshLexer lexer = new TeshLexer(input);
        CommonTokenStream tokenStream =new CommonTokenStream(lexer);
        TeshParser parser = new TeshParser(tokenStream);

        try {
            TeshParser.CompileUnitContext unit = parser.compileUnit();
            AST ast = new BuildAstVisitor().visitCompileUnit(unit);
        } catch (Exception e) {

        }
    }
}