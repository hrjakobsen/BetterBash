grammar Tesh;
import TeshTokens;

compileUnit
    : statement EOF;

statement
    : first=statement EOS second=statement                                                                              #multiStatement
    | IF expression START_BLOCK trueBranch=statement END_BLOCK ELSE START_BLOCK falseBranch=statement END_BLOCK         #ifStatement
    | WHILE expression START_BLOCK statement END_BLOCK                                                                  #whileStatement
    | FOR variablename=identifier IN array=identifier START_BLOCK statement END_BLOCK                                   #forStatement
    | identifier ASSIGN expression                                                                                      #assignmentStatement
    | arrayAccess ASSIGN expression                                                                                     #arrayElementAssignmentStatement
    | newArrayName=identifier ASSIGN SQUARE_BRACKET_START variableName=identifier IN arrayName=identifier PIPE expression SQUARE_BRACKET_END      #arrayBuilderStatement
    | identifier CHANNEL_OP SIMPLE_IDENTIFIER                                                                           #readFromChannelStatementToVariable
    | arrayAccess CHANNEL_OP SIMPLE_IDENTIFIER                                                                          #readFromChannelStatementToArray
    | SIMPLE_IDENTIFIER CHANNEL_OP expression                                                                           #writeToChannelStatement
    | RETURN expression                                                                                                 #returnStatement
    | VAR identifier ASSIGN expression                                                                                  #varStatement
    | identifier PARENTHESIS_START (expression (COMMA expression)*)? PARENTHESIS_END                                    #functionCallStatement
    | variableDeclaration (ASSIGN expression)?                                                                          #variableDeclarationStatement
    | recordDeclaration                                                                                                 #recordDeclarationStatement
    | functionDeclaration                                                                                               #functionDeclarationStatement
    | channelDeclaration                                                                                                #channelDeclarationStatement
    | identifier op=(OP_INCREMENT | OP_DECREMENT | OP_SCALE | OP_DIVIDE | OP_REM) expression                            #compoundStatement
    | arrayAccess op=(OP_INCREMENT | OP_DECREMENT | OP_SCALE | OP_DIVIDE | OP_REM) expression                           #compoundArrayStatement
    |                                                                                                                   #emptyStatement
    ;

expression
    : boolm
    ;

boolm
    : boolm op=(OP_AND | OP_OR) boolc                                                                                   #logicalComparison
    | boolc                                                                                                             #singleComparison
    ;

boolc
    : boolc op=(OP_EQ | OP_NEQ | OP_LT | OP_GT | OP_LEQ | OP_GEQ) arithmeticExpression                                  #boolComparison
    | arithmeticExpression                                                                                              #singleArithmeticExpr
    ;


arithmeticExpression
    : arithmeticExpression op=(OP_ADD | OP_SUB) term                                                                    #arithmeticExpr
    | term                                                                                                              #singleTerm
    ;

term
    : term op=(OP_MUL | OP_DIV | OP_MOD) value                                                                          #termExpr
    | value                                                                                                             #singleValue
    ;

value
    : op=(OP_ADD | OP_SUB) value                                                                                        #unaryOperator
    | NOT finalValue                                                                                                    #negateOperator
    | finalValue                                                                                                        #singleFinal
    ;

finalValue
    : constant                                                                                                          #singleConstant
    | identifier                                                                                                        #singleIdentifier
    | identifier PARENTHESIS_START (expression (COMMA expression)*)? PARENTHESIS_END                                    #functionCallExpr
    | arrayAccess                                                                                                       #arrayAccessExpr
    | PARENTHESIS_START arithmeticExpression PARENTHESIS_END                                                            #parenthesisExpr
    ;



identifier : (SIMPLE_IDENTIFIER | IDENTIFIER);

recordDeclaration: RECORD SIMPLE_IDENTIFIER START_BLOCK (variableDeclaration)+ END_BLOCK;

variableDeclaration: type SIMPLE_IDENTIFIER;

functionDeclaration: FUNCTION SIMPLE_IDENTIFIER PARENTHESIS_START (type SIMPLE_IDENTIFIER (COMMA type SIMPLE_IDENTIFIER)*)? PARENTHESIS_END type? START_BLOCK statement END_BLOCK;

channelDeclaration: CHANNEL SIMPLE_IDENTIFIER;

arrayAccess: identifier (SQUARE_BRACKET_START expression SQUARE_BRACKET_END)+;

constant: (INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL | CHAR_LITERAL | BOOL_LITERAL);

type: (SIMPLE_TYPE | RECORD SIMPLE_IDENTIFIER)ARRAY_IDENTIFIER*;


