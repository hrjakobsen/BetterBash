package TypeCheckVisitorTests;

import com.d401f17.AST.Nodes.*;
import com.d401f17.SymbolTable.SymTab;
import com.d401f17.SymbolTable.SymbolTable;
import com.d401f17.TypeSystem.*;
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
    public Type predicateType;

    @Parameterized.Parameter(value = 1)
    public Type expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new IntType(), new OkType()},
                {new FloatType(), new OkType()},
                {new CharType(), new OkType()},
                {new StringType(), new OkType()},
                {new BoolType(), new OkType()},
                {new ArrayType(), new OkType()},
                {new ChannelType(), new OkType()},
                {new BinFileType(), new OkType()},
                {new TextFileType(), new OkType()},

        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ProcedureCallNode_ExpectToBeOKSinceFunctionIsDeclared() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        idNode.setType(predicateType);
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
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }
}