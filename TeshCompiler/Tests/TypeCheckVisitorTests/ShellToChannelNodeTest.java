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

@RunWith(value = Parameterized.class)
public class ShellToChannelNodeTest {

    @Parameterized.Parameter(value = 0)
    public Type channelType;

    @Parameterized.Parameter(value = 1)
    public Type commandType;

    @Parameterized.Parameter(value = 2)
    public Type expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new IntType(), new IntType(), new IgnoreType()},
                {new IntType(), new FloatType(), new IgnoreType()},
                {new IntType(), new CharType(), new IgnoreType()},
                {new IntType(), new StringType(), new ErrorType()},
                {new IntType(), new BoolType(), new IgnoreType()},
                {new IntType(), new ArrayType(), new IgnoreType()},
                {new IntType(), new ChannelType(), new IgnoreType()},
                {new IntType(), new TextFileType(), new IgnoreType()},
                {new IntType(), new BinFileType(), new IgnoreType()},
                {new FloatType(), new IntType(), new IgnoreType()},
                {new FloatType(), new FloatType(), new IgnoreType()},
                {new FloatType(), new CharType(), new IgnoreType()},
                {new FloatType(), new StringType(), new ErrorType()},
                {new FloatType(), new BoolType(), new IgnoreType()},
                {new FloatType(), new ArrayType(), new IgnoreType()},
                {new FloatType(), new ChannelType(), new IgnoreType()},
                {new FloatType(), new TextFileType(), new IgnoreType()},
                {new FloatType(), new BinFileType(), new IgnoreType()},
                {new CharType(), new IntType(), new IgnoreType()},
                {new CharType(), new FloatType(), new IgnoreType()},
                {new CharType(), new StringType(), new ErrorType()},
                {new CharType(), new BoolType(), new IgnoreType()},
                {new CharType(), new CharType(), new IgnoreType()},
                {new CharType(), new ArrayType(), new IgnoreType()},
                {new CharType(), new ChannelType(), new IgnoreType()},
                {new CharType(), new TextFileType(), new IgnoreType()},
                {new CharType(), new BinFileType(), new IgnoreType()},
                {new StringType(), new IntType(), new IgnoreType()},
                {new StringType(), new FloatType(), new IgnoreType()},
                {new StringType(), new CharType(), new IgnoreType()},
                {new StringType(), new StringType(), new ErrorType()},
                {new StringType(), new BoolType(), new IgnoreType()},
                {new StringType(), new ArrayType(), new IgnoreType()},
                {new StringType(), new ChannelType(), new IgnoreType()},
                {new StringType(), new TextFileType(), new IgnoreType()},
                {new StringType(), new BinFileType(), new IgnoreType()},
                {new BoolType(), new IntType(), new IgnoreType()},
                {new BoolType(), new FloatType(), new IgnoreType()},
                {new BoolType(), new StringType(), new ErrorType()},
                {new BoolType(), new CharType(), new IgnoreType()},
                {new BoolType(), new BoolType(), new IgnoreType()},
                {new BoolType(), new ArrayType(), new IgnoreType()},
                {new BoolType(), new ChannelType(), new IgnoreType()},
                {new BoolType(), new TextFileType(), new IgnoreType()},
                {new BoolType(), new BinFileType(), new IgnoreType()},
                {new ArrayType(), new IntType(), new IgnoreType()},
                {new ArrayType(), new FloatType(), new IgnoreType()},
                {new ArrayType(), new CharType(), new IgnoreType()},
                {new ArrayType(), new StringType(), new ErrorType()},
                {new ArrayType(), new BoolType(), new IgnoreType()},
                {new ArrayType(), new ArrayType(), new IgnoreType()},
                {new ArrayType(), new ChannelType(), new IgnoreType()},
                {new ArrayType(), new TextFileType(), new IgnoreType()},
                {new ArrayType(), new BinFileType(), new IgnoreType()},
                {new ChannelType(), new IntType(), new IgnoreType()},
                {new ChannelType(), new FloatType(), new IgnoreType()},
                {new ChannelType(), new CharType(), new IgnoreType()},
                {new ChannelType(), new StringType(), new OkType()},
                {new ChannelType(), new BoolType(), new IgnoreType()},
                {new ChannelType(), new ArrayType(), new IgnoreType()},
                {new ChannelType(), new ChannelType(), new IgnoreType()},
                {new ChannelType(), new TextFileType(), new IgnoreType()},
                {new ChannelType(), new BinFileType(), new IgnoreType()},
                {new BinFileType(), new IntType(), new IgnoreType()},
                {new BinFileType(), new FloatType(), new IgnoreType()},
                {new BinFileType(), new StringType(), new ErrorType()},
                {new BinFileType(), new CharType(), new IgnoreType()},
                {new BinFileType(), new BoolType(), new IgnoreType()},
                {new BinFileType(), new ArrayType(), new IgnoreType()},
                {new BinFileType(), new ChannelType(), new IgnoreType()},
                {new BinFileType(), new TextFileType(), new IgnoreType()},
                {new BinFileType(), new BinFileType(), new IgnoreType()},
                {new TextFileType(), new IntType(), new IgnoreType()},
                {new TextFileType(), new FloatType(), new IgnoreType()},
                {new TextFileType(), new StringType(), new ErrorType()},
                {new TextFileType(), new CharType(), new IgnoreType()},
                {new TextFileType(), new BoolType(), new IgnoreType()},
                {new TextFileType(), new ArrayType(), new IgnoreType()},
                {new TextFileType(), new ChannelType(), new IgnoreType()},
                {new TextFileType(), new TextFileType(), new IgnoreType()}
        });
    }


    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ShellToChannelNode_Check_Expected() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        idNode.setType(channelType);
        TypeNode typeNode = new TypeNode(channelType.toString().toLowerCase());

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayList<ArithmeticExpressionNode> c = new ArrayList<ArithmeticExpressionNode>(){{
            add(new LiteralNode(0, new IntType()));
        }};

        ShellNode shellCommand = new ShellNode(new LiteralNode(0, commandType));
        shellCommand.accept(typeCheckVisitor);

        ShellToChannelNode node = new ShellToChannelNode(idNode, shellCommand);
        node.accept(typeCheckVisitor);

        String errMessage = channelType + ", " + commandType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }
}