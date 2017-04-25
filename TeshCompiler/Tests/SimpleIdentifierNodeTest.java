import com.d401f17.AST.Nodes.SimpleIdentifierNode;
import com.d401f17.AST.Nodes.TypeNode;
import com.d401f17.AST.Nodes.VariableDeclarationNode;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/20/17.
 */
@RunWith(value = Parameterized.class)
public class SimpleIdentifierNodeTest {

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
    public void SimpleIdentifierNode_SymbolsPresent_expected() {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        SimpleIdentifierNode idNode = new SimpleIdentifierNode("a");
        idNode.setType(new Type(predicateType));
        TypeNode typeNode = new TypeNode(predicateType.toString().toLowerCase());

        VariableDeclarationNode varNode = new VariableDeclarationNode(idNode, typeNode);
        varNode.accept(typeCheckVisitor);

        String errMessage = predicateType + ", " + expectedType + " => " + expectedType + "\n" + typeCheckVisitor.getAllErrors();
        try {
            Assert.assertEquals(errMessage, expectedType, symbolTable.lookup("a").getType().getPrimitiveType());
        } catch (VariableNotDeclaredException e) {
            Assert.fail();
        }
    }

    @Test
    public void SimpleIdentifierNode_SymbolsNotPresent_expected() throws VariableNotDeclaredException {
        SymTab symbolTable = new SymbolTable();
        SymTab recordTable = new SymbolTable();
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable, recordTable);

        exception.expect(VariableNotDeclaredException.class);
        symbolTable.lookup("a");
    }
}