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

    private RecordDeclarationNode recorddcl;
    private SymTab symbolTable;
    private SymTab recordTable;
    private SimpleIdentifierNode myPage;
    private TypeCheckVisitor typeCheckVisitor;
    private Type recordType;

    @Before
    public void SetUp() {
        /*
        record page {
            int child;
        }

        record page myPage;

        */
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

        myPage = new SimpleIdentifierNode("myPage");
        recordType = new RecordType("page");
        myPage.setType(recordType);
        myPage.setName("myPage");
        VariableDeclarationNode recordInstance = new VariableDeclarationNode(myPage, new TypeNode("recordpage"));
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
        String errMessage = recordType + " + " + expectedType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        if (leftType instanceof RecordType && !(rightType instanceof RecordType)) {
            // myPage + rightType
            node = new AdditionNode(myPage, new LiteralNode(0, rightType));
        } else if (rightType instanceof RecordType && !(leftType instanceof RecordType)) {
            //leftType + myPage
            node = new AdditionNode(new LiteralNode(0, leftType), myPage);
        } else {
            //myPage + myPage
            node = new AdditionNode(myPage, myPage);
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(errMessage, new ErrorType(), node.getType());
    }

    @Test
    public void AndNode() {
        AndNode node;
        String errMessage = recordType + " && " + expectedType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        if (leftType instanceof RecordType && !(rightType instanceof RecordType)) {
            //myPage && rightType
            node = new AndNode(myPage, new LiteralNode(0, rightType));
        } else if (rightType instanceof RecordType && !(leftType instanceof RecordType)) {
            //leftType && myPage
            node = new AndNode(new LiteralNode(0, leftType), myPage);
        } else {
            //myPage && myPage
            node = new AndNode(myPage, myPage);
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(errMessage, new ErrorType(), node.getType());
    }

    @Test
    public void ArrayAccessNode() {
        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        ArrayType arrayType = new ArrayType(recordType);
        idNode.setType(arrayType);
        TypeNode typeNode = new TypeNode("recordpage");

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayAccessNode node = new ArrayAccessNode(
                idNode,
                new ArrayList<ArithmeticExpressionNode>(){{add(new LiteralNode(0, new IntType()));}}
        );

        node.accept(typeCheckVisitor);

        String errMessage = arrayType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, recordType, node.getType());
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
        TypeNode typeNode;
        //multiple spaces between Record and page for the typenode. Only happens during tests
        if (!(arrayType.getChildType() instanceof RecordType)) {
            typeNode = new TypeNode(arrayType.toString().toLowerCase());
        } else {
            typeNode = new TypeNode("recordpage");
        }

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayList<ArithmeticExpressionNode> indices = new ArrayList<ArithmeticExpressionNode>(){{
            add(new LiteralNode(0, new IntType()));
        }};

        ArrayAccessNode node = new ArrayAccessNode(idNode, indices);
        node.accept(typeCheckVisitor);
        ArrayElementAssignmentNode a;

        if (leftType instanceof RecordType && !(rightType instanceof RecordType)) {
            a = new ArrayElementAssignmentNode(node, new LiteralNode(0, rightType));
        } else
            a = new ArrayElementAssignmentNode(node, new LiteralNode(0, recordType));
        a.accept(typeCheckVisitor);

        String errMessage = arrayType + ", " + recordType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        if (arrayType.getChildType() instanceof RecordType && rightType instanceof RecordType) {
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
            node = new AssignmentNode(myPage, new LiteralNode(1, rightType));
            errMessage = recordType + ", " + rightType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        } else if (rightType instanceof RecordType && !(leftType instanceof RecordType)){
            SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
            idNode.setType(leftType);
            TypeNode typeNode = new TypeNode(leftType.toString().toLowerCase());
            VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
            varNode.accept(typeCheckVisitor);

            node = new AssignmentNode(idNode, myPage);
            errMessage = leftType + ", " + recordType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        } else {
            node = new AssignmentNode(myPage, myPage);
            errMessage = recordType + ", " + recordType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }

    @Test
    public void EqualNode() {
        EqualNode node;
        if (leftType instanceof RecordType && !(rightType instanceof RecordType)) {
            node = new EqualNode(myPage,new LiteralNode(0,rightType));
        } else if (rightType instanceof RecordType && !(leftType instanceof RecordType)) {
            node = new EqualNode(new LiteralNode(0, leftType), myPage);
        } else {
            node = new EqualNode(myPage, myPage);
        }
        node.accept(typeCheckVisitor);

        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            Assert.assertEquals(new ErrorType(), node.getType());
        } else {
            Assert.assertEquals(expectedType, node.getType());
        }
    }

    @Test
    public void ForNode() {
        ForNode node;
        ArrayLiteralNode array = new ArrayLiteralNode(new ArrayList<ArithmeticExpressionNode>() {{
                add(new LiteralNode(0, recordType));
                add(new LiteralNode(0, recordType));
            }});
        array.accept(typeCheckVisitor);
        node = new ForNode(new SimpleIdentifierNode(""), array, new StatementsNode());
        node.accept(typeCheckVisitor);
        Assert.assertEquals(new OkType(), node.getType());
    }

    @Test
    public void GreaterThanNode() {
        GreaterThanNode node;
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new GreaterThanNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new GreaterThanNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new GreaterThanNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);

        Assert.assertEquals(new ErrorType(), node.getType());
    }

    @Test
    public void GreaterThanOrEqualNode() {
        GreaterThanOrEqualNode node;
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new GreaterThanOrEqualNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new GreaterThanOrEqualNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new GreaterThanOrEqualNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);

        Assert.assertEquals(new ErrorType(), node.getType());
    }

    @Test
    public void LessThanOrEqualNode() {
        LessThanOrEqualNode node;
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new LessThanOrEqualNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new LessThanOrEqualNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new LessThanOrEqualNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);

        Assert.assertEquals(new ErrorType(), node.getType());
    }

    @Test
    public void LessThanNode() {
        LessThanNode node;
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new LessThanNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new LessThanNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new LessThanNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);

        Assert.assertEquals(new ErrorType(), node.getType());
    }

    @Test
    public void ModuloNode() {
        ModuloNode node;
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new ModuloNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new ModuloNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new ModuloNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);

        Assert.assertEquals(new ErrorType(), node.getType());
    }
}
