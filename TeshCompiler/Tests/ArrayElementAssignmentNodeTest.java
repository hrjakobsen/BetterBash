import com.d401f17.AST.Nodes.*;
import com.d401f17.AST.TypeSystem.SymTab;
import com.d401f17.AST.TypeSystem.SymbolTable;
import com.d401f17.AST.TypeSystem.Type;
import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
/**
 * Created by tessa on 4/20/17.
 */

@RunWith(value = Parameterized.class)
public class ArrayElementAssignmentNodeTest {


    @Parameter(value = 0)
    public Types leftType;

    @Parameter(value = 1)
    public Types rightType;

    @Parameter(value = 2)
    public Types expectedType;

    @Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.INT, Types.OK},
                {Types.INT, Types.FLOAT, Types.IGNORE},
                {Types.INT, Types.CHAR, Types.IGNORE},
                {Types.INT, Types.STRING, Types.IGNORE},
                {Types.INT, Types.BOOL, Types.IGNORE},
                {Types.INT, Types.ARRAY, Types.IGNORE},
                {Types.INT, Types.CHANNEL, Types.IGNORE},
                {Types.INT, Types.RECORD, Types.IGNORE},
                {Types.INT, Types.FILE, Types.IGNORE},
                {Types.FLOAT, Types.INT, Types.OK},
                {Types.FLOAT, Types.FLOAT, Types.OK},
                {Types.FLOAT, Types.CHAR, Types.IGNORE},
                {Types.FLOAT, Types.STRING, Types.IGNORE},
                {Types.FLOAT, Types.BOOL, Types.IGNORE},
                {Types.FLOAT, Types.ARRAY, Types.IGNORE},
                {Types.FLOAT, Types.CHANNEL, Types.IGNORE},
                {Types.FLOAT, Types.RECORD, Types.IGNORE},
                {Types.FLOAT, Types.FILE, Types.IGNORE},
                {Types.CHAR, Types.INT, Types.OK},
                {Types.CHAR, Types.FLOAT, Types.IGNORE},
                {Types.CHAR, Types.STRING, Types.IGNORE},
                {Types.CHAR, Types.BOOL, Types.IGNORE},
                {Types.CHAR, Types.CHAR, Types.OK},
                {Types.CHAR, Types.ARRAY, Types.IGNORE},
                {Types.CHAR, Types.CHANNEL, Types.IGNORE},
                {Types.CHAR, Types.RECORD, Types.IGNORE},
                {Types.CHAR, Types.FILE, Types.IGNORE},
                {Types.STRING, Types.INT, Types.IGNORE},
                {Types.STRING, Types.FLOAT, Types.IGNORE},
                {Types.STRING, Types.CHAR, Types.IGNORE},
                {Types.STRING, Types.STRING, Types.OK},
                {Types.STRING, Types.BOOL, Types.IGNORE},
                {Types.STRING, Types.ARRAY, Types.IGNORE},
                {Types.STRING, Types.CHANNEL, Types.IGNORE},
                {Types.STRING, Types.RECORD, Types.IGNORE},
                {Types.STRING, Types.FILE, Types.IGNORE},
                {Types.BOOL, Types.INT, Types.IGNORE},
                {Types.BOOL, Types.FLOAT, Types.IGNORE},
                {Types.BOOL, Types.STRING, Types.IGNORE},
                {Types.BOOL, Types.CHAR, Types.IGNORE},
                {Types.BOOL, Types.BOOL, Types.OK},
                {Types.BOOL, Types.ARRAY, Types.IGNORE},
                {Types.BOOL, Types.CHANNEL, Types.IGNORE},
                {Types.BOOL, Types.RECORD, Types.IGNORE},
                {Types.BOOL, Types.FILE, Types.IGNORE},
                {Types.ARRAY, Types.INT, Types.IGNORE},
                {Types.ARRAY, Types.FLOAT, Types.IGNORE},
                {Types.ARRAY, Types.CHAR, Types.IGNORE},
                {Types.ARRAY, Types.STRING, Types.IGNORE},
                {Types.ARRAY, Types.BOOL, Types.IGNORE},
                {Types.ARRAY, Types.ARRAY, Types.OK},
                {Types.ARRAY, Types.CHANNEL, Types.IGNORE},
                {Types.ARRAY, Types.RECORD, Types.IGNORE},
                {Types.ARRAY, Types.FILE, Types.IGNORE},
                {Types.CHANNEL, Types.INT, Types.IGNORE},
                {Types.CHANNEL, Types.FLOAT, Types.IGNORE},
                {Types.CHANNEL, Types.CHAR, Types.IGNORE},
                {Types.CHANNEL, Types.STRING, Types.IGNORE},
                {Types.CHANNEL, Types.BOOL, Types.IGNORE},
                {Types.CHANNEL, Types.ARRAY, Types.IGNORE},
                {Types.CHANNEL, Types.CHANNEL, Types.OK},
                {Types.CHANNEL, Types.RECORD, Types.IGNORE},
                {Types.CHANNEL, Types.FILE, Types.IGNORE},
                {Types.FILE, Types.INT, Types.IGNORE},
                {Types.FILE, Types.FLOAT, Types.IGNORE},
                {Types.FILE, Types.STRING, Types.IGNORE},
                {Types.FILE, Types.CHAR, Types.IGNORE},
                {Types.FILE, Types.BOOL, Types.IGNORE},
                {Types.FILE, Types.ARRAY, Types.IGNORE},
                {Types.FILE, Types.CHANNEL, Types.IGNORE},
                {Types.FILE, Types.RECORD, Types.IGNORE},
                {Types.FILE, Types.FILE, Types.OK}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ArrayElementAssignmentNode_typeCheckWithParameters_expected() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

       SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
       idNode.setType(new Type(leftType));
       TypeNode typeNode = new TypeNode(leftType.toString().toLowerCase());

       VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
       varNode.accept(typeCheckVisitor);

        ArrayList<ArithmeticExpressionNode> c = new ArrayList<ArithmeticExpressionNode>(){{
            add(new LiteralNode(0, leftType));
            add(new LiteralNode(0, rightType));
        }};

        ArrayAccessNode node = new ArrayAccessNode(idNode,c);

        ArrayElementAssignmentNode a = new ArrayElementAssignmentNode(node, new LiteralNode(0, leftType));
        a.accept(typeCheckVisitor);

        String errMessage = leftType + ", " + rightType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, a.getType().getPrimitiveType());
    }
}

