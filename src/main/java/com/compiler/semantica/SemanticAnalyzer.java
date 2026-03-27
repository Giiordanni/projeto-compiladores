package com.compiler.semantica;

import com.compiler.parser.MyLangBaseVisitor;
import com.compiler.parser.MyLangParser;

public class SemanticAnalyzer extends MyLangBaseVisitor<String> {

    private final SymbolTable table = new SymbolTable();


    @Override
    public String visitVarDecl(MyLangParser.VarDeclContext ctx) {
        String name = ctx.ID().getText();

        if (ctx.expr() != null) {
            String exprType = visit(ctx.expr());
            table.declare(name, exprType);   // tipo vem da expressão
        } else {
            table.declare(name, "unknown");  // declarada sem valor
        }
        return null;
    }

    @Override
    public String visitAssignment(MyLangParser.AssignmentContext ctx) {
        String name     = ctx.ID().getText();
        table.assertDeclared(name);          // variável deve existir

        String exprType = visit(ctx.expr());
        String varType  = table.getType(name);

        // Se a variável ainda não tinha tipo definido, adota o da expressão
        if (varType.equals("unknown")) {
            table.setType(name, exprType);
        } else if (!varType.equals(exprType) && !exprType.equals("unknown")) {
            throw new RuntimeException(
                    "Erro semântico: variável '" + name + "' é do tipo " + varType
                            + " mas recebeu valor do tipo " + exprType + ".");
        }
        return null;
    }


    //  Estruturas de controle
    @Override
    public String visitIfStmt(MyLangParser.IfStmtContext ctx) {
        String condType = visit(ctx.expr());
        checkCondition(condType, "if");
        visit(ctx.block());
        return null;
    }

    @Override
    public String visitWhileStmt(MyLangParser.WhileStmtContext ctx) {
        String condType = visit(ctx.expr());
        checkCondition(condType, "while");
        visit(ctx.block());
        return null;
    }

    // Abre e fecha escopo ao entrar/sair de bloco { }
    @Override
    public String visitBlock(MyLangParser.BlockContext ctx) {
        table.enterScope();
        visitChildren(ctx);
        table.exitScope();
        return null;
    }


    //  Entrada e saída
    @Override
    public String visitPrintStmt(MyLangParser.PrintStmtContext ctx) {
        visit(ctx.expr()); // qualquer tipo é aceito no print
        return null;
    }

    @Override
    public String visitInputStmt(MyLangParser.InputStmtContext ctx) {
        String name = ctx.ID().getText();
        table.assertDeclared(name); // variável deve ter sido declarada
        return null;
    }

    //  Expressões — cada método retorna o tipo inferido
    @Override
    public String visitExprNumber(MyLangParser.ExprNumberContext ctx) {
        return "number";
    }

    @Override
    public String visitExprString(MyLangParser.ExprStringContext ctx) {
        return "string";
    }

    @Override
    public String visitExprTrue(MyLangParser.ExprTrueContext ctx) {
        return "bool";
    }

    @Override
    public String visitExprFalse(MyLangParser.ExprFalseContext ctx) {
        return "bool";
    }

    @Override
    public String visitExprId(MyLangParser.ExprIdContext ctx) {
        String name = ctx.ID().getText();
        table.assertDeclared(name);
        return table.getType(name); // retorna o tipo registrado
    }

    @Override
    public String visitExprParen(MyLangParser.ExprParenContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public String visitExprPow(MyLangParser.ExprPowContext ctx) {
        String left  = visit(ctx.expr(0));
        String right = visit(ctx.expr(1));
        checkNumeric(left,  "^", ctx.expr(0).getText());
        checkNumeric(right, "^", ctx.expr(1).getText());
        return "number";
    }

    @Override
    public String visitExprMulDiv(MyLangParser.ExprMulDivContext ctx) {
        String left  = visit(ctx.expr(0));
        String right = visit(ctx.expr(1));
        checkNumeric(left,  ctx.op.getText(), ctx.expr(0).getText());
        checkNumeric(right, ctx.op.getText(), ctx.expr(1).getText());
        return "number";
    }

    @Override
    public String visitExprAddSub(MyLangParser.ExprAddSubContext ctx) {
        String left  = visit(ctx.expr(0));
        String right = visit(ctx.expr(1));
        checkNumeric(left,  ctx.op.getText(), ctx.expr(0).getText());
        checkNumeric(right, ctx.op.getText(), ctx.expr(1).getText());
        return "number";
    }

    @Override
    public String visitExprRel(MyLangParser.ExprRelContext ctx) {
        String left  = visit(ctx.expr(0));
        String right = visit(ctx.expr(1));
        // Comparações == e != aceitam qualquer tipo, desde que os dois lados
        // sejam do mesmo tipo
        String op = ctx.op.getText();
        if (op.equals("==") || op.equals("!=")) {
            if (!left.equals(right) && !left.equals("unknown") && !right.equals("unknown")) {
                throw new RuntimeException(
                        "Erro semântico: operador '" + op + "' compara tipos diferentes ("
                                + left + " e " + right + ").");
            }
        } else {
            // <, >, <=, >= só fazem sentido com numbers
            checkNumeric(left,  op, ctx.expr(0).getText());
            checkNumeric(right, op, ctx.expr(1).getText());
        }
        return "bool";
    }

    @Override
    public String visitExprLogic(MyLangParser.ExprLogicContext ctx) {
        String left  = visit(ctx.expr(0));
        String right = visit(ctx.expr(1));
        checkLogical(left,  ctx.op.getText(), ctx.expr(0).getText());
        checkLogical(right, ctx.op.getText(), ctx.expr(1).getText());
        return "bool";
    }


    //  Métodos auxiliares de verificação

    private void checkNumeric(String type, String op, String exprText) {
        if (type.equals("string")) {
            throw new RuntimeException(
                    "Erro semântico: operador '" + op
                            + "' exige number, mas '" + exprText + "' é string.");
        }
    }

    private void checkLogical(String type, String op, String exprText) {
        if (type.equals("string")) {
            throw new RuntimeException(
                    "Erro semântico: operador lógico '" + op
                            + "' não pode ser aplicado a string ('" + exprText + "').");
        }
    }

    private void checkCondition(String type, String stmt) {
        if (type.equals("string")) {
            throw new RuntimeException(
                    "Erro semântico: condição do '" + stmt
                            + "' não pode ser uma string.");
        }
    }
}
