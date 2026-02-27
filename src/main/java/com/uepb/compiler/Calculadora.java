package com.uepb.compiler;

import com.uepb.ExprBaseVisitor;
import com.uepb.ExprParser.DeclVariavelContext;
import com.uepb.ExprParser.DeclaracaoContext;
import com.uepb.ExprParser.ExponenciacaoContext;
import com.uepb.ExprParser.MulDivContext;
import com.uepb.ExprParser.NumeroContext;
import com.uepb.ExprParser.ParentesesContext;
import com.uepb.ExprParser.ProgContext;
import com.uepb.ExprParser.SomaSubContext;
import com.uepb.ExprParser.UsoVariavelContext;

public class Calculadora extends ExprBaseVisitor<Double>{
    
    private final ScopeControl scopes = new ScopeControl();

    @Override
    public Double visitProg(ProgContext ctx) {
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
        var o1 = visit(ctx.O1);
        var o2 = visit(ctx.O2);
        var operador = ctx.OP.getText();

        if(operador.equals("/")){
            if(o2 == 0) throw new ArithmeticException("Divisão por zero!");

            return o1/o2;
        }else{
            return o1*o2;
        }
    }

    @Override
    public Double visitSomaSub(SomaSubContext ctx) {
        var o1 = visit(ctx.O1);
        var o2 = visit(ctx.O2);
        var operador = ctx.OP.getText();

        return operador.equals("+") ? o1+o2 : o1-o2;
    }

    @Override
    public Double visitNumero(NumeroContext ctx) {
        var numeroStr = ctx.NUMBER().getText();
        var sinal = ctx.SINAL;

        if(sinal != null && sinal.getText().equals("-")){
            return Double.valueOf(numeroStr) * (-1);
        }

        return Double.valueOf(numeroStr);
    }

    @Override
    public Double visitUsoVariavel(UsoVariavelContext ctx) {
        var nomeVar = ctx.ID().getText();
        var tk = ctx.ID().getSymbol();
        var sinal = ctx.SINAL;
        var declaracaoOpt = scopes.lookup(nomeVar);

        if(declaracaoOpt.isEmpty()){
            throw new RuntimeException(
                "A variavel '%s' não foi declarada na linha %d e coluna %d."
                .formatted(nomeVar,tk.getLine(),tk.getCharPositionInLine())
            );
        }

        var valor = declaracaoOpt.get().value();

        if(sinal != null && sinal.getText().equals("-")){
            return -valor;
        }

        return valor;
    }

    @Override
    public Double visitDeclVariavel(DeclVariavelContext ctx) {
        scopes.createScope();
        visit(ctx.listaDeclaracao());
        var valor = visit(ctx.expr());
        scopes.dropScope();

        return valor;
    }

    @Override
    public Double visitDeclaracao(DeclaracaoContext ctx) {
        var varName = ctx.ID().getText();
        var tk = ctx.ID().getSymbol();
        var currentScope = scopes.getCurrentScope();

        if(currentScope.exists(varName)){
            throw new RuntimeException(
                "A variavel '%s' presente na linha %d e coluna %d ja foi declarada."
                .formatted(varName,tk.getLine(),tk.getCharPositionInLine())
            );
        }

        var valor = visit(ctx.expr());
        currentScope.insert(varName, valor);

        return null;
    }

}
