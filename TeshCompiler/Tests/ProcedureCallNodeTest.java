/**
 * Created by hu on 4/25/17.
 */

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class ProcedureCallNodeTest {

    @Parameterized.Parameter(value = 0)
    public Types predicateType;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.OK},
                {Types.FLOAT, Types.OK},
                {Types.CHAR, Types.OK},
                {Types.STRING, Types.OK},
                {Types.BOOL, Types.OK},
                {Types.ARRAY, Types.OK},
                {Types.CHANNEL, Types.OK},
                {Types.RECORD, Types.OK},
                {Types.FILE, Types.OK},

        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ProcedureCallNode_ExpectToBeOKSinceFunctionIsDeclared() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        idNode.setType(new Type(predicateType));
        TypeNode typeNode = new TypeNode(predicateType.toString().toLowerCase());

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayList<VariableDeclarationNode> array = new ArrayList<VariableDeclarationNode>() {
            {
                add(varNode);
            }
        };

        StatementsNode returnStatement = new StatementsNode(new ReturnNode(new LiteralNode(0, predicateType)));
        FunctionNode functionNode = new FunctionNode(new SimpleIdentifierNode("funcname"), new TypeNode(predicateType.toString().toLowerCase()), array, returnStatement);
        functionNode.accept(typeCheckVisitor);

        ProcedureCallNode node = new ProcedureCallNode(functionNode.getName(), new LiteralNode(0, predicateType));
        node.accept(typeCheckVisitor);

        String errMessage = predicateType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType().getPrimitiveType());
    }
}