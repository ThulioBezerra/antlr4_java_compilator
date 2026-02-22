grammar Expr;

calc: expr EOF;
expr: '(' NESTED_EXPR=expr ')'                      # Parenteses
    | <assoc=right> BASE=expr OP='^' EXPOENTE=expr  # Exponenciacao
    | OPERANDO1=expr OP=('*'|'/') OPERANDO2=expr    # MulDiv
    | OPERANDO1=expr OP=('+'|'-') OPERANDO2=expr    # SomaSub
    | NUMBER                                        # Numero
    ;

NEWLINE : [ \t\r\n]+ -> skip;
NUMBER     : [0-9]+('.'[0-9]+)?;