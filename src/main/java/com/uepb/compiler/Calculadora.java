package com.uepb.compiler;

import com.uepb.ExprBaseVisitor;
import com.uepb.ExprParser;
import com.uepb.ExprParser.AtribuicaoContext;
import com.uepb.ExprParser.BlocoContext;
import com.uepb.ExprParser.CondAndContext;
import com.uepb.ExprParser.CondBooleanoContext;
import com.uepb.ExprParser.CondOrContext;
import com.uepb.ExprParser.CondParentesesContext;
import com.uepb.ExprParser.CondRelacionalContext;
import com.uepb.ExprParser.CondicaoContext;
import com.uepb.ExprParser.DeclVariavelContext;
import com.uepb.ExprParser.DeclaracaoContext;
import com.uepb.ExprParser.ImpressaoContext;
import com.uepb.ExprParser.InputContext;
import com.uepb.ExprParser.LoopContext;
import com.uepb.ExprParser.MulDivContext;
import com.uepb.ExprParser.NumeroContext;
import com.uepb.ExprParser.ParentesesContext;
import com.uepb.ExprParser.PotenciaContext;
import com.uepb.ExprParser.ProgContext;
import com.uepb.ExprParser.SomaSubContext;
import com.uepb.ExprParser.UsoVariavelContext;

public class Calculadora extends ExprBaseVisitor<Void> {

    private final ScopeControl scopes = new ScopeControl();
    private final MemoryMapper mapper = new MemoryMapper();
    private final StringBuilder code = new StringBuilder();
    private int label = 0;

    private String createLabel() {
        var labelGerada = "L" + String.valueOf(label);
        label++;
        return labelGerada;
    }

    public String getCode() {
        return code.toString();
    }

    @Override
    public Void visitProg(ProgContext ctx) {
        for (var expressao : ctx.expr()) {
            visit(expressao);
        }
        code.append("out\n");
        code.append("hlt\n");
        return null;
    }

    @Override
    public Void visitParenteses(ParentesesContext ctx) {
        visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitPotencia(PotenciaContext ctx) {
        var baseAddr = mapper.alloc();
        var expAddr = mapper.alloc();
        var resAddr = mapper.alloc();

        var startLoop = createLabel();
        var endLoop = createLabel();

        code.append("push $").append(baseAddr).append("\n");
        visit(ctx.expression(0));
        code.append("sto\n");

        code.append("push $").append(expAddr).append("\n");
        visit(ctx.expression(1));
        code.append("sto\n");

        code.append("push $").append(resAddr).append("\n");
        code.append("push 1\n");
        code.append("sto\n");

        code.append(startLoop).append(":\n");

        code.append("push $").append(expAddr).append("\n");
        code.append("lod\n");
        code.append("push 0\n");
        code.append("grt\n");
        code.append("fjp ").append(endLoop).append("\n");

        code.append("push $").append(resAddr).append("\n");
        code.append("push $").append(resAddr).append("\n");
        code.append("lod\n");
        code.append("push $").append(baseAddr).append("\n");
        code.append("lod\n");
        code.append("mul\n");
        code.append("sto\n");

        code.append("push $").append(expAddr).append("\n");
        code.append("push $").append(expAddr).append("\n");
        code.append("lod\n");
        code.append("push 1\n");
        code.append("sub\n");
        code.append("sto\n");

        code.append("ujp ").append(startLoop).append("\n");

        code.append(endLoop).append(":\n");

        code.append("push $").append(resAddr).append("\n");
        code.append("lod\n");

        return null;
    }

    @Override
    public Void visitMulDiv(MulDivContext ctx) {
        visit(ctx.expression(0));
        visit(ctx.expression(1));

        if (ctx.op.getType() == ExprParser.DIV) {
            code.append("div\n");
        } else {
            code.append("mul\n");
        }
        return null;
    }

    @Override
    public Void visitSomaSub(SomaSubContext ctx) {
        visit(ctx.expression(0));
        visit(ctx.expression(1));

        if (ctx.op.getType() == ExprParser.PLUS) {
            code.append("add\n");
        } else {
            code.append("sub\n");
        }
        return null;
    }

    @Override
    public Void visitNumero(NumeroContext ctx) {
        var numeroStr = ctx.INT().getText();

        if (ctx.MINUS() != null) {
            code.append("push -1\n");
            code.append("push ").append(numeroStr).append("\n");
            code.append("mul\n");
        } else {
            code.append("push ").append(numeroStr).append("\n");
        }
        return null;
    }

    @Override
    public Void visitUsoVariavel(UsoVariavelContext ctx) {
        var nomeVar = ctx.ID().getText();
        var tk = ctx.ID().getSymbol();
        var declaracaoOpt = scopes.lookup(nomeVar);

        if (declaracaoOpt.isEmpty()) {
            throw new RuntimeException(
                    "A variavel '%s' não foi declarada na linha %d e coluna %d."
                            .formatted(nomeVar, tk.getLine(), tk.getCharPositionInLine()));
        }

        var address = declaracaoOpt.get().address();
        code.append("push $").append(address).append("\n");
        code.append("lod\n");

        return null;
    }

    @Override
    public Void visitDeclVariavel(DeclVariavelContext ctx) {
        for (var decl : ctx.declaration()) {
            visit(decl);
        }
        return null;
    }

    @Override
    public Void visitDeclaracao(DeclaracaoContext ctx) {
        var varName = ctx.ID().getText();
        boolean isConst = ctx.start.getText().equals("const");
        var tk = ctx.ID().getSymbol();
        var currentScope = scopes.getCurrentScope();
        var address = mapper.alloc();

        if (currentScope.exists(varName)) {
            throw new RuntimeException(
                    "A variavel '%s' presente na linha %d e coluna %d ja foi declarada."
                            .formatted(varName, tk.getLine(), tk.getCharPositionInLine()));
        }

        currentScope.insert(varName, address, isConst);

        if (ctx.expression() != null) {
            code.append("push $").append(address).append("\n");
            visit(ctx.expression());
            code.append("sto\n");
        }
        return null;
    }

    @Override
    public Void visitAtribuicao(AtribuicaoContext ctx) {
        var nomeVar = ctx.ID().getText();
        var tk = ctx.ID().getSymbol();
        var declaracaoOpt = scopes.lookup(nomeVar);

        if (declaracaoOpt.isEmpty()) {
            throw new RuntimeException(
                    "A variavel '%s' não foi declarada na linha %d e coluna %d."
                            .formatted(nomeVar, tk.getLine(), tk.getCharPositionInLine()));
        }

        var variavel = declaracaoOpt.get();
        if (variavel.isConstant()) {
            throw new RuntimeException(
                    "Erro Semântico: Tentativa de reatribuir valor à constante '%s' na linha %d e coluna %d."
                            .formatted(nomeVar, tk.getLine(), tk.getCharPositionInLine()));
        }
        var address = variavel.address();

        code.append("push $").append(address).append("\n");
        visit(ctx.expression());
        code.append("sto\n");

        code.append("push $").append(address).append("\n");
        code.append("lod\n");

        return null;
    }

    @Override
    public Void visitInput(InputContext ctx) {
        var nomeVar = ctx.ID().getText();
        var declaracaoOpt = scopes.lookup(nomeVar);

        if (declaracaoOpt.isEmpty()) {
        }

        var address = declaracaoOpt.get().address();

        code.append("push $").append(address).append("\n");
        code.append("in\n");
        code.append("sto\n");

        return null;
    }

    @Override
    public Void visitImpressao(ImpressaoContext ctx) {
        if (ctx.STRING() != null) {
            code.append("push ").append(ctx.STRING().getText()).append("\n");
            code.append("out\n");
        } else {
            visit(ctx.expression());
            code.append("out\n");
        }

        code.append("push \"\\n\"\n");
        code.append("out\n");

        return null;
    }

    @Override
    public Void visitBloco(BlocoContext ctx) {
        scopes.createScope();
        for (var expressao : ctx.expr()) {
            visit(expressao);
        }
        scopes.dropScope();
        return null;
    }

    @Override
    public Void visitCondicao(CondicaoContext ctx) {
        var fimIf = createLabel();

        visit(ctx.condition());
        code.append("fjp ").append(fimIf).append("\n");

        visit(ctx.block());
        code.append(fimIf).append(":\n");

        return null;
    }

    @Override
    public Void visitLoop(LoopContext ctx) {
        var start = createLabel();
        var end = createLabel();

        code.append(start).append(":\n");
        visit(ctx.condition());
        code.append("fjp ").append(end).append("\n");

        visit(ctx.block());

        code.append("ujp ").append(start).append("\n");
        code.append(end).append(":\n");

        return null;
    }

    @Override
    public Void visitCondRelacional(CondRelacionalContext ctx) {
        visit(ctx.expression(0));
        visit(ctx.expression(1));

        int tipoOperador = ctx.op.start.getType();

        switch (tipoOperador) {
            case ExprParser.GT -> code.append("grt\n");
            case ExprParser.GE -> code.append("gte\n");
            case ExprParser.LT -> code.append("let\n");
            case ExprParser.LE -> code.append("lte\n");
            case ExprParser.EQ -> code.append("equ\n");
            case ExprParser.NEQ -> code.append("neq\n");
        }
        return null;
    }

    @Override
    public Void visitCondAnd(CondAndContext ctx) {
        visit(ctx.condition(0));
        visit(ctx.condition(1));
        code.append("and\n");
        return null;
    }

    @Override
    public Void visitCondOr(CondOrContext ctx) {
        visit(ctx.condition(0));
        visit(ctx.condition(1));
        code.append("or\n");
        return null;
    }

    @Override
    public Void visitCondBooleano(CondBooleanoContext ctx) {
        if (ctx.TRUE() != null) {
            code.append("push true\n");
        } else {
            code.append("push false\n");
        }
        return null;
    }

    @Override
    public Void visitCondParenteses(CondParentesesContext ctx) {
        visit(ctx.condition());
        return null;
    }
}