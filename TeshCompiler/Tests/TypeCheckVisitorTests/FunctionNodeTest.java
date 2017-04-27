package TypeCheckVisitorTests;

import com.d401f17.AST.Nodes.*;
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
 * Created by hu on 4/25/17.
 */
@RunWith(value = Parameterized.class)
public class FunctionNodeTest {

    @Parameterized.Parameter(value = 0)
    public Type functionType;

    @Parameterized.Parameter(value = 1)
    public Type returnType;

    @Parameterized.Parameter(value = 2)
    public Type expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new IntType(), new IntType(), new IntType()},
                {new IntType(), new FloatType(), new ErrorType()},
                {new IntType(), new CharType(), new ErrorType()},
                {new IntType(), new StringType(), new ErrorType()},
                {new IntType(), new BoolType(), new ErrorType()},
                {new IntType(), new ArrayType(), new ErrorType()},
                {new IntType(), new ChannelType(), new ErrorType()},
                {new IntType(), new TextFileType(), new ErrorType()},
                {new IntType(), new BinFileType(), new ErrorType()},
                {new IntType(), new TextFileType(), new ErrorType()},
                {new IntType(), new VoidType(), new ErrorType()},
                {new FloatType(), new IntType(), new ErrorType()},
                {new FloatType(), new FloatType(), new FloatType()},
                {new FloatType(), new CharType(), new ErrorType()},
                {new FloatType(), new StringType(), new ErrorType()},
                {new FloatType(), new BoolType(), new ErrorType()},
                {new FloatType(), new ArrayType(), new ErrorType()},
                {new FloatType(), new ChannelType(), new ErrorType()},
                {new FloatType(), new TextFileType(), new ErrorType()},
                {new FloatType(), new BinFileType(), new ErrorType()},
                {new FloatType(), new TextFileType(), new ErrorType()},
                {new FloatType(), new VoidType(), new ErrorType()},
                {new CharType(), new IntType(), new ErrorType()},
                {new CharType(), new FloatType(), new ErrorType()},
                {new CharType(), new StringType(), new ErrorType()},
                {new CharType(), new BoolType(), new ErrorType()},
                {new CharType(), new CharType(), new CharType()},
                {new CharType(), new ArrayType(), new ErrorType()},
                {new CharType(), new ChannelType(), new ErrorType()},
                {new CharType(), new TextFileType(), new ErrorType()},
                {new CharType(), new BinFileType(), new ErrorType()},
                {new CharType(), new TextFileType(), new ErrorType()},
                {new CharType(), new VoidType(), new ErrorType()},
                {new StringType(), new IntType(), new ErrorType()},
                {new StringType(), new FloatType(), new ErrorType()},
                {new StringType(), new CharType(), new ErrorType()},
                {new StringType(), new StringType(), new StringType()},
                {new StringType(), new BoolType(), new ErrorType()},
                {new StringType(), new ArrayType(), new ErrorType()},
                {new StringType(), new ChannelType(), new ErrorType()},
                {new StringType(), new BinFileType(), new ErrorType()},
                {new StringType(), new TextFileType(), new ErrorType()},
                {new StringType(), new VoidType(), new ErrorType()},
                {new BoolType(), new IntType(), new ErrorType()},
                {new BoolType(), new FloatType(), new ErrorType()},
                {new BoolType(), new StringType(), new ErrorType()},
                {new BoolType(), new CharType(), new ErrorType()},
                {new BoolType(), new BoolType(), new BoolType()},
                {new BoolType(), new ArrayType(), new ErrorType()},
                {new BoolType(), new ChannelType(), new ErrorType()},
                {new BoolType(), new TextFileType(), new ErrorType()},
                {new BoolType(), new BinFileType(), new ErrorType()},
                {new BoolType(), new VoidType(), new ErrorType()},
                {new ArrayType(), new IntType(), new ErrorType()},
                {new ArrayType(), new FloatType(), new ErrorType()},
                {new ArrayType(), new CharType(), new ErrorType()},
                {new ArrayType(), new StringType(), new ErrorType()},
                {new ArrayType(), new BoolType(), new ErrorType()},
                {new ArrayType(), new ArrayType(), new ArrayType()},
                {new ArrayType(), new ChannelType(), new ErrorType()},
                {new ArrayType(), new BinFileType(), new ErrorType()},
                {new ArrayType(), new TextFileType(), new ErrorType()},
                {new ArrayType(), new VoidType(), new ErrorType()},
                {new ChannelType(), new IntType(), new ErrorType()},
                {new ChannelType(), new FloatType(), new ErrorType()},
                {new ChannelType(), new CharType(), new ErrorType()},
                {new ChannelType(), new StringType(), new ErrorType()},
                {new ChannelType(), new BoolType(), new ErrorType()},
                {new ChannelType(), new ArrayType(), new ErrorType()},
                {new ChannelType(), new ChannelType(), new ChannelType()},
                {new ChannelType(), new BinFileType(), new ErrorType()},
                {new ChannelType(), new TextFileType(), new ErrorType()},
                {new ChannelType(), new VoidType(), new ErrorType()},
                {new BinFileType(), new IntType(), new ErrorType()},
                {new BinFileType(), new FloatType(), new ErrorType()},
                {new BinFileType(), new StringType(), new ErrorType()},
                {new BinFileType(), new CharType(), new ErrorType()},
                {new BinFileType(), new BoolType(), new ErrorType()},
                {new BinFileType(), new ArrayType(), new ErrorType()},
                {new BinFileType(), new ChannelType(), new ErrorType()},
                {new BinFileType(), new BinFileType(), new BinFileType()},
                {new BinFileType(), new TextFileType(), new ErrorType()},
                {new BinFileType(), new VoidType(), new ErrorType()},
                {new TextFileType(), new IntType(), new ErrorType()},
                {new TextFileType(), new FloatType(), new ErrorType()},
                {new TextFileType(), new StringType(), new ErrorType()},
                {new TextFileType(), new CharType(), new ErrorType()},
                {new TextFileType(), new BoolType(), new ErrorType()},
                {new TextFileType(), new ArrayType(), new ErrorType()},
                {new TextFileType(), new ChannelType(), new ErrorType()},
                {new TextFileType(), new BinFileType(), new ErrorType()},
                {new TextFileType(), new TextFileType(), new TextFileType()},
                {new TextFileType(), new VoidType(), new ErrorType()},
                {new VoidType(), new IntType(), new ErrorType()},
                {new VoidType(), new FloatType(), new ErrorType()},
                {new VoidType(), new StringType(), new ErrorType()},
                {new VoidType(), new CharType(), new ErrorType()},
                {new VoidType(), new BoolType(), new ErrorType()},
                {new VoidType(), new ArrayType(), new ErrorType()},
                {new VoidType(), new ChannelType(), new ErrorType()},
                {new VoidType(), new BinFileType(), new ErrorType()},
                {new VoidType(), new TextFileType(), new ErrorType()},
                {new VoidType(), new VoidType(), new VoidType()},
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void FunctionNode_ExpectSameTypeAsFunction() {
        /*
        func testFunction(int param1) *functionType* {
            return *returnType*
        }
        */

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("param1");
        idNode.setType(new IntType());
        TypeNode typeNode = new TypeNode("int");
        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayList<VariableDeclarationNode> functionParameters = new ArrayList<VariableDeclarationNode>() {
            {
                add(varNode);
            }
        };

        StatementsNode functionBody = new StatementsNode(
                new ReturnNode(
                        new LiteralNode(0, returnType)
                )
        );

        FunctionNode node = new FunctionNode(
                new SimpleIdentifierNode("testFunction"),
                new TypeNode(functionType.toString().toLowerCase()), functionParameters, functionBody
        );

        node.accept(typeCheckVisitor);

        String errMessage = functionType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void FunctionNode_NoFunctionBody_ExpectError() {
        /*
        func testFunction(int param1) *functionType* {
        }
         */

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("param1");
        idNode.setType(new IntType());
        TypeNode typeNode = new TypeNode("int");

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayList<VariableDeclarationNode> functionParamters = new ArrayList<VariableDeclarationNode>() {
            {
                add(varNode);
            }
        };

        StatementsNode functionBody = new StatementsNode();

        FunctionNode node = new FunctionNode(
                new SimpleIdentifierNode("testFunction"),
                new TypeNode(functionType.toString().toLowerCase()), functionParamters, functionBody);

        node.accept(typeCheckVisitor);

        String errMessage = functionType + " => " + new ErrorType() + "\n" + typeCheckVisitor.getAllErrors();
        if (functionType instanceof VoidType) {
            Assert.assertEquals(errMessage, new VoidType(), node.getType());
        } else {
            Assert.assertEquals(errMessage, new ErrorType(), node.getType());
        }
    }
}