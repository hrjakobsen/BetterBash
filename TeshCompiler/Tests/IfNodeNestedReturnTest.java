import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.SymTab;
import com.d401f17.TypeSystem.SymbolTable;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by hense on 4/12/17.
 */
@RunWith(value = Parameterized.class)
public class IfNodeNestedReturnTest {
    @Parameterized.Parameter(value = 0)
    public Type type1;

    @Parameterized.Parameter(value = 1)
    public Type type2;

    @Parameterized.Parameter(value = 2)
    public Type type3;

    @Parameterized.Parameter(value = 3)
    public Type expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new StringType(), new StringType(), new StringType(), new StringType() },
                {new IntType(), new StringType(), new StringType(), new IgnoreType() },
                {new StringType(), new IntType(), new StringType(), new IgnoreType() },
                {new StringType(), new StringType(), new IntType(), new IgnoreType() },
                {new IntType(), new IntType(), new IntType(), new IntType() },
                {new VoidType(), new IntType(), new IntType(), new IgnoreType() },
                {new VoidType(), new VoidType(), new IntType(), new IgnoreType() },
                {new VoidType(), new VoidType(), new VoidType(), new VoidType() },
                {new CharType(), new IntType(), new IntType(), new IgnoreType() },
        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void IfNode_NestedReturns_TypesPropagate() {
        /*
        1   if true {
        2       if false {
        3           return type1
        4       } else {
        5           return type2
        6       }
        7   } else {
        8       return type3
        9   }
         */

        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);
        StatementNode node = new StatementsNode(1,
            new IfNode(
                new LiteralNode(true, new BoolType()),
                    new StatementsNode(2,
                        new IfNode (
                            new LiteralNode(false, new BoolType()),
                                new StatementsNode(3, new ReturnNode(new LiteralNode(0, type1),3)),
                                    new StatementsNode(5, new ReturnNode(new LiteralNode(0, type2),5)),2)),
                                new StatementsNode(8,
                                    new StatementsNode(8,
                                        new ReturnNode(new LiteralNode(0, type3),8))
                                    ), 1)
        );

        node.accept(typeCheckVisitor);

        String errMessage = type1 + ", " + type2 + ", " + type3 + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        Assert.assertEquals(errMessage, expectedType, node.getType());
    }
}
