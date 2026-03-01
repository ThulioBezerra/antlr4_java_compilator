package com.uepb.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

        if (parser.getNumberOfSyntaxErrors() == 0) {
            var visitor = new ExprVisitor();
            visitor.visit(tree);

            String pCodeGerado = visitor.getResult();
            Files.writeString(output.toPath(), pCodeGerado);

            System.out.println("Arquivo P-Code gerado com sucesso em: " + output.getAbsolutePath());
        } else {
            System.err.println("Erro de compilação: O código possui erros sintáticos.");
        }

        if(verbose){
            var guiTask = new GuiVizualizerTask(parser, tree);
            guiTask.run();
        }
    }

}
