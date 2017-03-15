lexer grammar TeshTokens;

LINE_COMMENT : '#' ~[\r\n]* -> skip ;

IN: 'in';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
RETURN: 'return';
FOR: 'for';
VAR: 'var';
RECORD: 'record';
FUNCTION: 'func';
CHANNEL: 'channel';
START_BLOCK: '{';
END_BLOCK: '}';
ARRAY_IDENTIFIER:'[]';
SQUARE_BRACKET_START: '[';
SQUARE_BRACKET_END: ']';
CHANNEL_OP: '<<';
NOT: '!';
PARENTHESIS_START:'(';
PARENTHESIS_END:')';
OP_INCREMENT: '+=';
OP_DECREMENT: '-=';
OP_SCALE: '*=';
OP_DIVIDE: '/=';
OP_REM: '/=';
OP_ADD: '+';
OP_SUB: '-';
OP_MUL: '*';
OP_DIV: '/';
OP_AND: '&&';
OP_OR: '||';
OP_EQ: '==';
OP_NEQ: '!=';
OP_LT: '<';
OP_GT: '>';
OP_GEQ: '>=';
OP_LEQ: '<=';
OP_MOD: 'mod';
ASSIGN: '=';
PIPE: '|';
COMMA: ',';
SIMPLE_TYPE: ('string'|'int'|'float'|'char'|'bool');
CHAR_LITERAL: '\''.?'\'';
BOOL_LITERAL: 'true' | 'false';

STRING_LITERAL :  '"' (ESC | ~["\\])* '"' ;

fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;


FLOAT_LITERAL
    :    INT_LITERAL '.' INT_LITERAL EXP?   // 1.35, 1.35E-9, 0.3, -4.5
    |    INT_LITERAL EXP            // 1e10 -3e4
    |    INT_LITERAL                // -3, 45
    ;

INT_LITERAL :   '0' | [1-9] [0-9]* ; // no leading zeros
fragment EXP :   [Ee] [+\-]? INT_LITERAL ;




IDENTIFIER: SIMPLE_IDENTIFIER('.'SIMPLE_IDENTIFIER)+;

SIMPLE_IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*;


WS :  [ \r\t]+ -> skip ; // skip spaces, tabs, newlines

EOS: [\n];
