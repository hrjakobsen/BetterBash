package com.d401f17.Visitors.Interpreter;

import com.d401f17.AST.Nodes.*;

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
}
