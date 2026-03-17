grammar Expr;

prog : expr* EOF ;

expr
    : listaDeclaracao ';'
    | assignment ';'
    | ifExpr
    | whileExpr
    | printExpr ';'
    | inputExpr ';'
    | block
    ;

listDeclaration
    : declaration (',' declaration)*                           #DeclVariavel


declaration
    : (VAR | LET | CONST) ID ('=' expression)?                 #Declaracao
    ;

assignment
    : ID '=' expression                                        #Atribuicao
    ;

ifExpr
    : IF LPAREN condition RPAREN block                         #Condicao
    ;

whileExpr
    : WHILE LPAREN condition RPAREN block                      #Loop
    ;

block
    : LBRACE expr* RBRACE                                      #Bloco
    ;

printExpr
    : PRINT LPAREN expression RPAREN                           #Impressao
    ;

inputExpr
    : INPUT LPAREN ID RPAREN                                   #Input
    ;

expression
    : LPAREN expression RPAREN                                  #Parenteses
    | expression OP=('^') expression                            #Potencia
    | expression OP=('*' | '/') expression                      #MulDiv
    | expression OP=('+' | '-') expression                      #SomaSub 
    | ID                                                        #UsoVariavel
    | SIGN=('+'|'-') INT                                        #Numero
    ;

condition
    : LPAREN condition RPAREN                   
    | expression comparisonOperator expression  
    | (TRUE | FALSE)                                                                   
    | condition AND condition      
    | condition OR condition       
    ;

comparisonOperator
    : ('>' | '>=' | '<' | '<=' | '==' | '!=')
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

ID      : [a-zA-Z_][a-zA-Z0-9_]* ;
INT  : [0-9]+ ('.' [0-9]+)? ;
WS      : [ \t\r\n]+ -> skip ;
COMMENT : '//' ~[\r\n]* -> skip ;