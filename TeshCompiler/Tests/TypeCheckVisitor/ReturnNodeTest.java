import com.d401f17.AST.Nodes.*;

import com.d401f17.AST.TypeSystem.SymTab;
import com.d401f17.AST.TypeSystem.SymbolTable;
import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/19/17.
 */
@RunWith(value = Parameterized.class)
public class ReturnNodeTest {

    @Parameterized.Parameter(value = 0)
    public Types predicateType;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.INT},
                {Types.FLOAT, Types.FLOAT},
                {Types.CHAR, Types.CHAR},
                {Types.STRING, Types.STRING},
                {Types.BOOL, Types.BOOL},
                {Types.ARRAY, Types.ARRAY},
                {Types.CHANNEL, Types.CHANNEL},
                {Types.RECORD, Types.RECORD},
                {Types.FILE, Types.FILE},
                {Types.IGNORE, Types.IGNORE},
                {Types.ERROR, Types.IGNORE},
                {Types.OK, Types.OK}
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ShellNode_typeCheckWithParameters_expected() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);
        ReturnNode node = new ReturnNode(new LiteralNode(0, predicateType));
        node.accept(typeCheckVisitor);

        String errMessage = predicateType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType().getPrimitiveType());
    }
}