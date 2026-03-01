package com.uepb.compiler;

import com.uepb.ExprBaseVisitor;
import com.uepb.ExprParser.AtribVariavelContext;
import com.uepb.ExprParser.CalcContext;
import com.uepb.ExprParser.DeclaracaoContext;
import com.uepb.ExprParser.DeclaracaoVariavelContext;
import com.uepb.ExprParser.InputContext;
import com.uepb.ExprParser.LoopContext;
import com.uepb.ExprParser.MulDivContext;
import com.uepb.ExprParser.NumeroContext;
import com.uepb.ExprParser.SomaSubContext;
import com.uepb.ExprParser.UnarioContext;
import com.uepb.ExprParser.UsoVariavelContext;
import com.uepb.compiler.data_structures.AddressControl;
import com.uepb.compiler.data_structures.ScopeControl;

public class ExprVisitor extends ExprBaseVisitor<Void>{

    private final StringBuilder code = new StringBuilder();
    private final ScopeControl scopeControl = new ScopeControl();
    private final AddressControl addressControl = new AddressControl();
    private int labelCount = 0;

    private String newLabel(){
        return "L" + (labelCount++);
    }

    public String getResult(){
        return code.toString();
    }

    @Override
    public Void visitCalc(CalcContext ctx) {
        visit(ctx.expr());
        code.append("out\n");
        code.append("hlt\n");
        return null;
    }

    @Override
    public Void visitNumero(NumeroContext ctx) {
        code
            .append("push ")
            .append(ctx.NUMBER().getText())
            .append("\n");

        return null;
    }

    @Override
    public Void visitUnario(UnarioContext ctx) {
        visit(ctx.expr());
        if(ctx.OP.getText().equals("-")){
            code
                .append("push -1\n")
                .append("mul\n");
        }

        return null;
    }

    @Override
    public Void visitUsoVariavel(UsoVariavelContext ctx) {
        var name = ctx.ID().getText();
        var variable = scopeControl.findClosestVariable(name)
            .orElseThrow(() -> new RuntimeException("A variável não foi declarada: " + name));

        code
            .append("push $").append(variable.address()).append("\n")
            .append("lod\n");

        return null;
    }

    @Override
    public Void visitSomaSub(SomaSubContext ctx) {
        visit(ctx.OPERANDO1);
        visit(ctx.OPERANDO2);

        var op = ctx.OP.getText().equals("+") ? "add" : "sub";
        code.append(op).append("\n");
        return null;
    }

    @Override
    public Void visitMulDiv(MulDivContext ctx) {
        visit(ctx.OPERANDO1);
        visit(ctx.OPERANDO2);

        var op = ctx.OP.getText().equals("*") ? "mul" : "div";
        code.append(op).append("\n");
        return null;
    }

    @Override
    public Void visitAtribVariavel(AtribVariavelContext ctx) {
        var name = ctx.ID().getText();
        var variable = scopeControl.findClosestVariable(name)
            .orElseThrow(() -> new RuntimeException("A variável não foi declarada: " + name));

        code.append("push $").append(variable.address()).append("\n");
        visit(ctx.expr());
        code.append("sto\n");

        code.append("push $").append(variable.address()).append("\n");
        code.append("lod\n");

        return null;
    }

    @Override
    public Void visitDeclaracaoVariavel(DeclaracaoVariavelContext ctx) {
        int marker = addressControl.getMarker();
        scopeControl.createNewScope();

        visitListaDeclaracoes(ctx.listaDeclaracoes());

        visit(ctx.expr());

        scopeControl.dropCurrentScope();
        addressControl.restoreMarker(marker);

        return null;
    }

    @Override
    public Void visitDeclaracao(DeclaracaoContext ctx) {
        var name = ctx.ID().getText();
        var currentScope = scopeControl.getCurrentScope();

        if(currentScope.exists(name)){
            throw new RuntimeException("Variável já declarada: " + name);
        }

        var address = addressControl.allocate();

        code.append("push $").append(address).append("\n");
        visit(ctx.expr());
        code.append("sto\n");

        currentScope.insertVariable(name, address);

        return null;
    }

    @Override
    public Void visitInput(InputContext ctx) {
        code.append("in\n");
        return null;
    }

    @Override
    public Void visitLoop(LoopContext ctx) {
        String startLabel = newLabel();
        String endLabel = newLabel();
        int tempAddr = addressControl.allocate();

        code.append("push $").append(tempAddr).append("\n");
        visit(ctx.LOOP_EVAL);
        code.append("sto\n");

        code.append(startLabel).append(":\n");

        //Contador > 0
        code.append("push $").append(tempAddr).append("\n");
        code.append("lod\n");
        code.append("push 0\n");
        code.append("grt\n");

        //Se falso
        code.append("fjp ").append(endLabel).append("\n");

        visit(ctx.EXP);
        code.append("pop\n");

        //Decremento de contador
        code.append("push $").append(tempAddr).append("\n");
        code.append("dup\n");
        code.append("lod\n");
        code.append("push 1\n");
        code.append("sub\n");
        code.append("sto\n");
        code.append("ujp ").append(startLabel).append("\n");

        //Fim
        code.append(endLabel).append(":\n");
        code.append("push 0\n");

        visit(ctx.OUT);

        return null;
    }
}
