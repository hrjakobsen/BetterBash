import com.d401f17.AST.Nodes.ConstantNode;
import com.d401f17.AST.Nodes.SubtractionNode;
import com.d401f17.AST.TypeSystem.Type;
import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/11/17.
 */
@RunWith(value = Parameterized.class)
public class SubtractionNodeTest {

    @Parameter(value = 0)
    public Types leftType;

    @Parameter(value = 1)
    public Types rightType;

    @Parameter(value = 2)
    public Types expectedType;

    @Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.INT, Types.INT},
                {Types.INT, Types.FLOAT, Types.FLOAT},
                {Types.INT, Types.CHAR, Types.ERROR},
                {Types.INT, Types.STRING, Types.ERROR},
                {Types.INT, Types.BOOL, Types.ERROR},
                {Types.INT, Types.ARRAY, Types.ERROR},
                {Types.INT, Types.CHANNEL, Types.ERROR},
                {Types.INT, Types.RECORD, Types.ERROR},
                {Types.INT, Types.FILE, Types.ERROR},
                {Types.FLOAT, Types.INT, Types.FLOAT},
                {Types.FLOAT, Types.FLOAT, Types.FLOAT},
                {Types.FLOAT, Types.CHAR, Types.ERROR},
                {Types.FLOAT, Types.STRING, Types.ERROR},
                {Types.FLOAT, Types.BOOL, Types.ERROR},
                {Types.FLOAT, Types.ARRAY, Types.ERROR},
                {Types.FLOAT, Types.CHANNEL, Types.ERROR},
                {Types.FLOAT, Types.RECORD, Types.ERROR},
                {Types.FLOAT, Types.FILE, Types.ERROR},
                {Types.CHAR, Types.INT, Types.CHAR},
                {Types.CHAR, Types.FLOAT, Types.ERROR},
                {Types.CHAR, Types.STRING, Types.ERROR},
                {Types.CHAR, Types.BOOL, Types.ERROR},
                {Types.CHAR, Types.CHAR, Types.ERROR},
                {Types.CHAR, Types.ARRAY, Types.ERROR},
                {Types.CHAR, Types.CHANNEL, Types.ERROR},
                {Types.CHAR, Types.RECORD, Types.ERROR},
                {Types.CHAR, Types.FILE, Types.ERROR},
                {Types.STRING, Types.INT, Types.ERROR},
                {Types.STRING, Types.FLOAT, Types.ERROR},
                {Types.STRING, Types.CHAR, Types.ERROR},
                {Types.STRING, Types.STRING, Types.ERROR},
                {Types.STRING, Types.BOOL, Types.ERROR},
                {Types.STRING, Types.ARRAY, Types.ERROR},
                {Types.STRING, Types.CHANNEL, Types.ERROR},
                {Types.STRING, Types.RECORD, Types.ERROR},
                {Types.STRING, Types.FILE, Types.ERROR},
                {Types.BOOL, Types.INT, Types.ERROR},
                {Types.BOOL, Types.FLOAT, Types.ERROR},
                {Types.BOOL, Types.STRING, Types.ERROR},
                {Types.BOOL, Types.CHAR, Types.ERROR},
                {Types.BOOL, Types.BOOL, Types.ERROR},
                {Types.BOOL, Types.ARRAY, Types.ERROR},
                {Types.BOOL, Types.CHANNEL, Types.ERROR},
                {Types.BOOL, Types.RECORD, Types.ERROR},
                {Types.BOOL, Types.FILE, Types.ERROR},
                {Types.ARRAY, Types.INT, Types.ERROR},
                {Types.ARRAY, Types.FLOAT, Types.ERROR},
                {Types.ARRAY, Types.CHAR, Types.ERROR},
                {Types.ARRAY, Types.STRING, Types.ERROR},
                {Types.ARRAY, Types.BOOL, Types.ERROR},
                {Types.ARRAY, Types.ARRAY, Types.ERROR},
                {Types.ARRAY, Types.CHANNEL, Types.ERROR},
                {Types.ARRAY, Types.RECORD, Types.ERROR},
                {Types.ARRAY, Types.FILE, Types.ERROR},
                {Types.CHANNEL, Types.INT, Types.ERROR},
                {Types.CHANNEL, Types.FLOAT, Types.ERROR},
                {Types.CHANNEL, Types.CHAR, Types.ERROR},
                {Types.CHANNEL, Types.STRING, Types.ERROR},
                {Types.CHANNEL, Types.BOOL, Types.ERROR},
                {Types.CHANNEL, Types.ARRAY, Types.ERROR},
                {Types.CHANNEL, Types.CHANNEL, Types.ERROR},
                {Types.CHANNEL, Types.RECORD, Types.ERROR},
                {Types.CHANNEL, Types.FILE, Types.ERROR},
                {Types.RECORD, Types.INT, Types.ERROR},
                {Types.RECORD, Types.FLOAT, Types.ERROR},
                {Types.RECORD, Types.CHAR, Types.ERROR},
                {Types.RECORD, Types.STRING, Types.ERROR},
                {Types.RECORD, Types.BOOL, Types.ERROR},
                {Types.RECORD, Types.ARRAY, Types.ERROR},
                {Types.RECORD, Types.CHANNEL, Types.ERROR},
                {Types.RECORD, Types.RECORD, Types.ERROR},
                {Types.RECORD, Types.FILE, Types.ERROR},
                {Types.FILE, Types.INT, Types.ERROR},
                {Types.FILE, Types.FLOAT, Types.ERROR},
                {Types.FILE, Types.STRING, Types.ERROR},
                {Types.FILE, Types.CHAR, Types.ERROR},
                {Types.FILE, Types.BOOL, Types.ERROR},
                {Types.FILE, Types.ARRAY, Types.ERROR},
                {Types.FILE, Types.CHANNEL, Types.ERROR},
                {Types.FILE, Types.RECORD, Types.ERROR},
                {Types.FILE, Types.FILE, Types.ERROR},
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void subtractionNode_typeCheckWithParameters_expected() {
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        SubtractionNode node = new SubtractionNode(new ConstantNode(1, leftType), new ConstantNode(1, rightType),0);
        node.accept(typeCheckVisitor);
        Assert.assertEquals(node.getType().getErrorMessage(), expectedType, node.getType().getPrimitiveType());
    }
}