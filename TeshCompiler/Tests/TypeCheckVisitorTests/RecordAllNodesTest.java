package TypeCheckVisitorTests;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by hu on 4/27/17.
 */
@RunWith(value = Parameterized.class)
public class RecordAllNodesTest {

    private RecordDeclarationNode record;
    private SymTab symbolTable;
    private SymTab recordTable;
    private TypeCheckVisitor typeCheckVisitor;
    private Type recordType;
    private ArithmeticExpressionNode recordInstance;

    @Before
    public void SetUp() {
        symbolTable = new SymbolTable();
        recordTable = new SymbolTable();
        typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("child");
        Type intType = new IntType();
        idNode.setType(intType);
        TypeNode typeNode = new TypeNode(intType.toString().toLowerCase());

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayList<VariableDeclarationNode> variables = new ArrayList<VariableDeclarationNode>() {
            {
                add(varNode);
            }
        };

        record = new RecordDeclarationNode("page", variables);
        record.accept(typeCheckVisitor);
        recordType = record.getType();

        variables.remove(0);
        SimpleIdentifierNode page = new SimpleIdentifierNode("page");
        String[] a = {"child"};
        Type[] b = {intType};
        recordType = new RecordType("page",a,b);
        page.setType(recordType);
        page.setName("page");
        VariableDeclarationNode recordInstance = new VariableDeclarationNode(page, new TypeNode(recordType.toString().toLowerCase()));
        recordInstance.setType(recordType);
        recordInstance.accept(typeCheckVisitor);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Parameterized.Parameter(value = 0)
    public Type leftType;

    @Parameterized.Parameter(value = 1)
    public Type rightType;

    @Parameterized.Parameter(value = 2)
    public Type expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new IntType(), new RecordType(), new ErrorType()},
                {new FloatType(), new RecordType(), new ErrorType()},
                {new CharType(), new RecordType(), new ErrorType()},
                {new StringType(), new RecordType(), new ErrorType()},
                {new BoolType(), new RecordType(), new ErrorType()},
                {new ArrayType(), new RecordType(), new ErrorType()},
                {new ChannelType(), new RecordType(), new ErrorType()},
                {new BinFileType(), new RecordType(), new ErrorType()},
                {new TextFileType(), new RecordType(), new ErrorType()},
                {new RecordType(), new IntType(), new ErrorType()},
                {new RecordType(), new FloatType(), new ErrorType()},
                {new RecordType(), new StringType(), new ErrorType()},
                {new RecordType(), new CharType(), new ErrorType()},
                {new RecordType(), new BoolType(), new ErrorType()},
                {new RecordType(), new RecordType(), new ErrorType()},
                {new RecordType(), new ArrayType(), new ErrorType()},
                {new RecordType(), new ChannelType(), new ErrorType()},
                {new RecordType(), new BinFileType(), new ErrorType()},
                {new RecordType(), new TextFileType(), new ErrorType()},
        });
    }

    @Test
    public void AdditionNode() {
        if (leftType instanceof RecordType) {
            new AdditionNode(recordInstance, new LiteralNode(0, rightType));
            AdditionNode node = new AdditionNode(new LiteralNode(1, leftType), new LiteralNode(1, rightType));
            node.accept(typeCheckVisitor);
            String errMessage = recordType + ", " + expectedType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
            Assert.assertEquals(errMessage, expectedType, node.getType());
        }
    }
}
