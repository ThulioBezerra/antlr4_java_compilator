package com.uepb.compiler;

import com.uepb.ExprBaseVisitor;
import com.uepb.ExprParser.CalcContext;
import com.uepb.ExprParser.ExponenciacaoContext;
import com.uepb.ExprParser.MulDivContext;
import com.uepb.ExprParser.NumeroContext;
import com.uepb.ExprParser.ParentesesContext;
import com.uepb.ExprParser.SomaSubContext;

public class ExprVisitor extends ExprBaseVisitor<Double>{

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
        return Double.valueOf(ctx.NUMBER().getText());
    }

}
