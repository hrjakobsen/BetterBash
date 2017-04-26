import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import java.util.Arrays;
import java.util.Collection;
/**
 * Created by tessa on 4/11/17.
*/

@RunWith(value = Parameterized.class)
public class AssignmentNodeTest {


    @Parameter(value = 0)
    public Type leftType;

    @Parameter(value = 1)
    public Type rightType;

    @Parameter(value = 2)
    public Type expectedType;

    @Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new IntType(), new IntType(), new OkType()},
                {new IntType(), new FloatType(), new ErrorType()},
                {new IntType(), new CharType(), new ErrorType()},
                {new IntType(), new StringType(), new ErrorType()},
                {new IntType(), new BoolType(), new ErrorType()},
                {new IntType(), new ArrayType(), new ErrorType()},
                {new IntType(), new ChannelType(), new ErrorType()},
                {new IntType(), new TextFileType(), new ErrorType()},
                {new IntType(), new BinFileType(), new ErrorType()},
                {new FloatType(), new IntType(), new OkType()},
                {new FloatType(), new FloatType(), new OkType()},
                {new FloatType(), new CharType(), new ErrorType()},
                {new FloatType(), new StringType(), new ErrorType()},
                {new FloatType(), new BoolType(), new ErrorType()},
                {new FloatType(), new ArrayType(), new ErrorType()},
                {new FloatType(), new ChannelType(), new ErrorType()},
                {new FloatType(), new TextFileType(), new ErrorType()},
                {new FloatType(), new BinFileType(), new ErrorType()},
                {new CharType(), new IntType(), new CharType()},
                {new CharType(), new FloatType(), new ErrorType()},
                {new CharType(), new StringType(), new ErrorType()},
                {new CharType(), new BoolType(), new ErrorType()},
                {new CharType(), new CharType(), new CharType()},
                {new CharType(), new ArrayType(), new ErrorType()},
                {new CharType(), new ChannelType(), new ErrorType()},
                {new CharType(), new TextFileType(), new ErrorType()},
                {new CharType(), new BinFileType(), new ErrorType()},
                {new StringType(), new IntType(), new ErrorType()},
                {new StringType(), new FloatType(), new ErrorType()},
                {new StringType(), new CharType(), new ErrorType()},
                {new StringType(), new StringType(), new OkType()},
                {new StringType(), new BoolType(), new ErrorType()},
                {new StringType(), new ArrayType(), new ErrorType()},
                {new StringType(), new ChannelType(), new ErrorType()},
                {new StringType(), new TextFileType(), new ErrorType()},
                {new StringType(), new BinFileType(), new ErrorType()},
                {new BoolType(), new IntType(), new ErrorType()},
                {new BoolType(), new FloatType(), new ErrorType()},
                {new BoolType(), new StringType(), new ErrorType()},
                {new BoolType(), new CharType(), new ErrorType()},
                {new BoolType(), new BoolType(), new OkType()},
                {new BoolType(), new ArrayType(), new ErrorType()},
                {new BoolType(), new ChannelType(), new ErrorType()},
                {new BoolType(), new TextFileType(), new ErrorType()},
                {new BoolType(), new BinFileType(), new ErrorType()},
                {new ArrayType(), new IntType(), new ErrorType()},
                {new ArrayType(), new FloatType(), new ErrorType()},
                {new ArrayType(), new CharType(), new ErrorType()},
                {new ArrayType(), new StringType(), new ErrorType()},
                {new ArrayType(), new BoolType(), new ErrorType()},
                {new ArrayType(), new ArrayType(), new OkType()},
                {new ArrayType(), new ChannelType(), new ErrorType()},
                {new ArrayType(), new TextFileType(), new ErrorType()},
                {new ArrayType(), new BinFileType(), new ErrorType()},
                {new ChannelType(), new IntType(), new ErrorType()},
                {new ChannelType(), new FloatType(), new ErrorType()},
                {new ChannelType(), new CharType(), new ErrorType()},
                {new ChannelType(), new StringType(), new ErrorType()},
                {new ChannelType(), new BoolType(), new ErrorType()},
                {new ChannelType(), new ArrayType(), new ErrorType()},
                {new ChannelType(), new ChannelType(), new OkType()},
                {new ChannelType(), new TextFileType(), new ErrorType()},
                {new ChannelType(), new BinFileType(), new ErrorType()},
                {new BinFileType(), new IntType(), new ErrorType()},
                {new BinFileType(), new FloatType(), new ErrorType()},
                {new BinFileType(), new StringType(), new ErrorType()},
                {new BinFileType(), new CharType(), new ErrorType()},
                {new BinFileType(), new BoolType(), new ErrorType()},
                {new BinFileType(), new ArrayType(), new ErrorType()},
                {new BinFileType(), new ChannelType(), new ErrorType()},
                {new BinFileType(), new TextFileType(), new ErrorType()},
                {new BinFileType(), new BinFileType(), new OkType()},
                {new TextFileType(), new IntType(), new ErrorType()},
                {new TextFileType(), new FloatType(), new ErrorType()},
                {new TextFileType(), new StringType(), new ErrorType()},
                {new TextFileType(), new CharType(), new ErrorType()},
                {new TextFileType(), new BoolType(), new ErrorType()},
                {new TextFileType(), new ArrayType(), new ErrorType()},
                {new TextFileType(), new ChannelType(), new ErrorType()},
                {new TextFileType(), new TextFileType(), new OkType()}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void AssignmentNode_typeCheckWithParameters_expected() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        idNode.setType(leftType);
        TypeNode typeNode = new TypeNode(leftType.toString().toLowerCase());
        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        AssignmentNode node = new AssignmentNode(idNode, new LiteralNode(1, rightType));
        node.accept(typeCheckVisitor);

        String errMessage = leftType + ", " + rightType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node);
    }
}

