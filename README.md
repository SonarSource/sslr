# SonarSource Language Recognizer

[![Build Status](https://travis-ci.org/SonarSource/sslr.svg?branch=master)](https://travis-ci.org/SonarSource/sslr)

* Issue tracking: http://jira.sonarsource.com/browse/SSLR
* Community forums: feel free to ask any question on the [Community Forums](https://community.sonarsource.com/)
* License: LGPLv3

SSLR  is a lightweight Java library which provides everything required to analyse any piece of source code. Using SSLR, you can quickly create a lexer, a parser, and some AST visitors to implement quality rules or compute measures. This library is used in several SonarSource language analyzers.


## Motivations
Why yet another tool for language recognition? Why not reuse open source and well-know libraries like ANTLR or JavaCC? These are the first questions asked by any developer discovering SSLR. Of course this option was seriously studied and had big advantages but we decided to start from scratch for the following reasons:

* The SonarSource team is addicted to TDD, and we think that existing tools don't fit well with TDD because they require some code generation, and they don't provide any simple, quick way to unit test all parts of a source code analyzer, such as parsing rules for instance. 
* The SonarSource team is addicted to KISS, and we think a Java developer should be able to do anything from his or her favorite IDE.
* We needed to analyse some legacy languages, like COBOL, which require some very specific lexing and preprocessing features. Implementing those features with existing tools would have required us to fully master those tools, and we didn't feel like we benefited from their black box approach.
* In any case, the ultimate goal of SSLR is to provide a complete compiler front-end stack, which goes well beyond the parsing. Eventually, SSLR will provide the ability to fully implement:
   * Symbolic table (currently in beta)
   * Control flow graph
   * Data flow analysis
   * LLVM IR emitter

**Note:** Relying on SSLR for parsing a language is not a requirement for a SonarQube plugin. Feel free to use any other parsing technology if it makes more sense in your case.

## Features
Here are the main features of SSLR :

* Easy integration and use
   * Just add a dependency on a jar file (or several jars, according to what you want to use : lexer/parser, xpath, common rules, symbol table, ...)
   * No special step to add to the build process
   * No "untouchable" generated code
* Everything in Java
   * Definition of grammar and lexer directly in code, using Java
   * No break in IDE support (syntax highlighting, code navigation, refactoring, etc)
* Mature and production ready
   * This technology is already used in production to analyse millions of lines of code
   * Awesome performance
* Some common rules and basic metric computations available out-of-the-box

### SSLR in Action
If you want to start working with SSLR, you must be familiar with the following standard concepts : Lexical Analysis, Parsing Expression Grammar and AST(Abstract Syntax Tree). From there you can take a look to the source code of the JavaScript (lexer/parser, rules) or Python (lexer/parser, rules) plugins to see how those languages are analysed with help of SSLR. 

SSLR also comes with a MiniC language which has been created to easily and simply test all SSLR features. This MiniC language can be a good starting point for a beginner to understand how to implement/define the different mandatory layers to analyse a language:

* [Lexer](https://github.com/SonarSource/sslr/blob/master/sslr-testing-harness/src/main/java/com/sonar/sslr/test/minic/MiniCLexer.java)
* [Grammar](https://github.com/SonarSource/sslr/blob/master/sslr-testing-harness/src/main/java/com/sonar/sslr/test/minic/MiniCGrammar.java)
* [Parser](https://github.com/SonarSource/sslr/blob/master/sslr-testing-harness/src/main/java/com/sonar/sslr/test/minic/MiniCParser.java)
* [Toolkit](https://github.com/SonarSource/sslr/blob/master/sslr-testing-harness/src/main/java/com/sonar/sslr/test/minic/MiniCToolkit.java)

## SSLR Upgrade Guide
Compatibility is a complex issue, but we do our best to maintaining three types of compatibilities between any two sequential versions of SSLR (e.g. between 1.17 and 1.18, but not between 1.16 and 1.18):

* binary compatibility: we don't guarantee that your code can be linked with new version without recompilation, however for most releases this might be possible;
* source compatibility: in most cases (see below) you should be able to recompile your code with a newer version of SSLR without any changes;
* behavioral compatibility: in most cases (see below) your code will behave exactly as it did with the previous version of SSLR without any changes.

Also note that we don't provide any guarantee about compatibility with unreleased version. If you use snapshot version, then you do so at your own risk.

We can't guarantee that your code can be compiled or linked with new version of SSLR and behave exactly as before the upgrade in the following situations:

* You use internal classes or interfaces, i.e. those that are located under package "org.sonar.sslr.internal".
* You create instances or subclasses of classes, which are not intended for this. Such classes are marked by Javadoc ("This class is not intended to be instantiated or subclassed by clients").
* You implement interfaces which are not intended for this. Such interfaces are marked by Javadoc ("This interface is not intended to be implemented by clients").
* You use methods marked as internal. Such methods are marked by annotation "@VisibleForTesting" or by Javadoc ("For internal use only").
* You use beta code. Such code is marked by annotation "@Beta".
* You use deprecated code. Such code is marked by annotation "@Deprecated" and Javadoc.

We try to maintain deprecated code as long as possible, but generally it may be removed in a the release after the one in which it was marked as deprecated. That's why we highly recommend not to jump over two versions at once, but perform upgrades in several steps - one version per step. Each such step should include the removal of uses of deprecated code. Thus, it is recommended to upgrade as soon as a new version is available. 

Recommended upgrade steps:

1. Recompile your code with the next version of SSLR. If it can't be compiled, then don't hesitate to inform us.
1. Check release notes and upgrade notes for the existence of behavioral incompatibilities (see below). If there are any, you should fix them by following the instructions in the notes. Good coverage of your code by unit tests is highly recommended (SonarQube can help you to enforce this), so you will be able to perform tests to verify that it behaves exactly as before upgrade. If not, then don't hesitate to ask for help.
1. Remove uses of deprecated code by following the instructions you'll find in the deprecation Javadocs (SonarQube can help you to find such code). And don't forget to execute tests to verify that regressions were not introduced by such changes.

Sincerely yours, SonarSource Language Team.
