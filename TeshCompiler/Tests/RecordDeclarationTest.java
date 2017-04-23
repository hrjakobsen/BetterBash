import com.d401f17.AST.Nodes.*;
import com.d401f17.AST.TypeSystem.*;
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
/*
    @Test
    public void RecordDeclarationNode_AddRecordWithRecordWithEachType_ExpectedRecordsPresentInRecordTable() {
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

        RecordDeclarationNode subRecord = new RecordDeclarationNode("page",variables);
        subRecord.accept(typeCheckVisitor);

        variables.remove(0);
        SimpleIdentifierNode nameOfSubRecord = new SimpleIdentifierNode("mypage");
        String[] a = {"a"};
        Type[] b = {new Type(predicateType)};
        RecordType recordType = new RecordType("page",a,b);
        nameOfSubRecord.setType(recordType);
        nameOfSubRecord.setName("mypage");
        VariableDeclarationNode temp = new VariableDeclarationNode(nameOfSubRecord, new TypeNode(recordType.toString().toLowerCase()));
        variables.add(temp);

        RecordDeclarationNode node = new RecordDeclarationNode("book",variables);
        node.accept(typeCheckVisitor);

        SimpleIdentifierNode record = new SimpleIdentifierNode("mybook");
        String[] page = {"page"};
        Type[] type = {new Type(Types.RECORD)};
        RecordType recordType1 = new RecordType("mybook",page,type);
        record.setType(recordType1);
        record.setName("mybook");
        VariableDeclarationNode book = new VariableDeclarationNode(record, new TypeNode(recordType1.toString().toLowerCase()));
        book.accept(typeCheckVisitor);

        String errMessage = predicateType + ", " + expectedType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        try {
            Assert.assertEquals(errMessage, Types.RECORD, recordTable.lookup("mybook").getType().getPrimitiveType());
            try {
                Assert.assertEquals(errMessage, symbolTable.lookup("a").getType().getPrimitiveType(), ((RecordType) recordTable.lookup("book").getType()).getMemberType("a").getPrimitiveType());
            } catch (MemberNotFoundException m) {
                Assert.fail();
            }
        } catch (VariableNotDeclaredException e) {
            Assert.fail();
        }
    }*/
}