grammar Tesh;
import TeshTokens;

compileUnit
    : statement EOF;

statement
    : statement EOS statement
    | IF expression START_BLOCK statement END_BLOCK ELSE START_BLOCK statement END_BLOCK
    | WHILE expression START_BLOCK statement END_BLOCK
    | FOR identifier IN identifier START_BLOCK statement END_BLOCK
    | identifier ASSIGN expression
    | arrayAccess ASSIGN expression
    | identifier ASSIGN SQUARE_BRACKET_START identifier IN identifier PIPE expression SQUARE_BRACKET_END
    | identifier CHANNEL_OP SIMPLE_IDENTIFIER
    | SIMPLE_IDENTIFIER CHANNEL_OP expression
    | RETURN identifier
    | VAR identifier ASSIGN expression
    | identifier PARENTHESIS_START (expression (COMMA expression)*)? PARENTHESIS_END
    | variableDelcaration (ASSIGN expression)?
    | recordDeclaration
    | functionDeclaration
    | channelDeclaration
    | identifier (OP_INCREMENT | OP_DECREMENT)
    |
    ;

expression
    : identifier
    | identifier PARENTHESIS_START (expression (COMMA expression)*)? PARENTHESIS_END
    | arrayAccess
    | PARENTHESIS_START expression PARENTHESIS_END
    | NOT expression
    | expression (OP_MUL | OP_DIV | OP_MOD) expression
    | expression (OP_ADD | OP_SUB) expression
    | expression (OP_EQ | OP_NEQ | OP_LT | OP_GT | OP_LEQ | OP_GEQ) expression
    | expression (OP_AND | OP_OR) expression
    | constant
    ;

identifier
    : SIMPLE_IDENTIFIER
    | IDENTIFIER
    ;

recordDeclaration: RECORD SIMPLE_IDENTIFIER START_BLOCK (variableDelcaration)+ END_BLOCK;

variableDelcaration: type SIMPLE_IDENTIFIER;

functionDeclaration: FUNCTION SIMPLE_IDENTIFIER PARENTHESIS_START (type SIMPLE_IDENTIFIER (COMMA type SIMPLE_IDENTIFIER)*)? PARENTHESIS_END type? START_BLOCK statement END_BLOCK;

channelDeclaration: CHANNEL SIMPLE_IDENTIFIER;

arrayAccess
    : identifier (SQUARE_BRACKET_START expression SQUARE_BRACKET_END)+
    ;

constant
    : INT_LITERAL
    | FLOAT_LITERAL
    | STRING_LITERAL
    | CHAR_LITERAL
    ;

type
    : (SIMPLE_TYPE | RECORD SIMPLE_IDENTIFIER)ARRAY_IDENTIFIER*;
