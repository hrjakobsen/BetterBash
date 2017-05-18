package TypeCheckVisitorTests;

import com.d401f17.AST.Nodes.*;
import com.d401f17.SymbolTable.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

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
    private VariableDeclarationNode recordInstance;

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
        recordInstance = new VariableDeclarationNode(myPage, new TypeNode("recordpage"));
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
        if(leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new AdditionNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new AdditionNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new AdditionNode(new LiteralNode(0, leftType), myPage);
        }
        Type type;
        SimpleIdentifierNode a;
        try {
            type = ((RecordType) recordInstance.getName().getType()).getMemberType("child");
            Assert.assertEquals(new IntType(), type);
        } catch (com.d401f17.SymbolTable.MemberNotFoundException e) {
            type = null;
        }
        new AdditionNode(new LiteralNode(0, type), new LiteralNode(0, new IntType()));
        node.accept(typeCheckVisitor);
        Assert.assertEquals(new ErrorType(), node.getType());
    }

    @Test
    public void SubtractionNode() {
        SubtractionNode node;
        if(leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new SubtractionNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new SubtractionNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new SubtractionNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(new ErrorType(), node.getType());
    }

    @Test
    public void MultiplicationNode() {
        MultiplicationNode node;
        if(leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new MultiplicationNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new MultiplicationNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new MultiplicationNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(new ErrorType(), node.getType());
    }

    @Test
    public void DivisionNode() {
        DivisionNode node;
        if(leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new DivisionNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new DivisionNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new DivisionNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(new ErrorType(), node.getType());
    }

    @Test
    public void AndNode() {
        AndNode node;
        String errMessage = recordType + " && " + expectedType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            //myPage && myPage
            node = new AndNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            //myPage && rightType
            node = new AndNode(myPage, new LiteralNode(0, rightType));
        } else {
            //leftType && myPage
            node = new AndNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(errMessage, new ErrorType(), node.getType());
    }

    @Test
    public void OrNode() {
        OrNode node;
        String errMessage = recordType + " && " + expectedType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            //myPage && myPage
            node = new OrNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            //myPage && rightType
            node = new OrNode(myPage, new LiteralNode(0, rightType));
        } else {
            //leftType && myPage
            node = new OrNode(new LiteralNode(0, leftType), myPage);
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
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new AssignmentNode(myPage, myPage);
            errMessage = recordType + ", " + recordType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        } else if (leftType instanceof RecordType) {
            node = new AssignmentNode(myPage, new LiteralNode(1, rightType));
            errMessage = recordType + ", " + rightType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        } else {
            SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
            idNode.setType(leftType);
            TypeNode typeNode = new TypeNode(leftType.toString().toLowerCase());
            VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
            varNode.accept(typeCheckVisitor);

            node = new AssignmentNode(idNode, myPage);
            errMessage = leftType + ", " + recordType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }

    @Test
    public void EqualNode() {
        EqualNode node;
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new EqualNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new EqualNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new EqualNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(new ErrorType(), node.getType());
    }

    @Test
    public void NotEqualNode() {
        NotEqualNode node;
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new NotEqualNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new NotEqualNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new NotEqualNode(new LiteralNode(0,leftType), myPage);
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(new ErrorType(), node.getType());
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

    @Test
    public void PatternMatchNode() {
        PatternMatchNode node;
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            node = new PatternMatchNode(myPage, myPage);
        } else if (leftType instanceof RecordType) {
            node = new PatternMatchNode(myPage, new LiteralNode(0, rightType));
        } else {
            node = new PatternMatchNode(new LiteralNode(0, leftType), myPage);
        }
        node.accept(typeCheckVisitor);
        Assert.assertEquals(new ErrorType(), node.getType());
    }

    @Test
    public void FunctionDeclarationNode() {
        FunctionCallNode node;
        ArrayList<VariableDeclarationNode> array = new ArrayList<VariableDeclarationNode>();
        StatementsNode returnStatement;
        if (leftType instanceof RecordType) {
            returnStatement = new StatementsNode(new ReturnNode(new LiteralNode(0, recordType)));
        } else {
            returnStatement = new StatementsNode(new ReturnNode(new LiteralNode(0, leftType)));
        }
        returnStatement.accept(typeCheckVisitor);
        FunctionNode functionNode;
        if (rightType instanceof RecordType) {
            functionNode = new FunctionNode(new SimpleIdentifierNode("funcname"), new TypeNode("recordpage"), array, returnStatement);
        } else {
            functionNode = new FunctionNode(new SimpleIdentifierNode("funcname"), new TypeNode(rightType.toString()), array, returnStatement);
        }
        functionNode.accept(typeCheckVisitor);

        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            Assert.assertEquals(new OkType(), functionNode.getType());
        } else {
            Assert.assertEquals(expectedType, functionNode.getType());
        }
    }

    @Test
    public void ifReturn() {
        Type type1, type2;

        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            type1 = recordType;
            type2 = recordType;
        } else if (leftType instanceof RecordType) {
            type1 = recordType;
            type2 = rightType;
        } else {
            type1 = leftType;
            type2 = recordType;
        }
        /*
        1   if true {
        2       if false {
        3           return type1
        4       } else {
        5           return type2
        6       }
        7   } else {
        8       return type2
        9   }
         */
        StatementNode node = new StatementsNode(1,
                new IfNode(
                        new LiteralNode(true, new BoolType()),
                        new StatementsNode(2,
                                new IfNode(
                                        new LiteralNode(false, new BoolType()),
                                        new StatementsNode(3, new ReturnNode(new LiteralNode(0, type1), 3)),
                                        new StatementsNode(5, new ReturnNode(new LiteralNode(0, type2), 5)), 2)),
                        new StatementsNode(8,
                                new StatementsNode(8,
                                        new ReturnNode(new LiteralNode(0, type2), 8))
                        ), 1)
        );
        node.accept(typeCheckVisitor);

        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            Assert.assertEquals(recordType, node.getType());
        } else {
            Assert.assertEquals(new IgnoreType(), node.getType());
        }
    }

    @Test
    public void whileNode() {
        WhileNode node;
        StatementsNode returnNodeTrue;
        StatementsNode returnNodeFalse;
        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            returnNodeTrue = new StatementsNode(new ReturnNode(new LiteralNode(0, recordType)));
            returnNodeFalse = new StatementsNode(new ReturnNode(new LiteralNode(0, recordType)));
        } else if (leftType instanceof RecordType) {
            returnNodeTrue = new StatementsNode(new ReturnNode(new LiteralNode(0, recordType)));
            returnNodeFalse = new StatementsNode(new ReturnNode(new LiteralNode(0, rightType)));
        } else {
            returnNodeTrue = new StatementsNode(new ReturnNode(new LiteralNode(0, leftType)));
            returnNodeFalse = new StatementsNode(new ReturnNode(new LiteralNode(0, recordType)));
        }

        StatementsNode ifNode = new StatementsNode(new IfNode(new LiteralNode(1, new BoolType()), returnNodeTrue, returnNodeFalse));

        /*
        while b {
            if b {
                return returnNodeTrue
            } else {
                return returnNodeFalse
            }
        }
         */
        node = new WhileNode(new LiteralNode(1, new BoolType()), ifNode);
        node.accept(typeCheckVisitor);

        if (leftType instanceof RecordType && rightType instanceof RecordType) {
            Assert.assertEquals(recordType, node.getType());
        } else {
            Assert.assertEquals(new IgnoreType(), node.getType());
        }
    }
}

