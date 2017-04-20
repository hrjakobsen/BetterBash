import com.d401f17.AST.Nodes.SimpleIdentifierNode;
import com.d401f17.AST.Nodes.TypeNode;
import com.d401f17.AST.Nodes.VariableDeclarationNode;
import com.d401f17.AST.TypeSystem.*;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/20/17.
 */
@RunWith(value = Parameterized.class)
public class SimpleIdentifierNodeTest {

  @Parameterized.Parameter(value = 0)
  public Types predicateType;

  @Parameterized.Parameter(value = 1)
  public Types expectedType;

  @Parameterized.Parameters
  public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                    {Types.INT, Types.OK},
                    {Types.FLOAT, Types.OK},
                    {Types.CHAR, Types.OK},
                    {Types.STRING, Types.OK},
                    {Types.BOOL, Types.OK},
                    {Types.ARRAY, Types.OK},
                    {Types.CHANNEL, Types.OK},
                    {Types.RECORD, Types.OK},
                    {Types.FILE, Types.OK},

        });

        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        idNode.setType(new Type(predicateType));
        TypeNode typeNode = new TypeNode(predicateType.toString().toLowerCase());

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);
        try {
            Assert.assertEquals(expectedType, symbolTable.lookup("a").getType().getPrimitiveType());
        } catch (VariableNotDeclaredException) {

      }
  }

}
