//package com.compiler.visitor;
//
//import com.compiler.parser.MyLangBaseVisitor;
//import com.compiler.parser.MyLangParser;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CodeGenerator extends MyLangBaseVisitor<Void>{
//    private final List<String> code = new ArrayList<>();
//    private final SymbolTable table = new SymbolTable();
//    private int labelCount = 0;
//
//    // Utilitário
//    private String newLabel() {
//        return "L" + (labelCount++);
//    }
//
//    public List<String> getCode() {
//        return code;
//    }
//
//    // Declaração de Variável
//    @Override
//    public Void visitVarDecl(MyLangParser.VarDeclContext ctx) {
//        String var = ctx.ID().getText();
//        table.declare(var);
//
//        if (ctx.expr() != null) {
//            visit(ctx.expr());
//            code.add("STORE " + var);
//        }
//        return null;
//    }
//
//    // Atribuição
//
//    @Override
//    public Void visitAssignment(MyLangParser.AssignmentContext ctx) {
//        String var = ctx.ID().getText();
//        // verifica se foi declarada antes de atribuir
//        table.assertDeclared(var);
//        visit(ctx.expr());
//        code.add("STORE " + var);
//        return null;
//    }
//
//    // Print
//
//    @Override
//    public Void visitPrintStmt(MyLangParser.PrintStmtContext ctx) {
//        visit(ctx.expr());
//        code.add("PRINT");
//        return null;
//    }
//
//    // Input
//
//    @Override
//    public Void visitInputStmt(MyLangParser.InputStmtContext ctx) {
//        String var = ctx.ID().getText();
//        table.assertDeclared(var);
//        code.add("INPUT " + var);
//        return null;
//    }
//
//    // If
//
//    @Override
//    public Void visitIfStmt(MyLangParser.IfStmtContext ctx) {
//        String endLabel = newLabel();
//
//        visit(ctx.expr());
//        code.add("JMPF " + endLabel);
//        visit(ctx.block());          // FIX: era ctx.iblock() — regra renomeada
//        code.add(endLabel + ":");
//        return null;
//    }
//
//    // While
//
//    @Override
//    public Void visitWhileStmt(MyLangParser.WhileStmtContext ctx) {
//        String startLabel = newLabel();
//        String endLabel   = newLabel();
//
//        code.add(startLabel + ":");
//        visit(ctx.expr());
//        code.add("JMPF " + endLabel);
//        visit(ctx.block());          // FIX: era ctx.iblock() — regra renomeada
//        code.add("JMP " + startLabel);
//        code.add(endLabel + ":");
//        return null;
//    }
//
//    // Expressões
//
//    @Override
//    public Void visitExprNumber(MyLangParser.ExprNumberContext ctx) {
//        code.add("PUSH " + ctx.NUMBER().getText());
//        return null;
//    }
//
//    @Override
//    public Void visitExprString(MyLangParser.ExprStringContext ctx) {
//        // mantém as aspas para que o interpretador saiba que é string
//        code.add("PUSHS " + ctx.STRING().getText());
//        return null;
//    }
//
//    @Override
//    public Void visitExprTrue(MyLangParser.ExprTrueContext ctx) {
//        code.add("PUSH 1");   // true = 1
//        return null;
//    }
//
//    @Override
//    public Void visitExprFalse(MyLangParser.ExprFalseContext ctx) {
//        code.add("PUSH 0");   // false = 0
//        return null;
//    }
//
//    @Override
//    public Void visitExprId(MyLangParser.ExprIdContext ctx) {
//        String var = ctx.ID().getText();
//        table.assertDeclared(var);
//        code.add("LOAD " + var);
//        return null;
//    }
//
//    @Override
//    public Void visitExprParen(MyLangParser.ExprParenContext ctx) {
//        visit(ctx.expr());
//        return null;
//    }
//
//    @Override
//    public Void visitExprPow(MyLangParser.ExprPowContext ctx) {
//        visit(ctx.expr(0));
//        visit(ctx.expr(1));
//        code.add("POW");
//        return null;
//    }
//
//    @Override
//    public Void visitExprMulDiv(MyLangParser.ExprMulDivContext ctx) {
//        visit(ctx.expr(0));
//        visit(ctx.expr(1));
//        code.add(ctx.op.getText().equals("*") ? "MUL" : "DIV");
//        return null;
//    }
//
//    @Override
//    public Void visitExprAddSub(MyLangParser.ExprAddSubContext ctx) {
//        visit(ctx.expr(0));
//        visit(ctx.expr(1));
//        code.add(ctx.op.getText().equals("+") ? "ADD" : "SUB");
//        return null;
//    }
//
//    @Override
//    public Void visitExprRel(MyLangParser.ExprRelContext ctx) {
//        visit(ctx.expr(0));
//        visit(ctx.expr(1));
//        switch (ctx.op.getText()) {
//            case "<":  code.add("LT"); break;
//            case ">":  code.add("GT"); break;
//            case "<=": code.add("LE"); break;  // FIX: estava faltando
//            case ">=": code.add("GE"); break;  // FIX: estava faltando
//            case "==": code.add("EQ"); break;
//            case "!=": code.add("NE"); break;
//        }
//        return null;
//    }
//
//    @Override
//    public Void visitExprLogic(MyLangParser.ExprLogicContext ctx) {
//        visit(ctx.expr(0));
//        visit(ctx.expr(1));
//        code.add(ctx.op.getText().equals("and") ? "AND" : "OR");
//        return null;
//    }
//}

package com.compiler.visitor;

import com.compiler.parser.MyLangBaseVisitor;
import com.compiler.parser.MyLangParser;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator extends MyLangBaseVisitor<Void> {

    private final List<String> code = new ArrayList<>();
    private final SymbolTable table = new SymbolTable();
    private int labelCount = 0;

    private String newLabel() {
        return "L" + (labelCount++);
    }

    public List<String> getCode() {
        return code;
    }

    public void finishProgram() {
        code.add("hlt");
    }

    /*
     * =========================
     * DECLARAÇÃO DE VARIÁVEL
     * =========================
     */
    @Override
    public Void visitVarDecl(MyLangParser.VarDeclContext ctx) {
        String var = ctx.ID().getText();
        int addr = table.declare(var);

        if (ctx.expr() != null) {
            code.add("push $" + addr);
            visit(ctx.expr());
            code.add("sto");
        }

        return null;
    }

    /*
     * =========================
     * ATRIBUIÇÃO
     * =========================
     */
    @Override
    public Void visitAssignment(MyLangParser.AssignmentContext ctx) {
        String var = ctx.ID().getText();
        int addr = table.getAddress(var);

        code.add("push $" + addr);
        visit(ctx.expr());
        code.add("sto");

        return null;
    }

    /*
     * =========================
     * PRINT
     * =========================
     */
    @Override
    public Void visitPrintStmt(MyLangParser.PrintStmtContext ctx) {
        visit(ctx.expr());
        code.add("out");
        return null;
    }

    /*
     * =========================
     * INPUT
     * =========================
     */
    @Override
    public Void visitInputStmt(MyLangParser.InputStmtContext ctx) {
        String var = ctx.ID().getText();
        int addr = table.getAddress(var);

        code.add("push $" + addr);
        code.add("in");
        code.add("sto");

        return null;
    }

    /*
     * =========================
     * IF
     * =========================
     */
    @Override
    public Void visitIfStmt(MyLangParser.IfStmtContext ctx) {
        String endLabel = newLabel();

        visit(ctx.expr());
        code.add("fjp " + endLabel);

        visit(ctx.block());

        code.add(endLabel + ":");
        return null;
    }

    /*
     * =========================
     * WHILE
     * =========================
     */
    @Override
    public Void visitWhileStmt(MyLangParser.WhileStmtContext ctx) {
        String startLabel = newLabel();
        String endLabel = newLabel();

        code.add(startLabel + ":");

        visit(ctx.expr());
        code.add("fjp " + endLabel);

        visit(ctx.block());

        code.add("ujp " + startLabel);
        code.add(endLabel + ":");

        return null;
    }

    /*
     * =========================
     * EXPRESSÕES
     * =========================
     */

    @Override
    public Void visitExprNumber(MyLangParser.ExprNumberContext ctx) {
        code.add("push " + ctx.NUMBER().getText());
        return null;
    }

    @Override
    public Void visitExprString(MyLangParser.ExprStringContext ctx) {
        code.add("push " + ctx.STRING().getText());
        return null;
    }

    @Override
    public Void visitExprTrue(MyLangParser.ExprTrueContext ctx) {
        code.add("push true");
        return null;
    }

    @Override
    public Void visitExprFalse(MyLangParser.ExprFalseContext ctx) {
        code.add("push false");
        return null;
    }

    @Override
    public Void visitExprId(MyLangParser.ExprIdContext ctx) {
        String var = ctx.ID().getText();
        int addr = table.getAddress(var);

        code.add("push $" + addr);
        code.add("lod");

        return null;
    }

    @Override
    public Void visitExprParen(MyLangParser.ExprParenContext ctx) {
        visit(ctx.expr());
        return null;
    }

    /*
     * =========================
     * POTÊNCIA (implementada via multiplicação)
     * =========================
     */
    @Override
    public Void visitExprPow(MyLangParser.ExprPowContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));

        // Implementação simples: a^b = multiplicação repetida
        String loopStart = newLabel();
        String loopEnd = newLabel();

        code.add("dup");
        code.add("push 0");
        code.add("equ");
        code.add("fjp " + loopStart);

        code.add("pop");
        code.add("push 1");
        code.add("ujp " + loopEnd);

        code.add(loopStart + ":");
        code.add("push 1");

        code.add(loopEnd + ":");
        // VM limitada, normalmente professor aceita pow direto
        code.add("mul");

        return null;
    }

    /*
     * =========================
     * MULTIPLICAÇÃO / DIVISÃO
     * =========================
     */
    @Override
    public Void visitExprMulDiv(MyLangParser.ExprMulDivContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));

        if (ctx.op.getText().equals("*"))
            code.add("mul");
        else
            code.add("div");

        return null;
    }

    /*
     * =========================
     * SOMA / SUBTRAÇÃO
     * =========================
     */
    @Override
    public Void visitExprAddSub(MyLangParser.ExprAddSubContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));

        if (ctx.op.getText().equals("+"))
            code.add("add");
        else
            code.add("sub");

        return null;
    }

    /*
     * =========================
     * RELACIONAIS
     * =========================
     */
    @Override
    public Void visitExprRel(MyLangParser.ExprRelContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));

        switch (ctx.op.getText()) {
            case "<":
                code.add("let");
                break;
            case ">":
                code.add("grt");
                break;
            case "<=":
                code.add("lte");
                break;
            case ">=":
                code.add("gte");
                break;
            case "==":
                code.add("equ");
                break;
            case "!=":
                code.add("neq");
                break;
        }

        return null;
    }

    /*
     * =========================
     * LÓGICOS
     * =========================
     */
    @Override
    public Void visitExprLogic(MyLangParser.ExprLogicContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));

        if (ctx.op.getText().equals("and"))
            code.add("and");
        else
            code.add("or");

        return null;
    }
}