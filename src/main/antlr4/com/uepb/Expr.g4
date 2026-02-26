grammar Expr;

prog: expr EOF;
expr: '(' NESTED_EXPR=expr ')'                          #Parenteses
    | <assoc=right> BASE=expr OP='^' EXPOENTE=expr      #Exponenciacao
    | O1=expr OP=('*'|'/') O2=expr                      #MulDiv
    | O1=expr OP=('+'|'-') O2=expr                      #SomaSub
    | SINAL=('+'|'-')? NUMBER                           #Numero
    | SINAL=('+'|'-')? ID                                #UsoVariavel
    | 'let' listaDeclaracao '->' expr                   #DeclVariavel
;

listaDeclaracao: declaracao (',' declaracao)*;

declaracao: ID '=' expr;

NUMBER: [0-9]+('.'[0-9]+)?;
ID: [a-zA-Z_][a-zA-Z_0-9]*;
PALAVRA_CHAVE: 'while' | 'if' | 'print';
WS: [ \t\r\n]+ -> skip;