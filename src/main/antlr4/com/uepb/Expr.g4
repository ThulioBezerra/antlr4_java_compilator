grammar Expr;

prog : expr* EOF ;

expr
    : listDeclaration ';'
    | assignment ';'
    | ifExpr
    | whileExpr
    | printExpr ';'
    | inputExpr ';'
    | block
    ;

listDeclaration
    : declaration (',' declaration)* #DeclVariavel
    ;

declaration
    : (VAR | LET | CONST) ID ('=' expression)?                     #Declaracao
    ;

assignment
    : ID '=' expression                                            #Atribuicao
    ;

ifExpr
    : IF LPAREN condition RPAREN block                             #Condicao
    ;

whileExpr
    : WHILE LPAREN condition RPAREN block                          #Loop
    ;

block
    : LBRACE expr* RBRACE                                          #Bloco
    ;

printExpr
    : PRINT LPAREN (expression | STRING) RPAREN                    #Impressao
    ;

inputExpr
    : INPUT LPAREN ID RPAREN                                       #Input
    ;

expression
    : LPAREN expression RPAREN                                     #Parenteses
    | <assoc=right> expression op=POW expression                   #Potencia
    | expression op=(MUL | DIV) expression                         #MulDiv
    | expression op=(PLUS | MINUS) expression                      #SomaSub 
    | ID                                                           #UsoVariavel
    | (PLUS | MINUS)? INT                                          #Numero
    ;

condition
    : LPAREN condition RPAREN                                      #CondParenteses
    | expression op=comparisonOperator expression                  #CondRelacional
    | (TRUE | FALSE)                                               #CondBooleano
    | condition op=AND condition                                   #CondAnd
    | condition op=OR condition                                    #CondOr
    ;

comparisonOperator
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

PLUS    : '+' ;
MINUS   : '-' ;
MUL     : '*' ;
DIV     : '/' ;
POW     : '^' ;

GT      : '>' ;
GE      : '>=' ;
LT      : '<' ;
LE      : '<=' ;
EQ      : '==' ;
NEQ     : '!=' ;

ID      : [a-zA-Z_][a-zA-Z0-9_]* ;
INT     : [0-9]+ ('.' [0-9]+)? ;
WS      : [ \t\r\n]+ -> skip ;
COMMENT : '//' ~[\r\n]* -> skip ;
STRING  : '"' .*? '"' ;
