package TypeCheckVisitorTests;

import com.d401f17.AST.Nodes.*;

import com.d401f17.SymbolTable.SymTab;
import com.d401f17.SymbolTable.SymbolTable;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/15/17.
 */
@RunWith(value = Parameterized.class)
public class ArrayAccessNodeTest {

    @Parameterized.Parameter(value = 0)
    public Type type;

    @Parameterized.Parameter(value = 1)
    public Type expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new IntType(), new IntType()},
                {new FloatType(), new ErrorType()},
                {new CharType(), new ErrorType()},
                {new StringType(), new ErrorType()},
                {new BoolType(), new ErrorType()},
                {new ArrayType(), new ErrorType()},
                {new ChannelType(), new ErrorType()},
                {new BinFileType(), new ErrorType()},
                {new TextFileType(), new ErrorType()}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ArrayAccessNode_IndiceTest() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        ArrayType arrayType = new ArrayType(type);
        idNode.setType(arrayType);
        TypeNode typeNode = new TypeNode(arrayType.toString().toLowerCase());

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayAccessNode node = new ArrayAccessNode(
                idNode,
                new ArrayList<ArithmeticExpressionNode>(){{add(new LiteralNode(0, type));}}
        );

        node.accept(typeCheckVisitor);

        String errMessage = arrayType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }
}