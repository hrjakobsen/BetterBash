grammar Tesh;
import TeshTokens;

compileUnit
    : statement* EOF;

statement
    : simpleStatement                                                                                                   #simpStatement
    | compoundStatement                                                                                                 #compStatement
    | NEWLINE                                                                                                           #newlineStatement
    ;

simpleStatement
    : FORK simpleStatement                                                                                              #forkSimpleStatement
    | identifier ASSIGN expression                                                                                      #assignmentStatement
    | arrayAccess ASSIGN expression                                                                                     #arrayElementAssignmentStatement
    | newArrayName=identifier ASSIGN SQUARE_BRACKET_START
          variableName=identifier IN arrayName=identifier PIPE expression SQUARE_BRACKET_END                            #arrayBuilderStatement
    | identifier CHANNEL_OP SIMPLE_IDENTIFIER                                                                           #readFromChannelStatementToVariable
    | arrayAccess CHANNEL_OP SIMPLE_IDENTIFIER                                                                          #readFromChannelStatementToArray
    | SIMPLE_IDENTIFIER CHANNEL_OP expression                                                                           #writeToChannelStatement
    | VAR identifier ASSIGN expression                                                                                  #varStatement
    | functionCall                                                                                                      #functionCallStatement
    | DOLLAR expression                                                                                                 #backgroundExecuteShellCommandStatement
    | SIMPLE_IDENTIFIER CHANNEL_OP DOLLAR expression                                                                    #executeShellCommandIntoChannelStatement
    | DOLLAR expression                                                                                                 #executeShellCommandStatement
    | variableDeclaration (ASSIGN expression)?                                                                          #variableDeclarationStatement
    //| channelDeclaration                                                                                                #channelDeclarationStatement
    | identifier op=(OP_INCREMENT | OP_DECREMENT | OP_SCALE | OP_DIVIDE | OP_REM) expression                            #compoundAssignment
    | arrayAccess op=(OP_INCREMENT | OP_DECREMENT | OP_SCALE | OP_DIVIDE | OP_REM) expression                           #compoundArrayStatement
    | flow                                                                                                              #flowStatement
    ;

flow
    : CONTINUE                                                                                                          #continueStatement
    | BREAK                                                                                                             #breakStatement
    | RETURN expression                                                                                                 #returnStatement
    ;

block:
    NEWLINE* START_BLOCK multipleStatements END_BLOCK NEWLINE*
    ;

multipleStatements
    : NEWLINE* ((statement NEWLINE) | NEWLINE)*
    ;

functionCall
    : identifier PARENTHESIS_START (expression (COMMA expression)*)? PARENTHESIS_END
    ;

compoundStatement
    : IF expression trueBranch=block ELSE falseBranch=block                                                             #ifStatement
    | WHILE expression block                                                                                            #whileStatement
    | FOR SIMPLE_IDENTIFIER IN identifier block                                                                         #forStatement
    | recordDeclaration                                                                                                 #recordDeclarationStatement
    | functionDeclaration                                                                                               #functionDeclarationStatement
    | FORK compoundStatement                                                                                            #forkCompoundStatement
    ;

expression
    : boolm
    ;

boolm
    : boolm op=(OP_AND | OP_OR) boolc                                                                                   #logicalComparison
    | boolc                                                                                                             #singleComparison
    ;

boolc
    : boolc op=(OP_EQ | OP_NEQ | OP_LT | OP_GT | OP_LEQ | OP_GEQ | OP_PAT) arithmeticExpression                         #boolComparison
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
    | functionCall                                                                                                      #functionCallExpr
    | arrayAccess                                                                                                       #arrayAccessExpr
    | PARENTHESIS_START arithmeticExpression PARENTHESIS_END                                                            #parenthesisExpr
    ;



identifier
    : (SIMPLE_IDENTIFIER | IDENTIFIER)
    ;

recordDeclaration
    : RECORD SIMPLE_IDENTIFIER NEWLINE* START_BLOCK (NEWLINE | variableDeclaration NEWLINE)* END_BLOCK
    ;

variableDeclaration
    : type SIMPLE_IDENTIFIER
    ;

functionDeclaration
    : FUNCTION name=SIMPLE_IDENTIFIER PARENTHESIS_START (type SIMPLE_IDENTIFIER (COMMA type SIMPLE_IDENTIFIER)*)? PARENTHESIS_END returntype=type block
    ;

channelDeclaration
    : CHANNEL SIMPLE_IDENTIFIER
    ;

arrayAccess
    : identifier (SQUARE_BRACKET_START expression SQUARE_BRACKET_END)+
    ;

constant
    : (INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL | CHAR_LITERAL | BOOL_LITERAL)
    ;

type
    : (SIMPLE_TYPE | RECORD SIMPLE_IDENTIFIER)ARRAY_IDENTIFIER*
    ;


