import com.d401f17.AST.Nodes.LiteralNode;
import com.d401f17.AST.Nodes.WhileNode;
import com.d401f17.AST.Nodes.StatementsNode;
import com.d401f17.TypeSystem.SymTab;
import com.d401f17.TypeSystem.SymbolTable;
import com.d401f17.TypeSystem.Types;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/11/17.
 */
@RunWith(value = Parameterized.class)
public class WhileNodeTest {

    @Parameterized.Parameter(value = 0)
    public Types predicateType;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.ERROR},
                {Types.FLOAT, Types.ERROR},
                {Types.CHAR, Types.ERROR},
                {Types.STRING, Types.ERROR},
                {Types.BOOL, Types.OK},
                {Types.ARRAY, Types.ERROR},
                {Types.CHANNEL, Types.ERROR},
                {Types.RECORD, Types.ERROR},
                {Types.FILE, Types.ERROR},
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void WhileNode_PredicateMustBeBool() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);
        WhileNode node = new WhileNode(new LiteralNode(1, predicateType), new StatementsNode(1),0);
        node.accept(typeCheckVisitor);


        String errMessage = predicateType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType().getPrimitiveType());
    }
}