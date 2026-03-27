# Compilador MyLang

Este projeto é um compilador para a linguagem simplificada **MyLang**, desenvolvido para a disciplina de Compiladores. O compilador lê um código-fonte (`program.txt`) e gera o código intermediário correspondente em **P-Code**, pronto para ser executado em uma máquina virtual baseada em pilha.

## Pré-requisitos
Antes de começar, certifique-se de ter instalado em sua máquina:
* Java (JDK 21+)
* Maven (3.6+)

## Como compilar e rodar o projeto

**1. Compilar o projeto**

Abra o terminal na pasta raiz do projeto e execute o comando abaixo para limpar builds anteriores e gerar o executável:

```bash
mvn clean package

**2. Executar o compilador**

Após a compilação, o arquivo `.jar` será criado dentro da pasta `target`. Para rodar o compilador passando o seu arquivo de código (neste exemplo, `program.txt`), use o comando no terminal:

```bash
java -jar target/compiler.jar program.txt

**3. Resultado**

Se não houver erros léxicos, sintáticos ou semânticos, o arquivo P-Code gerado estará disponível na pasta de saída: `src/output/program.pcode` e no console.
