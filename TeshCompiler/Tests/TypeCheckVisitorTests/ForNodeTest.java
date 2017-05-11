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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/20/17.
 */
@RunWith(value = Parameterized.class)
public class ForNodeTest {

    @Parameterized.Parameter(value = 0)
    public Type predicateType;

    @Parameterized.Parameter(value = 1)
    public Type expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new IntType(), new OkType()},
                {new FloatType(), new OkType()},
                {new CharType(), new OkType()},
                {new StringType(), new OkType()},
                {new BoolType(), new OkType()},
                {new ArrayType(), new OkType()},
                {new ChannelType(), new OkType()},
                {new BinFileType(), new OkType()},
                {new TextFileType(), new OkType()}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ForNode_ForNodeTypeIsStatementsType() {
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();


        ArrayLiteralNode array = new ArrayLiteralNode(new ArrayList<ArithmeticExpressionNode>() {{
                add(new LiteralNode(0, predicateType));
                add(new LiteralNode(0, predicateType));
        }});

        ForNode node = new ForNode(new SimpleIdentifierNode(""), array, new StatementsNode());
        node.accept(typeCheckVisitor);

        String errMessage = predicateType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ForNode_PredicateMustBeBool() {
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();


        ArrayLiteralNode array = new ArrayLiteralNode(new ArrayList<ArithmeticExpressionNode>() {{
            add(new LiteralNode(0, predicateType));
            add(new LiteralNode(0, predicateType));
        }});
        typeCheckVisitor.visit(array);

        ForNode node = new ForNode(new SimpleIdentifierNode(""), array,
                new LiteralNode(0, ((ArrayType)array.getType()).getChildType()));
        node.accept(typeCheckVisitor);

        //ForNode changes its type to the type of the statementsNode, in this case, predicateType
        String errMessage = predicateType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, predicateType, node.getType());
    }
}