import com.d401f17.AST.Nodes.TypeNode;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by hu on 4/25/17.
 */
@RunWith(value = Parameterized.class)
public class TypeNodeTest {

    @Parameterized.Parameter(value = 0)
    public Types predicateType;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
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
                {new IgnoreType(), new IgnoreType()}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void TypeNode_Expect() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        TypeNode node = new TypeNode(predicateType.toString().toLowerCase());
        node.accept(typeCheckVisitor);

        String errMessage = predicateType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }
}