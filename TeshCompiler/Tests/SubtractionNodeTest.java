import com.d401f17.AST.Nodes.ConstantNode;
import com.d401f17.AST.Nodes.SubtractionNode;
import com.d401f17.AST.TypeSystem.Types;
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
public class SubtractionNodeTest {

    @Parameter(value = 0)
    public SubtractionNode subtractionNode;

    @Parameter(value = 1)
    public Types expected;

    @Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new SubtractionNode(new ConstantNode(1, Types.INT),new ConstantNode(1,Types.INT),0), Types.INT},
                {new SubtractionNode(new ConstantNode(1, Types.FLOAT),new ConstantNode(1,Types.INT),0), Types.FLOAT},
                {new SubtractionNode(new ConstantNode(1, Types.CHAR),new ConstantNode(1,Types.INT),0), Types.CHAR},
                {new SubtractionNode(new ConstantNode(1, Types.STRING),new ConstantNode(1,Types.INT),0), Types.STRING},
                {new SubtractionNode(new ConstantNode(1, Types.BOOL),new ConstantNode(1,Types.INT),0), Types.ERROR},
                {new SubtractionNode(new ConstantNode(1, Types.CHANNEL),new ConstantNode(1,Types.INT),0), Types.ERROR},
                {new SubtractionNode(new ConstantNode(1, Types.FILE),new ConstantNode(1,Types.INT),0), Types.ERROR},
                {new SubtractionNode(new ConstantNode(1, Types.ARRAY),new ConstantNode(1,Types.INT),0), Types.ERROR}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void subtractionNode_typeCheckWithParameters_expected() {
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        subtractionNode.accept(typeCheckVisitor);
        Assert.assertEquals(subtractionNode.getType().getPrimitiveType(), expected);
    }
}