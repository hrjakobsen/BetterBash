lexer grammar TeshTokens;

RECORD_DECLARATION: 'record';
FUNCTION_DECLARATION: 'func';
WHILE: 'while';
FOR: 'for';
IF: 'if';
ELSEIF: 'else if';
ELSE: 'else';

IN: 'in';
WHERE: '|';



INT_LITERAL: [0-9]+;
STRING_LITERAL :  '"' (ESC | ~["\\])* '"' ;


fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;

LINE_COMMENT : '#' ~'\n'* '\n' -> channel(HIDDEN) ;

FLOAT_LITERAL: [0-9]+'.'[0-9]+;
CHAR_LITERAL: '\''[a-zA-Z0-9]'\'';
BOOL_LITERAL: 'true' | 'false';

INT_DECLARATION: 'int';
FLOAT_DECLARATION: 'float';
CHAR_DECLARATION: 'char';
STRING_DECLARATION: 'string';
BOOL_DECLARATION: 'bool';

SIMPLE_IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*;
WS :  [ \r\n]+ -> skip ; // skip spaces, tabs, newlines
EOS : [;] ;

START_BLOCK: '{';
END_BLOCK: '}';
START_PAR: '(';
END_PAR: ')';

ASSIGN: '=';


COMMA: ',';

START_ARR: '[';
END_ARR : ']';

ARRAY_IDENTIFIER: START_ARR END_ARR;


LESS_THAN: '<';
GREATER_THAN: '>';
LESS_OR_EQUAL: '<=';
GREATER_OR_EQUAL: '>=';
EQUAL: '==';
NOT_EQUAL: '!=';

ADD: '+';
SUB: '-';
MULT: '*';
DIV: '/';

DOT: '.';
