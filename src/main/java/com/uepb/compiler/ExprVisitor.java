package com.uepb.compiler;

import com.uepb.ExprBaseVisitor;
import com.uepb.ExprParser.CalcContext;
import com.uepb.ExprParser.DeclaracaoContext;
import com.uepb.ExprParser.DeclaracaoVariavelContext;
import com.uepb.ExprParser.ExponenciacaoContext;
import com.uepb.ExprParser.MulDivContext;
import com.uepb.ExprParser.NumeroContext;
import com.uepb.ExprParser.ParentesesContext;
import com.uepb.ExprParser.SomaSubContext;
import com.uepb.ExprParser.UsoVariavelContext;
import com.uepb.compiler.data_structures.ScopeControl;

public class ExprVisitor extends ExprBaseVisitor<Double>{

    private final ScopeControl scopeControl = new ScopeControl();

    @Override
    public Double visitCalc(CalcContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Double visitParenteses(ParentesesContext ctx) {
        return visit(ctx.NESTED_EXPR);
    }

    @Override
    public Double visitExponenciacao(ExponenciacaoContext ctx) {
        var base = visit(ctx.BASE);
        var expoente = visit(ctx.EXPOENTE);
        return Math.pow(base, expoente);
    }

    @Override
    public Double visitMulDiv(MulDivContext ctx) {
        var o1 = visit(ctx.OPERANDO1);
        var o2 = visit(ctx.OPERANDO2);

        if(ctx.OP.getText().equals("*")){
            return o1*o2;
        }else{
            if(o2 == 0) throw new ArithmeticException("Divisão por zero!");
            return o1/o2;
        }
    }

    @Override
    public Double visitSomaSub(SomaSubContext ctx) {
        var o1 = visit(ctx.OPERANDO1);
        var o2 = visit(ctx.OPERANDO2);

        return ctx.OP.getText().equals("+") ? o1+o2 : o1-o2;
    }

    @Override
    public Double visitNumero(NumeroContext ctx) {
        var value = Double.valueOf(ctx.NUMBER().getText());
        if(ctx.SINAL != null && ctx.SINAL.getText().equals("-")){
            return -value;
        }
        return value;
    }

    @Override
    public Double visitUsoVariavel(UsoVariavelContext ctx) {
        var name = ctx.ID().getText();
        var linha = ctx.ID().getSymbol().getLine();
        var col = ctx.ID().getSymbol().getCharPositionInLine();
        var variable = scopeControl.findClosestVariable(name)
            .orElseThrow(() -> new RuntimeException(
                "Variável '%s' não declarada na linha %d e coluna %d!".formatted(name, linha, col)
            )
        );

        if(ctx.SINAL != null && ctx.SINAL.getText().equals("-")){
            return -variable.value();
        }

        return variable.value();
    }

    @Override
    public Double visitDeclaracaoVariavel(DeclaracaoVariavelContext ctx) {
        scopeControl.createNewScope();
        visit(ctx.listaDeclaracoes());
        var resultado = visit(ctx.expr());
        scopeControl.dropCurrentScope();
        return resultado;
    }

    @Override
    public Double visitDeclaracao(DeclaracaoContext ctx) {
        var nome = ctx.ID().getText();
        var valor = visit(ctx.expr());
        scopeControl.getCurrentScope().insertVariable(nome, valor);
        return valor;
    }

}
