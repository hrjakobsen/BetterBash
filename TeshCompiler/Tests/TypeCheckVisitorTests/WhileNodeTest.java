package TypeCheckVisitorTests;

import com.d401f17.AST.Nodes.LiteralNode;
import com.d401f17.AST.Nodes.WhileNode;
import com.d401f17.AST.Nodes.StatementsNode;
import com.d401f17.SymbolTable.SymTab;
import com.d401f17.SymbolTable.SymbolTable;
import com.d401f17.TypeSystem.*;
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
    public Type predicateType;

    @Parameterized.Parameter(value = 1)
    public Type expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new IntType(), new ErrorType()},
                {new FloatType(), new ErrorType()},
                {new CharType(), new ErrorType()},
                {new StringType(), new ErrorType()},
                {new BoolType(), new OkType()},
                {new ArrayType(), new ErrorType()},
                {new ChannelType(), new ErrorType()},
                {new BinFileType(), new ErrorType()},
                {new TextFileType(), new ErrorType()},
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
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }
}