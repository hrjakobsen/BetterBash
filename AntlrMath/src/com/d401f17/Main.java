package com.d401f17;

import org.antlr.v4.runtime.*;
import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        InputStream is = new ByteArrayInputStream( "2+3*4".getBytes() );
        ANTLRInputStream input = new ANTLRInputStream(is);
        MathLexer lexer = new MathLexer(input);
        CommonTokenStream tokenStream =new CommonTokenStream(lexer);
        MathParser parser = new MathParser(tokenStream);

        try {
            MathParser.CompileUnitContext unit = parser.compileUnit();
            ExpressionNode ast = new BuildAstVisitor().visitCompileUnit(unit);
            String result = new PrintAstVisitor().visit(ast);
            System.out.println(result);
        } catch (Exception e) {

        }

    }
}
