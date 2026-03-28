package com.compiler;

import com.compiler.parser.MyLangLexer;
import com.compiler.parser.MyLangParser;
import com.compiler.semantica.SemanticAnalyzer;
import com.compiler.visitor.CodeGenerator;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main( String[] args ) {
        String inputFile = (args.length > 0) ? args[0] : "program.txt";

        try {
            // Leitura do arquivo-fonte
            CharStream input = CharStreams.fromFileName(inputFile);

            // Análise Léxica
            MyLangLexer lexer = new MyLangLexer(input);
            lexer.removeErrorListeners();
            lexer.addErrorListener(new ThrowingErrorListener("Léxico"));

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Análise Sintática
            MyLangParser parser = new MyLangParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new ThrowingErrorListener("Sintático"));

            ParseTree tree = parser.program();

            // ANÁLISE SEMÂNTICA  ← novo
            SemanticAnalyzer semantic = new SemanticAnalyzer();
            semantic.visit(tree);
            System.out.println("✓ Análise semântica concluída sem erros.");

            // Geração de P-Code
            CodeGenerator generator = new CodeGenerator();
            generator.visit(tree);
            generator.finishProgram();
            List<String> code = generator.getCode();

            // Escrita do arquivo de saída
            Path outputPath = Paths.get("output/program.pcode");
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, code);

            System.out.println("✓ Compilação concluída: " + outputPath.toAbsolutePath());
            System.out.println("── P-Code gerado (" + code.size() + " instruções) ──");
            code.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Erro de I/O: " + e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            System.err.println("Erro de compilação: " + e.getMessage());
            System.exit(1);
        }
    }

    // Listener que lança exceção em vez de só imprimir erros
    private static class ThrowingErrorListener extends BaseErrorListener {
        private final String phase;
        ThrowingErrorListener(String phase) { this.phase = phase; }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                                Object offendingSymbol,
                                int line, int charPositionInLine,
                                String msg, RecognitionException e) {
            throw new RuntimeException(
                    String.format("[Erro %s] linha %d:%d — %s", phase, line, charPositionInLine, msg)
            );
        }
    }
}
