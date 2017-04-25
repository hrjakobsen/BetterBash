import com.d401f17.AST.Nodes.*;

import com.d401f17.TypeSystem.SymTab;
import com.d401f17.TypeSystem.SymbolTable;
import com.d401f17.TypeSystem.Type;
import com.d401f17.TypeSystem.Types;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/15/17.
 */
@RunWith(value = Parameterized.class)
public class ArrayAccessNodeTest {

    @Parameterized.Parameter(value = 0)
    public Types type;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.INT},
                {Types.FLOAT, Types.ERROR},
                {Types.CHAR, Types.ERROR},
                {Types.STRING, Types.ERROR},
                {Types.BOOL, Types.ERROR},
                {Types.ARRAY, Types.ERROR},
                {Types.CHANNEL, Types.ERROR},
                {Types.FILE, Types.ERROR},
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ArrayAccessNode_IndiceTest() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        idNode.setType(new Type(type));
        TypeNode typeNode = new TypeNode(type.toString().toLowerCase());

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayAccessNode node = new ArrayAccessNode(
                idNode,
                new ArrayList<ArithmeticExpressionNode>(){{add(new LiteralNode(0, type));}}
        );

        node.accept(typeCheckVisitor);

        String errMessage = type + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType().getPrimitiveType());
    }
}