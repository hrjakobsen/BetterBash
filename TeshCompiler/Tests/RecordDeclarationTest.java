import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class RecordDeclarationTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Parameterized.Parameter(value = 0)
    public Types predicateType;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.INT},
                {Types.FLOAT, Types.FLOAT},
                {Types.CHAR, Types.CHAR},
                {Types.STRING, Types.STRING},
                {Types.BOOL, Types.BOOL},
                {Types.ARRAY, Types.ARRAY},
                {Types.CHANNEL, Types.CHANNEL},
                {Types.FILE, Types.FILE}

        });
    }
    @Test
    public void RecordDeclarationNode_AddRecordWithEachType_ExpectedRecordPresentInRecordTable() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        idNode.setType(new Type(predicateType));
        TypeNode typeNode = new TypeNode(predicateType.toString().toLowerCase());

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        ArrayList<VariableDeclarationNode> variables = new ArrayList<VariableDeclarationNode>() {
            {
                add(varNode);
            }
        };

        RecordDeclarationNode node = new RecordDeclarationNode("record",variables);

        node.accept(typeCheckVisitor);
        String errMessage = predicateType + ", " + expectedType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        try {
            Assert.assertEquals(errMessage, Types.RECORD, recordTable.lookup("record").getType().getPrimitiveType());
            try {
                Assert.assertEquals(errMessage, symbolTable.lookup("a").getType().getPrimitiveType(), ((RecordType) recordTable.lookup("record").getType()).getMemberType("a").getPrimitiveType());
            } catch (MemberNotFoundException m) {
                Assert.fail();
            }
        } catch (VariableNotDeclaredException e) {
            Assert.fail();
        }
    }

    @Test
    public void RecordDeclarationNode_AddRecordWithRecordWithEachType_ExpectedRecordsPresentInRecordTable() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        //Declaration of variable a
        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        idNode.setType(new Type(predicateType));
        TypeNode typeNode = new TypeNode(predicateType.toString().toLowerCase());
        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        //Array used for declaring the following two Records
        ArrayList<VariableDeclarationNode> variables = new ArrayList<VariableDeclarationNode>() {
            {
                add(varNode);
            }
        };

        //Declaring the page record
        RecordDeclarationNode subRecord = new RecordDeclarationNode("page",variables);
        subRecord.accept(typeCheckVisitor);

        //Declaring the page record as a variable in order to declare another record, and add the record variable to the array
        variables.remove(0);
        SimpleIdentifierNode nameOfSubRecord = new SimpleIdentifierNode("page");
        String[] a = {"a"};
        Type[] b = {new Type(predicateType)};
        RecordType recordType = new RecordType("page",a,b);
        nameOfSubRecord.setType(recordType);
        nameOfSubRecord.setName("page");
        VariableDeclarationNode temp = new VariableDeclarationNode(nameOfSubRecord, new TypeNode(recordType.toString().toLowerCase()));
        temp.setType(new Type(Types.RECORD));
        variables.add(temp);

        //Declaring the book record
        RecordDeclarationNode node = new RecordDeclarationNode("book",variables);
        node.accept(typeCheckVisitor);

        //Creates a book variable and add the page record to the book record as its only member
        SimpleIdentifierNode record = new SimpleIdentifierNode("book");
        String[] page = {"page"};
        Type[] type = {new Type(Types.RECORD)};
        RecordType recordType1 = new RecordType("book",page,type);
        record.setType(recordType1);
        record.setName("book");
        VariableDeclarationNode book = new VariableDeclarationNode(record, new TypeNode(recordType1.toString().toLowerCase()));
        book.accept(typeCheckVisitor);

        try {
            RecordType bookType = ((RecordType)recordTable.lookup("book").getType());
            Assert.assertEquals(Types.RECORD, bookType.getPrimitiveType());
            try {
                RecordType pageType = ((RecordType)bookType.getMemberType("page"));
                Assert.assertEquals(symbolTable.lookup("a").getType().getPrimitiveType(), pageType.getMemberType("a").getPrimitiveType());
            } catch (MemberNotFoundException m) {
                Assert.fail();
            }
        } catch (VariableNotDeclaredException e) {
            Assert.fail();
        }
    }
}