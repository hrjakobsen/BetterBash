grammar Tesh;
import TeshTokens;

compileUnit
    : statement EOF;

statement
    : first=statement EOS second=statement                                                                              #multiStatement
    | IF expression START_BLOCK statement END_BLOCK ELSE START_BLOCK statement END_BLOCK                                #ifStatement
    | WHILE expression START_BLOCK statement END_BLOCK                                                                  #whileStatement
    | FOR identifier IN identifier START_BLOCK statement END_BLOCK                                                      #forStatement
    | (identifier | arrayAccess) ASSIGN expression                                                                      #assignmentStatement
    | identifier ASSIGN SQUARE_BRACKET_START identifier IN identifier PIPE expression SQUARE_BRACKET_END                #arrayBuilderStatement
    | identifier CHANNEL_OP SIMPLE_IDENTIFIER                                                                           #writeToChannelStatement
    | SIMPLE_IDENTIFIER CHANNEL_OP expression                                                                           #readFromChannelStatement
    | RETURN expression                                                                                                 #returnStatement
    | VAR identifier ASSIGN expression                                                                                  #varStatement
    | identifier PARENTHESIS_START (expression (COMMA expression)*)? PARENTHESIS_END                                    #functionCallStatement
    | variableDelcaration (ASSIGN expression)?                                                                          #variableDelcarationStatement
    | recordDeclaration                                                                                                 #recordDeclarationStatement
    | functionDeclaration                                                                                               #functionDeclarationStatement
    | channelDeclaration                                                                                                #channelDeclarationStatement
    | (identifier | arrayAccess)(OP_INCREMENT | OP_DECREMENT | OP_SCALE | OP_DIVIDE | OP_REM) expression                #compoundStatement
    |                                                                                                                   #emptyStatement
    ;

expression
    : boolm
    ;

boolm
    : boolm (OP_AND | OP_OR) boolc                                                                                      #logicalComparison
    | boolc                                                                                                             #singleComparison
    ;

boolc
    : boolc (OP_EQ | OP_NEQ | OP_LT | OP_GT | OP_LEQ | OP_GEQ) arithmeticExpression                                     #boolComparison
    | arithmeticExpression                                                                                              #singleArithmeticExpr
    ;


arithmeticExpression
    : arithmeticExpression (OP_ADD | OP_SUB) term                                                                       #arithmeticExpr
    | term                                                                                                              #singleTerm
    ;

term
    : term (OP_MUL | OP_DIV | OP_MOD) value                                                                             #termExpr
    | value                                                                                                             #singleValue
    ;

value
    : (OP_ADD | OP_SUB) value                                                                                           #unaryOperator
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

recordDeclaration: RECORD SIMPLE_IDENTIFIER START_BLOCK (variableDelcaration)+ END_BLOCK;

variableDelcaration: type SIMPLE_IDENTIFIER;

functionDeclaration: FUNCTION SIMPLE_IDENTIFIER PARENTHESIS_START (type SIMPLE_IDENTIFIER (COMMA type SIMPLE_IDENTIFIER)*)? PARENTHESIS_END type? START_BLOCK statement END_BLOCK;

channelDeclaration: CHANNEL SIMPLE_IDENTIFIER;

arrayAccess: identifier (SQUARE_BRACKET_START expression SQUARE_BRACKET_END)+;

constant: (INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL | CHAR_LITERAL | BOOL_LITERAL);

type: (SIMPLE_TYPE | RECORD SIMPLE_IDENTIFIER)ARRAY_IDENTIFIER*;