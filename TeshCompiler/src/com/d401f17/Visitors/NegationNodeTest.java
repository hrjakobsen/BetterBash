package com.d401f17.Visitors;
import com.d401f17.AST.Nodes.NegationNode;
import com.d401f17.AST.Nodes.ConstantNode;
import com.d401f17.AST.TypeSystem.Types;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/11/17.
 */
@RunWith(value = Parameterized.class)
public class NegationNodeTest {

    @Parameterized.Parameter(value = 0)
    public Types leftType;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.ERROR},
                {Types.FLOAT, Types.ERROR},
                {Types.CHAR, Types.ERROR},
                {Types.STRING, Types.ERROR},
                {Types.BOOL, Types.BOOL},
                {Types.ARRAY, Types.ERROR},
                {Types.CHANNEL, Types.ERROR},
                {Types.RECORD, Types.ERROR},
                {Types.FILE, Types.ERROR},

        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void NegationNode_typeCheckWithParameters_expected() {
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        NegationNode node = new NegationNode(new ConstantNode(1, leftType),0);
        node.accept(typeCheckVisitor);
        Assert.assertEquals(expectedType, node.getType().getPrimitiveType());
    }
}