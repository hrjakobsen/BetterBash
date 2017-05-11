package com.d401f17.TypeSystem.SymbolTable;

import com.d401f17.AST.Nodes.AST;
import com.d401f17.TypeSystem.Type;

/**
 * Created by Henrik on 05-04-2017.
 */
public class Symbol {
    private Type type;
    private AST declarationNode;
    private int address;

    public Symbol(Type type, AST declarationNode) {
        this.type = type;
        this.declarationNode = declarationNode;
    }

    public Type getType() {
        return type;
    }

    public AST getDeclarationNode() {
        return declarationNode;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }


}
