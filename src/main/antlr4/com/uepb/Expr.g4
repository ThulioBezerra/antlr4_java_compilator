grammar Expr;

prog : expr* EOF ;

expr
    : listaDeclaracao ';'
    | atribuicao ';'
    | condicional
    | loopWhile
    | impressao ';'
    | entrada ';'
    ;

listaDeclaracao
    : declaracao (',' declaracao)*                               #DeclVariavel
    ;

declaracao
    : (VAR | LET | CONST) ID ('=' expressao)?                     #Declaracao
    ;

atribuicao
    : ID '=' expressao                                            #Atribuicao
    ;

condicional
    : IF LPAREN condicao RPAREN LBRACE expr* RBRACE               #Condicao
    | ELSE LBRACE expr* RBRACE                                    #naoCondicao
    ;

loopWhile
    : WHILE LPAREN condicao RPAREN LBRACE expr* RBRACE            #Loop
    ;

impressao
    : PRINT LPAREN (expressao | STRING) RPAREN                    #Impressao
    ;

entrada
    : INPUT LPAREN ID RPAREN                                       #Input
    ;

expressao
    : LPAREN expressao RPAREN                                    #Parenteses
    | <assoc=right> expressao op=EXP expressao                   #Potencia
    | expressao op=(MUL | DIV) expressao                         #MulDiv
    | expressao op=(SOM | SUB) expressao                         #SomaSub 
    | ID                                                         #UsoVariavel
    | (SOM | SUB)? NUMBER                                        #Numero
    ;

condicao
    : LPAREN condicao RPAREN                                     #CondParenteses
    | expressao op=operadoresCondicionais expressao              #CondRelacional
    | (TRUE | FALSE)                                             #CondBooleano
    | condicao op=AND condicao                                   #CondAnd
    | condicao op=OR condicao                                    #CondOr
    ;

operadoresCondicionais
    : GT | GE | LT | LE | EQ | NEQ
    ;

VAR     : 'var' ;
LET     : 'let' ;
CONST   : 'const' ;
IF      : 'if' ;
WHILE   : 'while' ;
PRINT   : 'print' ;
INPUT   : 'input' ;
AND     : 'and' ;
OR      : 'or' ;
TRUE    : 'true' ;
FALSE   : 'false' ;

LPAREN  : '(' ;
RPAREN  : ')' ;
LBRACE  : '{' ;
RBRACE  : '}' ;

SOM    : '+' ;
SUB   : '-' ;
MUL     : '*' ;
DIV     : '/' ;
EXP     : '^' ;

GT      : '>' ;
GE      : '>=' ;
LT      : '<' ;
LE      : '<=' ;
EQ      : '==' ;
NEQ     : '!=' ;

ID      : [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER     : [0-9]+ ('.' [0-9]+)? ;
WS      : [ \t\r\n]+ -> skip ;
COMMENT : '//' ~[\r\n]* -> skip ;
STRING  : '"' .*? '"' ;
