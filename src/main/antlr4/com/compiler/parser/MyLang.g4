grammar MyLang;

// Programa
program: statement* EOF;

// Instruções
statement
    : varDecl
    | assignment
    | ifStmt
    | whileStmt
    | printStmt
    | inputStmt
    ;

// Declaração e Atribuição
varDecl    : 'var' ID ( '=' expr )? ';' ;
assignment : ID '=' expr ';' ;

// Estruturas de Controle
ifStmt    : 'if'    '(' expr ')' block ;
whileStmt : 'while' '(' expr ')' block ;

block : '{' statement* '}' ;

// Entrada / Saída
printStmt : 'print' '(' expr ')' ';' ;
inputStmt : 'input' '(' ID ')' ';' ;

// Expressões (com precedência explícita via alternativas ordenadas)
expr
    : <assoc=right> expr '^' expr                           # exprPow
    | expr op=('*'|'/')   expr                              # exprMulDiv
    | expr op=('+'|'-')   expr                              # exprAddSub
    | expr op=('<'|'>'|'<='|'>='|'=='|'!=') expr            # exprRel
    | expr op=('and'|'or') expr                             # exprLogic
    | NUMBER                                                # exprNumber
    | STRING                                                # exprString
    | 'true'                                                # exprTrue
    | 'false'                                               # exprFalse
    | ID                                                    # exprId
    | '(' expr ')'                                          # exprParen
    ;

// Tokens
ID     : [a-zA-Z_][a-zA-Z0-9_]* ;
// FIX: suporte a números decimais
NUMBER : [0-9]+ ('.' [0-9]+)? ;
// FIX: suporte a strings literais entre aspas duplas
STRING : '"' (~["\r\n])* '"' ;

WS      : [ \t\r\n]+ -> skip ;
COMMENT : '//' ~[\r\n]*  -> skip ;