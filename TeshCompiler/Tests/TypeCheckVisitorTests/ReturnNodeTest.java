package TypeCheckVisitorTests;

import com.d401f17.AST.Nodes.*;

import com.d401f17.TypeSystem.*;
import com.d401f17.TypeSystem.SymbolTable.SymTab;
import com.d401f17.TypeSystem.SymbolTable.SymbolTable;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/19/17.
 */
@RunWith(value = Parameterized.class)
public class ReturnNodeTest {

    @Parameterized.Parameter(value = 0)
    public Type predicateType;

    @Parameterized.Parameter(value = 1)
    public Type expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new IntType(), new IntType()},
                {new FloatType(), new FloatType()},
                {new CharType(), new CharType()},
                {new StringType(), new StringType()},
                {new BoolType(), new BoolType()},
                {new ArrayType(), new ArrayType()},
                {new ChannelType(), new ChannelType()},
                {new RecordType(), new RecordType()},
                {new BinFileType(), new BinFileType()},
                {new TextFileType(), new TextFileType()},
                {new ErrorType(), new IgnoreType()},
                {new IgnoreType(), new IgnoreType()},
                {new OkType(), new OkType()}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ShellNode_typeCheckWithParameters_expected() {
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        ReturnNode node = new ReturnNode(new LiteralNode(0, predicateType));
        node.accept(typeCheckVisitor);

        String errMessage = predicateType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }
}