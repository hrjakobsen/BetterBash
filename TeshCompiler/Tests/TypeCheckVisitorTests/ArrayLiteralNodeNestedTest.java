package TypeCheckVisitorTests;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.TypeSystem.ArrayType;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/20/17.
 */
@RunWith(value = Parameterized.class)
public class ArrayLiteralNodeNestedTest {

    @Parameterized.Parameter(value = 0)
    public Type type;

    @Parameterized.Parameter(value = 1)
    public Type expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new IntType(), new ArrayType(new ArrayType(new IntType()))},
                {new FloatType(), new ArrayType(new ArrayType(new FloatType()))},
                {new CharType(), new ArrayType(new ArrayType(new CharType()))},
                {new StringType(), new ArrayType(new ArrayType(new StringType()))},
                {new BoolType(), new ArrayType(new ArrayType(new BoolType()))},
                {new ArrayType(), new ArrayType(new ArrayType(new ArrayType()))},
                {new ChannelType(), new ArrayType(new ArrayType(new ChannelType()))},
                {new BinFileType(), new ArrayType(new ArrayType(new BinFileType()))},
                {new TextFileType(), new ArrayType(new ArrayType(new TextFileType()))},
        });
    }


    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ArrayConstantNodeNested_IndiceTest() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);
        ArrayLiteralNode array = new ArrayLiteralNode(new ArrayList<ArithmeticExpressionNode>() {
            {
                add(new LiteralNode(0, type));
                add(new LiteralNode(0, type));
            }
        });
        ArrayLiteralNode node = new ArrayLiteralNode(new ArrayList<ArithmeticExpressionNode>() {
            {
                add(array);
                add(array);
            }
        });

        node.accept(typeCheckVisitor);

        String errMessage = type + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        if(node.getType() instanceof ArrayType) {
            Assert.assertEquals(errMessage, expectedType, node.getType());
        } else {
            Assert.fail();
        }
    }
}