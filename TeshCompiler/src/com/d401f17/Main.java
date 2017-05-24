package com.d401f17;

import com.d401f17.AST.Nodes.*;
import com.d401f17.SymbolTable.SymTab;
import com.d401f17.SymbolTable.SymbolTable;
import com.d401f17.Visitors.BuildAstVisitor;
import com.d401f17.Visitors.CodeGenerator.ByteCodeVisitor;
import com.d401f17.Visitors.CodeGenerator.ClassDescriptor;
import com.d401f17.Visitors.Interpreter.InterpretVisitor;
import com.d401f17.Visitors.PrettyPrintASTVisitor;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        boolean debug = true;
        if (!debug) {
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
            SymTab symbolTable = new SymbolTable();
            SymTab recordTable = SymbolTable.TableWithDefaultRecords();

            //Type check the AST
            TypeCheckVisitor typeCheck = new TypeCheckVisitor(symbolTable, recordTable);
            ast.accept(typeCheck);

            if (!typeCheck.getErrors().isEmpty()) {
                for (String err : typeCheck.getErrors()) {
                    System.err.println(err);
                }
                return;
            }

            if (args.length == 1) {
                ByteCodeVisitor run = new ByteCodeVisitor();

                ast.accept(run);
                run.End();

                List<ClassDescriptor> classes = run.getOtherClasses();
                classes.add(0, new ClassDescriptor("Main", run.getCw()));

                try {
                    Path tempDir = Files.createTempDirectory("teshsource");
                    System.out.println("Executing in: " + tempDir);
                    for (ClassDescriptor c : classes) {
                        FileOutputStream fos = new FileOutputStream(tempDir.toString() + "/" + c.getName() + ".class");
                        fos.write(c.getWriter().toByteArray());
                        fos.close();
                    }
                    String[] Libraries = {"RecursiveSymbolTable.class", "StdFunc.class"};
                    for (String lib : Libraries) {
                        InputStream stream = Main.class.getResourceAsStream("/" + lib);
                        Path p = Paths.get(tempDir.toString(), lib);
                        Files.copy(stream, p);
                    }

                    ProcessBuilder builder = new ProcessBuilder("java", "-cp", tempDir.toString(), "Main");
                    builder.redirectErrorStream(true);

                    Process p = builder.start();
                    BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = b.readLine()) != null) {
                        System.out.println(line);
                    }

                    p.waitFor();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (args.length > 1) {
                if (args[1].equals("-i") || args[1].equals("-interpret")) {
                    InterpretVisitor run = new InterpretVisitor(SymbolTable.TableWithDefaultRecords());
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
                    FileOutputStream fos = new FileOutputStream(args[2]);
                    fos.write(p.toString().getBytes());
                    fos.close();
                }
            }
        }

        if (!debug) {
            return;
        }

        InputStream is = Main.class.getResourceAsStream("/1chat.tsh");
        CharStream input = CharStreams.fromStream(is);

        //Lex the input file to convert it to tokens
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
        SymTab recordTable = SymbolTable.TableWithDefaultRecords();

        //Type check the AST
        TypeCheckVisitor typeCheck = new TypeCheckVisitor(symbolTable, recordTable);
        ast.accept(typeCheck);

        for (String s : typeCheck.getErrors()) {
            System.out.println(s);
        }

        if (typeCheck.getErrors().size() > 0) {
            return;
        }

        //InterpretVisitor run = new InterpretVisitor(recordTable);
        //ast.accept(run);
        ByteCodeVisitor run = new ByteCodeVisitor();

        ast.accept(run);
        run.End();

        List<ClassDescriptor> classes = run.getOtherClasses();
        classes.add(0, new ClassDescriptor("Main", run.getCw()));

        try {
            Path tempDir = Files.createTempDirectory("teshsource");
            System.out.println("Executing in: " + tempDir);
            for (ClassDescriptor c : classes) {
                FileOutputStream fos = new FileOutputStream(tempDir.toString() + "/" + c.getName() + ".class");
                fos.write(c.getWriter().toByteArray());
                fos.close();
            }
            String[] Libraries = {"RecursiveSymbolTable.class", "StdFunc.class", "binfile.class"};
            for (String lib : Libraries) {
                InputStream stream = Main.class.getResourceAsStream("/" + lib);
                Path p = Paths.get(tempDir.toString(), lib);
                Files.copy(stream, p);
            }

            ProcessBuilder builder = new ProcessBuilder("java", "-cp", tempDir.toString(), "Main");
            builder.redirectErrorStream(true);

            Process p = builder.start();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = b.readLine()) != null) {
                System.out.println(line);
            }

            p.waitFor();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}