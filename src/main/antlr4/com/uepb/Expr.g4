grammar Expr;

calc: expr EOF;

expr: '(' expr ')'                                              # Parenteses
    | OP=('-'|'+') expr                                         # Unario
    | OPERANDO1=expr OP=('*'|'/') OPERANDO2=expr                # MulDiv
    | OPERANDO1=expr OP=('+'|'-') OPERANDO2=expr                # SomaSub
    | NUMBER                                                    # Numero
    | ID                                                        # UsoVariavel
    | 'let' listaDeclaracoes '->' expr                          # DeclaracaoVariavel
    | <assoc=right> ID '=' expr                                 # AtribVariavel
    | 'loop' '{' LOOP_EVAL=expr '}' EXP=expr  '->' OUT=expr     # Loop
    | 'ask'                                                     # Input
    ;

listaDeclaracoes: declaracao (',' declaracao)*;

declaracao: ID '=' expr;

NEWLINE : [ \t\r\n]+ -> skip;
NUMBER     : [0-9]+('.'[0-9]+)?;
ID: [_a-zA-Z][_a-zA-Z0-9]*;