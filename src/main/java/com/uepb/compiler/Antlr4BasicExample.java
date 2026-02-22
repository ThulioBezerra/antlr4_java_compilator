package com.uepb.compiler;

import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import com.uepb.ExprLexer;
import com.uepb.ExprParser;
import com.uepb.gui.GuiVizualizerTask;
import com.uepb.interfaces.CompilerEngine;

public class Antlr4BasicExample implements CompilerEngine{

    @Override
    public void execute(File input, File output, boolean verbose) throws IOException {
        var charStream = CharStreams.fromPath(input.toPath());
        var lexer = new ExprLexer(charStream);
        var tokens = new CommonTokenStream(lexer);
        var parser = new ExprParser(tokens);
        var tree = parser.calc();

        if(parser.getNumberOfSyntaxErrors() == 0){
            var visitor = new ExprVisitor();
            var resultado = visitor.visit(tree);
            System.out.println("Resultado: " + resultado);
        }

        if(verbose){
            var guiTask = new GuiVizualizerTask(parser, tree);
            guiTask.run();
        }
    }

}
