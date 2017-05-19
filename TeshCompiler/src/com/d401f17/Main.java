package com.d401f17;

import com.d401f17.AST.Nodes.*;
import com.d401f17.SymbolTable.SymTab;
import com.d401f17.SymbolTable.SymbolTable;
import com.d401f17.Visitors.BuildAstVisitor;
import com.d401f17.Visitors.CodeGenerator.ByteCodeVisitor;
import com.d401f17.Visitors.CodeGenerator.ClassDescriptor;
import com.d401f17.Visitors.Interpreter.InterpretVisitor;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        //InputStream is = new ByteArrayInputStream( "bool a = (10 * 0.1 == 1 && \"hej\" == (\"hej2\"))".getBytes() );
        InputStream is = Main.class.getResourceAsStream("/test.tsh");
        //Lex the input file to convert it to tokens
        CharStream input = CharStreams.fromStream(is);
        TeshLexer lexer = new TeshLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        //Parse the token stream
        TeshParser parser = new TeshParser(tokenStream);
        TeshParser.CompileUnitContext unit = parser.compileUnit();

        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }

        //Build Abstract Syntax Tree (AST)
        TeshBaseVisitor<AST> ASTBuilder = new BuildAstVisitor();
        AST ast = ASTBuilder.visitCompileUnit(unit);

        //Create a symbol table containing standard library of functions
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();

        //Type check the AST
        TypeCheckVisitor typeCheck = new TypeCheckVisitor(symbolTable, recordTable);
        ast.accept(typeCheck);

        for (String s : typeCheck.getErrors()) {
            System.out.println(s);
        }

        InterpretVisitor run = new InterpretVisitor(recordTable);
        ast.accept(run);
        /*ByteCodeVisitor run = new ByteCodeVisitor();

        ast.accept(run);
        run.End();

        List<ClassDescriptor> classes = run.getOtherClasses();
        classes.add(0, new ClassDescriptor("Main", run.getCw()));

        try {
            for (ClassDescriptor c : classes) {
                FileOutputStream fos = new FileOutputStream("/home/mathias/Desktop/javaprivatetest/" + c.getName() + ".class");
                fos.write(c.getWriter().toByteArray());
                fos.close();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

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