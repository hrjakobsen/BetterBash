package com.d401f17.Visitors.Interpreter;

import com.d401f17.AST.Nodes.*;
import com.d401f17.SymbolTable.FunctionSymbol;
import com.d401f17.SymbolTable.SymTab;
import com.d401f17.SymbolTable.SymbolTable;
import com.d401f17.SymbolTable.VariableAlreadyDeclaredException;
import com.d401f17.TypeSystem.*;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Created by mathias on 4/27/17.
 */
public final class StandardLib {
    public static void InsertFunctionNames (SymTab table) {
        try {
            table.insert("intToStr", new FunctionSymbol(new FunctionType("intToStr", new Type[] {new IntType()}, new StringType()), null, null));
            table.insert("floatToStr", new FunctionSymbol(new FunctionType("floatToStr", new Type[] {new FloatType()}, new StringType()), null, null));
            table.insert("charToStr", new FunctionSymbol(new FunctionType("charToStr", new Type[] {new CharType()}, new StringType()), null, null));
            table.insert("boolToStr", new FunctionSymbol(new FunctionType("boolToStr", new Type[] {new BoolType()}, new StringType()), null, null));
            table.insert("intVal", new FunctionSymbol(new FunctionType("intVal", new Type[] {new StringType()}, new IntType()), null, null));
            table.insert("floatVal", new FunctionSymbol(new FunctionType("floatVal", new Type[] {new StringType()}, new FloatType()), null, null));
            table.insert("print", new FunctionSymbol(new FunctionType("print", new Type[] {new StringType()}, new VoidType()), null, null));
            table.insert("read", new FunctionSymbol(new FunctionType("read", new Type[] {}, new StringType()), null, null));
            table.insert("sqrt", new FunctionSymbol(new FunctionType("sqrt", new Type[] {new FloatType()}, new FloatType()), null, null));
            table.insert("rnd", new FunctionSymbol(new FunctionType("rnd", new Type[] {}, new FloatType()), null, null));
            table.insert("ceil", new FunctionSymbol(new FunctionType("ceil", new Type[] {new FloatType()}, new IntType()), null, null));
            table.insert("floor", new FunctionSymbol(new FunctionType("floor", new Type[] {new FloatType()}, new IntType()), null, null));
            table.insert("empty", new FunctionSymbol(new FunctionType("empty", new Type[] {new ChannelType()}, new BoolType()), null, null));
            table.insert("getFilesFromDir", new FunctionSymbol(new FunctionType("getFilesFromDir", new Type[] {new StringType()}, new ArrayType(new RecordType("binfile", new String[]{"error", "directory", "name"}, new Type[]{new IntType(),new StringType(), new StringType()}))), null, null));
        } catch (VariableAlreadyDeclaredException e) {}
    }

    public static SymTab InsertFunctionNames() {
        SymTab table = new SymbolTable();
        InsertFunctionNames(table);
        return table;
    }

    public static void InsertFunctions (HashMap<String, Function<LiteralNode[], LiteralNode>> table) {
        table.put("intToStr", StandardLib::LiteralToString);
        table.put("charToStr", StandardLib::LiteralToString);
        table.put("boolToStr", StandardLib::LiteralToString);
        table.put("intVal", StandardLib::ParseInt);
        table.put("floatVal", StandardLib::ParseFloat);
        table.put("print", StandardLib::Print);
        table.put("read", StandardLib::Read);
        table.put("sqrt", StandardLib::SquareRoot);
        table.put("rnd", StandardLib::Random);
        table.put("ceil", StandardLib::Ceil);
        table.put("floor", StandardLib::Floor);
    }

    public static HashMap<String, Function<LiteralNode[], LiteralNode>> InsertFunctions() {
        HashMap<String, Function<LiteralNode[], LiteralNode>> table = new HashMap<>();
        InsertFunctions(table);
        return table;
    }

    public static StringLiteralNode LiteralToString(LiteralNode[] nodes) {
        LiteralNode node = nodes[0];
        if (node instanceof FloatLiteralNode || node instanceof IntLiteralNode || node instanceof CharLiteralNode) {
            return new StringLiteralNode(node.getValue().toString());
        }
        return null;
    }

    public static IntLiteralNode ParseInt(LiteralNode[] nodes) {
        LiteralNode node = nodes[0];
        if (node instanceof StringLiteralNode) {
            return new IntLiteralNode(Long.parseLong(((StringLiteralNode)node).getValue()));
        }
        return null;
    }

    public static FloatLiteralNode ParseFloat(LiteralNode[] nodes) {
        LiteralNode node = nodes[0];
        if (node instanceof StringLiteralNode) {
            return new FloatLiteralNode(Double.parseDouble(((StringLiteralNode)node).getValue()));
        }
        return null;
    }

    public static StringLiteralNode Read(LiteralNode[] nodes) {
        Scanner scanner = new Scanner(System.in);
        StringLiteralNode s = new StringLiteralNode(scanner.next());
        scanner.close();
        return s;
    }

    public static LiteralNode Print(LiteralNode[] nodes) {
        System.out.println(nodes[0].getValue());
        return null;
    }

    public static FloatLiteralNode SquareRoot(LiteralNode[] nodes) {
        FloatLiteralNode result = null;
        if (nodes[0].getType() instanceof FloatType) {
            double d;
            if (nodes[0].getType() instanceof IntType) {
                d = ((Long)nodes[0].getValue()).doubleValue();
            } else {
                d = (double) nodes[0].getValue();
            }

            result = new FloatLiteralNode(Math.sqrt(d));
        }
        return result;
    }

    public static FloatLiteralNode Random(LiteralNode[] nodes) {
        Random rnd = new Random();
        return new FloatLiteralNode(rnd.nextDouble());
    }

    public static IntLiteralNode Ceil(LiteralNode[] nodes) {
        return new IntLiteralNode((long)Math.ceil((double) nodes[0].getValue()));
    }

    public static IntLiteralNode Floor(LiteralNode[] nodes) {
        return new IntLiteralNode((long)Math.floor((double) nodes[0].getValue()));
    }
}
