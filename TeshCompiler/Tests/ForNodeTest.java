import com.d401f17.AST.Nodes.*;
import com.d401f17.AST.TypeSystem.*;
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
    public Types predicateType;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.OK},
                {Types.FLOAT, Types.OK},
                {Types.CHAR, Types.OK},
                {Types.STRING, Types.OK},
                {Types.BOOL, Types.OK},
                {Types.ARRAY, Types.OK},
                {Types.CHANNEL, Types.OK},
                {Types.RECORD, Types.OK},
                {Types.FILE, Types.OK}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ForNode_ForNodeTypeIsStatementsType() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);


        ArrayLiteralNode array = new ArrayLiteralNode(new ArrayList<ArithmeticExpressionNode>() {{
                add(new LiteralNode(0, predicateType));
                add(new LiteralNode(0, predicateType));
        }});

        ForNode node = new ForNode(new SimpleIdentifierNode(""), array, new StatementsNode());
        node.accept(typeCheckVisitor);

        String errMessage = predicateType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType().getPrimitiveType());
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ForNode_PredicateMustBeBool() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);


        ArrayLiteralNode array = new ArrayLiteralNode(new ArrayList<ArithmeticExpressionNode>() {{
            add(new LiteralNode(0, predicateType));
            add(new LiteralNode(0, predicateType));
        }});
        typeCheckVisitor.visit(array);

        ForNode node = new ForNode(new SimpleIdentifierNode(""), array,
                new LiteralNode(0, ((ArrayType)array.getType()).getChildType().getPrimitiveType()));
        node.accept(typeCheckVisitor);

        String errMessage = predicateType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, predicateType, node.getType().getPrimitiveType());
    }
}