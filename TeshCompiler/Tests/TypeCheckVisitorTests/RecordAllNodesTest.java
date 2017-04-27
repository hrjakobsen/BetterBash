package TypeCheckVisitorTests;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.TypeCheckVisitor;
import jdk.nashorn.internal.ir.Assignment;
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

    private RecordDeclarationNode recorddcl;
    private SymTab symbolTable;
    private SymTab recordTable;
    private SimpleIdentifierNode page;
    private TypeCheckVisitor typeCheckVisitor;
    private Type recordType;
    private ArithmeticExpressionNode record;

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

        recorddcl = new RecordDeclarationNode("page", variables);
        recorddcl.accept(typeCheckVisitor);
        recordType = recorddcl.getType();

        variables.remove(0);
        page = new SimpleIdentifierNode("page");
        String[] a = {"child"};
        Type[] b = {intType};
        recordType = new RecordType("page",a,b);
        page.setType(recordType);
        page.setName("page");
        VariableDeclarationNode recordInstance = new VariableDeclarationNode(page, new TypeNode("recordpage"));
        recordInstance.setType(recordType);
        recordInstance.accept(typeCheckVisitor);
        record = recordInstance.getName();
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
                {new RecordType(), new RecordType(), new OkType()},
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
                {new RecordType(), new ArrayType(), new ErrorType()},
                {new RecordType(), new ChannelType(), new ErrorType()},
                {new RecordType(), new BinFileType(), new ErrorType()},
                {new RecordType(), new TextFileType(), new ErrorType()},
        });
    }

    @Test
    public void AdditionNode() {
        AdditionNode node;
        String errMessage = recordType + ", " + expectedType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        if (leftType instanceof RecordType && !(rightType instanceof RecordType)) {
            node = new AdditionNode(record, new LiteralNode(0, rightType));
        } else if (rightType instanceof RecordType && !(leftType instanceof RecordType)) {
            node = new AdditionNode(new LiteralNode(0, leftType), record);
        } else {
            node = new AdditionNode(record, record);
        }
        node.accept(typeCheckVisitor);
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            Assert.assertEquals(new ErrorType(), node.getType());
        } else {
            Assert.assertEquals(errMessage, expectedType, node.getType());
        }
    }

    @Test
    public void AndNode() {
        AndNode node;
        String errMessage = recordType + ", " + expectedType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        if (leftType instanceof RecordType && !(rightType instanceof RecordType)) {
            node = new AndNode(record, new LiteralNode(0, rightType));
        } else if (rightType instanceof RecordType && !(leftType instanceof RecordType)) {
            node = new AndNode(new LiteralNode(0, leftType), record);
        } else {
            node = new AndNode(record, record);
        }
        node.accept(typeCheckVisitor);
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            Assert.assertEquals(new ErrorType(), node.getType());
        } else {
            Assert.assertEquals(errMessage, expectedType, node.getType());
        }
    }

    @Test
    public void ArrayAccessNode() {
        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        ArrayType arrayType = new ArrayType(recordType);
        idNode.setType(arrayType);
        TypeNode typeNode = new TypeNode(arrayType.toString().toLowerCase());

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayAccessNode node = new ArrayAccessNode(
                idNode,
                new ArrayList<ArithmeticExpressionNode>(){{add(new LiteralNode(0, recordType));}}
        );

        node.accept(typeCheckVisitor);

        String errMessage = arrayType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, new ArrayType(recordType), node.getType());
    }

    /*
    @Test
    public void ArrayBuilderNode() {
        Assert.fail("Implement this");
    }
    */

    @Test
    public void ArrayElementAssignmentNode() {
        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        ArrayType arrayType;
        if(leftType instanceof RecordType) {
            arrayType = new ArrayType(recordType);
        } else {
            arrayType = new ArrayType(leftType);
        }

        idNode.setType(arrayType);
        TypeNode typeNode = new TypeNode(arrayType.toString().toLowerCase());

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayList<ArithmeticExpressionNode> indices = new ArrayList<ArithmeticExpressionNode>(){{
            add(new LiteralNode(0, new IntType()));
        }};

        ArrayAccessNode node = new ArrayAccessNode(idNode, indices);
        node.accept(typeCheckVisitor);
        ArrayElementAssignmentNode a;

        if (leftType instanceof RecordType) {
            a = new ArrayElementAssignmentNode(node, new LiteralNode(0, rightType));
        } else
            a = new ArrayElementAssignmentNode(node, new LiteralNode(0, recordType));
        a.accept(typeCheckVisitor);

        String errMessage = arrayType + ", " + recordType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        if (arrayType.getChildType() instanceof RecordType) {
            Assert.assertEquals(errMessage, new OkType(), a.getType());
        } else {
            Assert.assertEquals(errMessage, expectedType, a.getType());
        }
    }

    @Test
    public void AssignmentNode() {
        AssignmentNode node;
        String errMessage;
        if (leftType instanceof RecordType && !(rightType instanceof RecordType)) {
            node = new AssignmentNode(page, new LiteralNode(1, rightType));
            errMessage = recordType + ", " + rightType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        } else if (rightType instanceof RecordType && !(leftType instanceof RecordType)){
            SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
            idNode.setType(leftType);
            TypeNode typeNode = new TypeNode(leftType.toString().toLowerCase());
            VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
            varNode.accept(typeCheckVisitor);

            node = new AssignmentNode(idNode, record);
            errMessage = leftType + ", " + recordType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        } else {
            node = new AssignmentNode(page, page);
            errMessage = recordType + ", " + recordType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }

    @Test
    public void EqualNode() {
        EqualNode node;
        if (leftType instanceof RecordType && !(rightType instanceof RecordType)) {
            node = new EqualNode(record,new LiteralNode(0,rightType));
        } else if (rightType instanceof RecordType && !(leftType instanceof RecordType)) {
            node = new EqualNode(new LiteralNode(0, leftType),record);
        } else {
            node = new EqualNode(record, record);
        }
        node.accept(typeCheckVisitor);

        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            Assert.assertEquals(new ErrorType(), node.getType());
        } else {
            Assert.assertEquals(expectedType, node.getType());
        }
    }
}
