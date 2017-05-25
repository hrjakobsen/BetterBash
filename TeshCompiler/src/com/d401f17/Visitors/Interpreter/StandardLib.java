package com.d401f17.Visitors.Interpreter;

import com.d401f17.AST.Nodes.*;
import com.d401f17.SymbolTable.FunctionSymbol;
import com.d401f17.SymbolTable.SymTab;
import com.d401f17.SymbolTable.SymbolTable;
import com.d401f17.SymbolTable.VariableAlreadyDeclaredException;
import com.d401f17.TypeSystem.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
            table.insert("getFilesFromDir", new FunctionSymbol(new FunctionType("getFilesFromDir", new Type[] {new StringType()}, new ArrayType(RecordType.binfile)), null, null));
            table.insert("openTextfile", new FunctionSymbol(new FunctionType("openTextfile", new Type[] {new StringType()}, RecordType.textfile), null, null));
            table.insert("writeText", new FunctionSymbol(new FunctionType("writeText", new Type[] {RecordType.textfile, new StringType()}, new BoolType()), null, null));
            table.insert("openBinfile", new FunctionSymbol(new FunctionType("openBinFile", new Type[] {new StringType()}, RecordType.binfile), null, null));
            table.insert("writeData", new FunctionSymbol(new FunctionType("writeData", new Type[] {RecordType.textfile, new ArrayType(new IntType())}, new BoolType()), null, null));

        } catch (VariableAlreadyDeclaredException e) {}
    }

    public static SymTab InsertFunctionNames() {
        SymTab table = new SymbolTable();
        InsertFunctionNames(table);
        return table;
    }

    public static void InsertFunctions (HashMap<String, Function<LiteralNode[], LiteralNode>> table) {
        table.put("intToStr", StandardLib::LiteralToString);
        table.put("floatToStr", StandardLib::LiteralToString);
        table.put("charToStr", StandardLib::LiteralToString);
        table.put("boolToStr", StandardLib::LiteralToString);
        table.put("floatToStr", StandardLib::LiteralToString);
        table.put("intVal", StandardLib::ParseInt);
        table.put("floatVal", StandardLib::ParseFloat);
        table.put("print", StandardLib::Print);
        table.put("read", StandardLib::Read);
        table.put("sqrt", StandardLib::SquareRoot);
        table.put("rnd", StandardLib::Random);
        table.put("ceil", StandardLib::Ceil);
        table.put("floor", StandardLib::Floor);
        table.put("empty", StandardLib::Empty);
        table.put("openTextfile", StandardLib::OpenTextFile);
        table.put("openBinfile", StandardLib::OpenBinFile);
        table.put("writeText", StandardLib::WriteText);
        table.put("writeData", StandardLib::WriteData);
        table.put("getFilesFromDir", StandardLib::GetFilesFromDirectory);
    }

    public static HashMap<String, Function<LiteralNode[], LiteralNode>> InsertFunctions() {
        HashMap<String, Function<LiteralNode[], LiteralNode>> table = new HashMap<>();
        InsertFunctions(table);
        return table;
    }

    public static StringLiteralNode LiteralToString(LiteralNode[] nodes) {
        LiteralNode node = nodes[0];
        return new StringLiteralNode(node.getValue().toString());
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

    public static BoolLiteralNode Empty(LiteralNode[] nodes) {
        ChannelLiteralNode chn = (ChannelLiteralNode)(nodes[0]);
        return new BoolLiteralNode(chn.getValue().isEmpty());
    }

    public static RecordLiteralNode OpenTextFile(LiteralNode[] nodes) {
        String path = (String)nodes[0].getValue();
        File f = new File(path);
        RecordLiteralNode file = new RecordLiteralNode(new HashMap<>(), RecordType.textfile);
        file.getValue().put("directory", new StringLiteralNode(path));
        file.getValue().put("error", new IntLiteralNode(0));
        file.getValue().put("name", new StringLiteralNode(f.getName()));
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                file.getValue().put("error", new IntLiteralNode(1));
            }
        }
        return file;
    }

    public static RecordLiteralNode OpenBinFile(LiteralNode[] nodes) {
        String path = (String)nodes[0].getValue();
        File f = new File(path);
        RecordLiteralNode file = new RecordLiteralNode(new HashMap<>(), RecordType.binfile);
        file.getValue().put("directory", new StringLiteralNode(path));
        file.getValue().put("error", new IntLiteralNode(0));
        file.getValue().put("name", new StringLiteralNode(f.getName()));
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                file.getValue().put("error", new IntLiteralNode(1));
            }
        }
        return file;
    }

    public static BoolLiteralNode WriteText(LiteralNode[] nodes) {
        RecordLiteralNode file = (RecordLiteralNode) nodes[0];
        String data = (String) nodes[1].getValue();

        String path = String.valueOf(file.getValue().get("directory").getValue());
        File f = new File(path);
        if (f.exists() && f.canWrite()) {
            try {
                Files.write(Paths.get(path), data.getBytes());
            } catch (IOException err) {
                return new BoolLiteralNode(false);
            }
            return new BoolLiteralNode(true);
        } else {
            return new BoolLiteralNode(false);
        }
    }

    public static BoolLiteralNode WriteData(LiteralNode[] nodes) {
        RecordLiteralNode file = (RecordLiteralNode) nodes[0];
        ValueArrayLiteralNode arrayData = (ValueArrayLiteralNode) nodes[1];

        byte[] data = new byte[arrayData.getValue().size()];

        for (int i = 0; i < arrayData.getValue().size(); i++) {
            data[i] = ((Long)arrayData.getValue().get(i).getValue()).byteValue();
        }

        String path = String.valueOf(file.getValue().get("directory").getValue());
        File f = new File(path);
        if (f.exists() && f.canWrite()) {
            try {
                Files.write(Paths.get(path), data);
            } catch (IOException err) {
                return new BoolLiteralNode(false);
            }
            return new BoolLiteralNode(true);
        } else {
            return new BoolLiteralNode(false);
        }
    }

    public static ValueArrayLiteralNode GetFilesFromDirectory(LiteralNode[] nodes) {
        String dir = nodes[0].getValue().toString();

        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        ArrayList<LiteralNode> files = new ArrayList<>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                files.add(OpenBinFile(new LiteralNode[]{new StringLiteralNode(file.getAbsolutePath())}));
            }
        }
        return new ValueArrayLiteralNode(files);
    }
}
