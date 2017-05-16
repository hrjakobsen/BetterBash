package com.d401f17;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.SymTab;
import com.d401f17.TypeSystem.SymbolTable;
import com.d401f17.Visitors.BuildAstVisitor;
import com.d401f17.Visitors.Interpreter.InterpretVisitor;
import com.d401f17.Visitors.PrettyPrintASTVisitor;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        boolean debug = false;
        if (debug) {
            if (args.length == 0) {
                System.err.println("You must specify an input file");
                return;
            }

            if (!args[0].endsWith(".tsh")) {
                System.err.println("The input file must be a tesh-file");
                return;
            }

            CharStream inputStream;

            try {
                inputStream = CharStreams.fromFileName(args[0]);
            } catch (IOException e) {
                System.err.println("The input file could not be opened");
                return;
            }

            //Lex the input file to convert it to tokens
            TeshLexer lexer = new TeshLexer(inputStream);
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
            SymTab symbolTable = SymbolTable.StandardTable();
            SymTab recordTable = new SymbolTable();

            //Type check the AST
            TypeCheckVisitor typeCheck = new TypeCheckVisitor(symbolTable, recordTable);
            ast.accept(typeCheck);

            if (!typeCheck.getErrors().isEmpty()) {
                for (String err : typeCheck.getErrors()) {
                    System.err.println(err);
                }
                return;
            }

            if (args.length > 1) {
                if (args[1].equals("-i") || args[1].equals("-interpret")) {
                    InterpretVisitor run = new InterpretVisitor(recordTable);
                    ast.accept(run);
                } else if (args[1].equals("-p") || args[1].equals("-print")) {
                    if (args.length != 3) {
                        System.err.println("Please specify an output file for the dot-file");
                        return;
                    }
                    if (!args[2].endsWith(".dot")) {
                        System.err.println("Please output file must be a dot-file");
                        return;
                    }

                    PrettyPrintASTVisitor p = new PrettyPrintASTVisitor();
                    ast.accept(p);

                    PrintWriter writer =
                            new PrintWriter(
                                    new File(args[2]));
                    writer.print("graph {\n" + p.toString() + "\n}\n");
                    writer.flush();
                    writer.close();
                } else {
                    System.err.println("Please specify a mode: (i)nterpret or (p)rint");
                    return;
                }
            }
        } else {
            //InputStream is = new ByteArrayInputStream( "bool a = (10 * 0.1 == 1 && \"hej\" == (\"hej2\"))".getBytes() );
            InputStream is = Main.class.getResourceAsStream("/test.tsh");

            CharStream input = CharStreams.fromStream(is);
            TeshLexer lexer = new TeshLexer(input);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            TeshParser parser = new TeshParser(tokenStream);

            TeshParser.CompileUnitContext unit = parser.compileUnit();

            if (parser.getNumberOfSyntaxErrors() > 0) {
                return;
            }

            AST ast = new BuildAstVisitor().visitCompileUnit(unit);

            SymTab symbolTable = SymbolTable.StandardTable();
            SymTab recordTable = new SymbolTable();

            TypeCheckVisitor typeCheck = new TypeCheckVisitor(symbolTable, recordTable);
            ast.accept(typeCheck);

            for (String err : typeCheck.getErrors()) {
                System.err.println(err);
            }


            if (typeCheck.getErrors().size() > 0) return;

            InterpretVisitor run = new InterpretVisitor(recordTable);
            ast.accept(run);
        }
    }
}