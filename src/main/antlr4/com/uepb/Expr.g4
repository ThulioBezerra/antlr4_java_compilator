grammar Expr;

calc: expr EOF;

expr: '(' NESTED_EXPR=expr ')'                      # Parenteses
    | <assoc=right> BASE=expr OP='^' EXPOENTE=expr  # Exponenciacao
    | OPERANDO1=expr OP=('*'|'/') OPERANDO2=expr    # MulDiv
    | OPERANDO1=expr OP=('+'|'-') OPERANDO2=expr    # SomaSub
    | SINAL=('+'|'-')? NUMBER                       # Numero
    | SINAL=('+'|'-')? ID                           # UsoVariavel
    | 'let' listaDeclaracoes '->' expr              # DeclaracaoVariavel
    ;

listaDeclaracoes: declaracao (',' declaracao)*;

declaracao: ID '=' expr;

NEWLINE : [ \t\r\n]+ -> skip;
NUMBER     : [0-9]+('.'[0-9]+)?;
ID: [_a-zA-Z][_a-zA-Z0-9]*;