import com.d401f17.AST.Nodes.*;

import com.d401f17.AST.TypeSystem.SymTab;
import com.d401f17.AST.TypeSystem.SymbolTable;
import com.d401f17.AST.TypeSystem.Type;
import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.Visitors.TypeCheckVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by tessa on 4/19/17.
 */
@RunWith(value = Parameterized.class)
public class ReturnNodeTest {

    @Parameterized.Parameter(value = 0)
    public Types type;

    @Parameterized.Parameter(value = 1)
    public Types expectedType;

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {Types.INT, Types.IGNORE},
                {Types.FLOAT, Types.IGNORE},
                {Types.CHAR, Types.IGNORE},
                {Types.STRING, Types.IGNORE},
                {Types.BOOL, Types.ERROR},
                {Types.ARRAY, Types.ERROR},
                {Types.CHANNEL, Types.ERROR},
                {Types.RECORD, Types.ERROR},
                {Types.FILE, Types.ERROR}
        });
    }
}