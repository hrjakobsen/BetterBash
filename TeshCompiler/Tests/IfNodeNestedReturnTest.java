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
 * Created by hense on 4/12/17.
 */
@RunWith(value = Parameterized.class)
public class IfNodeNestedReturnTest {
    @Parameterized.Parameter(value = 0)
    public Types return1;

    @Parameterized.Parameter(value = 1)
    public Types return2;

    @Parameterized.Parameter(value = 2)
    public Types return3;

    @Parameterized.Parameter(value = 3)
    public Types expected;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {Types.STRING, Types.STRING, Types.STRING, Types.STRING },
                {Types.INT, Types.STRING, Types.STRING, Types.IGNORE },
                {Types.STRING, Types.INT, Types.STRING, Types.IGNORE },
                {Types.STRING, Types.STRING, Types.INT, Types.IGNORE },
                {Types.INT, Types.INT, Types.INT, Types.INT },
                {Types.VOID, Types.INT, Types.INT, Types.IGNORE },
                {Types.VOID, Types.VOID, Types.INT, Types.IGNORE },
                {Types.VOID, Types.VOID, Types.VOID, Types.VOID },
                {Types.CHAR, Types.INT, Types.INT, Types.IGNORE },
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void IfNode_NestedReturns_TypesPropagate() {
        /*
        1   if true {
        2       if false {
        3           return return1
        4       } else {
        5           return return2
        6       }
        7   } else {
        8       return return3
        9   }
         */

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        StatementNode node = new StatementsNode(
                1,
                new IfNode(
                        new ConstantNode(true, Types.BOOL),
                        new StatementsNode(
                                2,
                                new IfNode (
                                        new ConstantNode(false, Types.BOOL),
                                        new StatementsNode(
                                                3,
                                                new ReturnNode(new ConstantNode(0, return1),3)
                                        ),
                                        new StatementsNode(
                                                5,
                                                new ReturnNode(new ConstantNode(0, return2),5)
                                        ),
                                        2
                                )
                        ),
                        new StatementsNode(
                                8,
                                new StatementsNode(
                                        8,
                                        new ReturnNode(new ConstantNode(0, return3),8)
                                )
                        ),
                        1
                )
        );

        node.accept(typeCheckVisitor);
        Assert.assertEquals(typeCheckVisitor.getAllErrors(), expected, node.getType().getPrimitiveType());
    }
}
