import com.d401f17.AST.Nodes.*;
import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/17/17.
 */
@RunWith(value = Parameterized.class)
public class WhileNodeWithStatementsTest {

    @Parameterized.Parameter(value = 0)
    public Types type;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.INT},
                {Types.FLOAT, Types.FLOAT},
                {Types.CHAR, Types.CHAR},
                {Types.STRING, Types.STRING},
                {Types.BOOL, Types.BOOL},
                {Types.ARRAY, Types.ARRAY},
                {Types.CHANNEL, Types.CHANNEL},
                {Types.RECORD, Types.RECORD},
                {Types.FILE, Types.FILE},
                {Types.ERROR, Types.ERROR},
                {Types.IGNORE, Types.IGNORE}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void WhileNode() {
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        WhileNode node = new WhileNode(new ConstantNode(1, Types.BOOL), new StatementsNode(new ReturnNode(new ConstantNode( 0, type))));
        node.accept(typeCheckVisitor);


        //String errMessage = type + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(expectedType, node.getType().getPrimitiveType());
    }
}