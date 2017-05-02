package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.ChannelType;
import com.d401f17.TypeSystem.Type;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by mathias on 5/2/17.
 */
public class ChannelLiteralNode extends LiteralNode {
    public ChannelLiteralNode(Queue<String> value) {
        super(value, new ChannelType());
    }

    @Override
    public Queue<String> getValue() {
        return (Queue<String>)super.getValue();
    }

    public String read() {
        return getValue().poll();
    }

    public void write(String message) {
        getValue().add(message);
    }

}
