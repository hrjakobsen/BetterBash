grammar Tesh;

import TeshTokens;

prog
    : definition* ;
definition
    : record
    | function
    | statement
    ;

record
    : RECORD_DECLARATION SIMPLE_IDENTIFIER START_BLOCK varDef* END_BLOCK  #recordID
    ;

varDef
    : type identifierDeclaration (ASSIGN valueStatement)?
    ;

function
    : FUNCTION_DECLARATION SIMPLE_IDENTIFIER START_PAR formalArguments* END_PAR type? START_BLOCK statement* END_BLOCK;

formalArguments
    : formalArgument (COMMA formalArgument)*
    ;

formalArgument
    : type SIMPLE_IDENTIFIER
    ;

type
    : arrayType
    | simpleType
    ;
arrayType
    : simpleType ARRAY_IDENTIFIER*
    ;



simpleType
    : INT_DECLARATION
    | FLOAT_DECLARATION
    | CHAR_DECLARATION
    | STRING_DECLARATION
    | BOOL_DECLARATION
    | RECORD_DECLARATION identifierDeclaration
    ;


statement
    : varDef
    | valueStatement
    | identifier ASSIGN valueStatement
    | controlStructure
    ;

controlStructure
    : ifStatement
    | whileLoop
    | forLoop
    ;

whileLoop
    : WHILE valueStatement START_BLOCK statement* END_BLOCK
    ;

forLoop
    : FOR varDef COMMA valueStatement COMMA (varDef|valueStatement|identifier ASSIGN valueStatement) START_BLOCK statement* END_BLOCK
    ;

ifStatement
    : IF valueStatement START_BLOCK statement* END_BLOCK (ELSEIF valueStatement START_BLOCK statement* END_BLOCK)* (ELSE START_BLOCK statement* END_BLOCK)?
    ;

functionCall
    : identifier START_PAR actualArguments* END_PAR
    ;

valueStatement
    : functionCall
    | math LESS_THAN  valueStatement
    | math GREATER_THAN  valueStatement
    | math EQUAL valueStatement
    | math GREATER_OR_EQUAL valueStatement
    | math LESS_OR_EQUAL valueStatement
    | math NOT_EQUAL valueStatement
    | math
    | arrayLiteral
    ;

math
    : term ADD valueStatement
    | term SUB valueStatement
    | term
    ;


term
    : constant DIV term
    | constant MULT term
    | constant
    ;

constant
    : identifier arrayAccess*
    | START_PAR valueStatement END_PAR
    | intLiteral | floatLiteral | stringLiteral | charLiteral | boolLiteral
    ;

arrayAccess
    : START_ARR identifier END_ARR
    ;

arrayLiteral
    : START_ARR type identifierDeclaration IN identifier (WHERE valueStatement)? END_ARR
    | START_ARR (valueStatement (COMMA valueStatement)*)? END_ARR
    ;

intLiteral: INT_LITERAL;
floatLiteral: FLOAT_LITERAL;
stringLiteral: STRING_LITERAL;
charLiteral: CHAR_LITERAL;
boolLiteral: BOOL_LITERAL;


identifierDeclaration
    : SIMPLE_IDENTIFIER
    ;

identifier
    : SIMPLE_IDENTIFIER arrayAccess*
    | SIMPLE_IDENTIFIER (DOT SIMPLE_IDENTIFIER)*
    ;


actualArguments
    : actualArgument (COMMA actualArgument)*
    ;

actualArgument
    : identifier
    | valueStatement
    ;
