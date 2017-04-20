import com.d401f17.AST.Nodes.*;
import com.d401f17.AST.TypeSystem.*;
import com.d401f17.AST.TypeSystem.ArrayType;
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
public class ArrayConstantNodeNestedTest {

    @Parameterized.Parameter(value = 0)
    public Type type;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new ArrayType(Types.ARRAY, new Type(Types.INT)), Types.INT},
                {new ArrayType(Types.ARRAY, new Type(Types.FLOAT)), Types.FLOAT},
                {new ArrayType(Types.ARRAY, new Type(Types.CHAR)), Types.CHAR},
                {new ArrayType(Types.ARRAY, new Type(Types.STRING)), Types.STRING},
                {new ArrayType(Types.ARRAY, new Type(Types.BOOL)), Types.BOOL},
                {new ArrayType(Types.ARRAY, new Type(Types.ARRAY)), Types.ARRAY},
                {new ArrayType(Types.ARRAY, new Type(Types.CHANNEL)), Types.CHANNEL},
                {new ArrayType(Types.ARRAY, new Type(Types.RECORD)), Types.RECORD},
                {new ArrayType(Types.ARRAY, new Type(Types.FILE)), Types.FILE},
        });
    }


    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ArrayConstantNodeNested_IndiceTest() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);
        ArrayConstantNode array = new ArrayConstantNode(new ArrayList<ArithmeticExpressionNode>() {
            {
                add(new ConstantNode(0, expectedType));
            }
        });
        ArrayConstantNode node = new ArrayConstantNode(new ArrayList<ArithmeticExpressionNode>() {
            {
                add(array);
            }
        });

        node.accept(typeCheckVisitor);

        String errMessage = type + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        if(node.getType() instanceof ArrayType) {
            Assert.assertEquals(expectedType, ((ArrayType)((ArrayType)node.getType()).getChildType()).getChildType().getPrimitiveType());
        } else {
            Assert.assertEquals(expectedType, node.getType().getPrimitiveType());
        }
    }
}