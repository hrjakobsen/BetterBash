import com.d401f17.AST.Nodes.AdditionNode;
import com.d401f17.AST.Nodes.ConstantNode;
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
 * Created by tessa on 4/10/17.
 */
@RunWith(value = Parameterized.class)
public class AdditionNodeTest {

    @Parameter(value = 0)
    public AdditionNode additionNode;

    @Parameter(value = 1)
    public Types expected;

    @Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new AdditionNode(new ConstantNode(1, Types.INT),new ConstantNode(1,Types.INT),0), Types.INT},
                {new AdditionNode(new ConstantNode(1, Types.FLOAT),new ConstantNode(1,Types.INT),0), Types.FLOAT},
                {new AdditionNode(new ConstantNode(1, Types.CHAR),new ConstantNode(1,Types.INT),0), Types.CHAR},
                {new AdditionNode(new ConstantNode(1, Types.STRING),new ConstantNode(1,Types.INT),0), Types.STRING},
                {new AdditionNode(new ConstantNode(1, Types.BOOL),new ConstantNode(1,Types.INT),0), Types.ERROR},
                {new AdditionNode(new ConstantNode(1, Types.CHANNEL),new ConstantNode(1,Types.INT),0), Types.ERROR},
                {new AdditionNode(new ConstantNode(1, Types.FILE),new ConstantNode(1,Types.INT),0), Types.ERROR},
                {new AdditionNode(new ConstantNode(1, Types.ARRAY),new ConstantNode(1,Types.INT),0), Types.ERROR}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void additionNode_typeCheckWithParameters_expected() {
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        additionNode.accept(typeCheckVisitor);
        Assert.assertEquals(additionNode.getType().getPrimitiveType(), expected);
    }
}