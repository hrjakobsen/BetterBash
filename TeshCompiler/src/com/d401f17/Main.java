package com.d401f17;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.SymTab;
import com.d401f17.TypeSystem.SymbolTable;
import com.d401f17.Visitors.BuildAstVisitor;
import com.d401f17.Visitors.CodeGenerator.ByteCodeVisitor;
import com.d401f17.Visitors.PrettyPrintASTVisitor;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.*;

public class Main {

    public static void main(String[] args) throws Exception {
        //InputStream is = new ByteArrayInputStream( "bool a = (10 * 0.1 == 1 && \"hej\" == (\"hej2\"))".getBytes() );
        InputStream is = Main.class.getResourceAsStream("/bytecodetest.tsh");

        CharStream input = CharStreams.fromStream(is);
        TeshLexer lexer = new TeshLexer(input);
        CommonTokenStream tokenStream =new CommonTokenStream(lexer);
        TeshParser parser = new TeshParser(tokenStream);

        TeshParser.CompileUnitContext unit = parser.compileUnit();

        AST ast = new BuildAstVisitor().visitCompileUnit(unit);

        SymTab symbolTable = SymbolTable.StandardTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheck = new TypeCheckVisitor(symbolTable, recordTable);
        ast.accept(typeCheck);

        for (String err : typeCheck.getErrors()) {
            System.err.println(err);
        }

        if (typeCheck.getErrors().size() > 0) return;

        //InterpretVisitor run = new InterpretVisitor(recordTable);
        ByteCodeVisitor run = new ByteCodeVisitor();

        ast.accept(run);
        run.End();

        try {
            FileOutputStream fos = new FileOutputStream("/home/mathias/Desktop/Main.class");
            fos.write(run.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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