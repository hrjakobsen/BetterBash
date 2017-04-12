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
    public Types type1;

    @Parameterized.Parameter(value = 1)
    public Types type2;

    @Parameterized.Parameter(value = 2)
    public Types type3;

    @Parameterized.Parameter(value = 3)
    public Types expectedType;

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
        3           return type1
        4       } else {
        5           return type2
        6       }
        7   } else {
        8       return type3
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
                                                new ReturnNode(new ConstantNode(0, type1),3)
                                        ),
                                        new StatementsNode(
                                                5,
                                                new ReturnNode(new ConstantNode(0, type2),5)
                                        ),
                                        2
                                )
                        ),
                        new StatementsNode(
                                8,
                                new StatementsNode(
                                        8,
                                        new ReturnNode(new ConstantNode(0, type3),8)
                                )
                        ),
                        1
                )
        );

        node.accept(typeCheckVisitor);

        String errMessage = type1 + ", " + type2 + ", " + type3 + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType().getPrimitiveType());
    }
}
