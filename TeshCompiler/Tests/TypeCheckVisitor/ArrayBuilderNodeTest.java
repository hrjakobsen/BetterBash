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
 * Created by tessa on 4/20/17.
 */
@RunWith(value = Parameterized.class)
public class ArrayBuilderNodeTest {

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
                {Types.STRING, Types.STRING},
                {Types.BOOL, Types.ERROR},
                {Types.ARRAY, Types.ERROR},
                {Types.CHANNEL, Types.ERROR},
                {Types.RECORD, Types.ERROR},
                {Types.FILE, Types.ERROR},

        });
    }

    @Test
    //Hvilken class skal testes, hvad skal ske, hvad vi forventer at få
    public void ArrayBuilderNode_typeCheckWithParameters_expected() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);
      //  ArrayBuilderNode node = new ArrayBuilderNode(new SimpleIdentifierNode("node", ));
       // node.accept(typeCheckVisitor);

        String errMessage = predicateType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
      //Assert.assertEquals(errMessage, expectedType, node.getType().getPrimitiveType());
    }
}