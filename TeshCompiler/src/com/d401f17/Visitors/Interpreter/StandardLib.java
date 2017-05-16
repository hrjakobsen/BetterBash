package com.d401f17.Visitors.Interpreter;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.IntType;

import java.util.Random;
import java.util.Scanner;

/**
 * Created by mathias on 4/27/17.
 */
public final class StandardLib {
    public static StringLiteralNode LiteralToString(LiteralNode[] nodes) {
        LiteralNode node = nodes[0];
        if (node instanceof FloatLiteralNode || node instanceof IntLiteralNode || node instanceof CharLiteralNode) {
            return new StringLiteralNode(node.getValue().toString());
        }
        return null;
    }

    public static IntLiteralNode FloatToInt(LiteralNode[] nodes) {
        LiteralNode node = nodes[0];
        if (node instanceof FloatLiteralNode) {
            return new IntLiteralNode(((FloatLiteralNode)node).getValue().intValue());
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
        if (nodes[0] instanceof IntLiteralNode) {
            long val = (long) nodes[0].getValue();
            result = new FloatLiteralNode(Math.sqrt(val));
        } else if (nodes[0] instanceof FloatLiteralNode) {
            double val = (double) nodes[0].getValue();
            result = new FloatLiteralNode(Math.sqrt(val));
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
