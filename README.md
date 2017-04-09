# Monkey language in Java

[![Build Status](https://travis-ci.org/lionell/monkey-in-java.svg?branch=master)](https://travis-ci.org/lionell/monkey-in-java)

<div align="center">
  <img width="300px" src="http://tinyclipart.com/resource/monkey-cartoon/monkey-cartoon-142.jpg" />
</div>

## Language specification

Here will be formal language specification in EBNF.

```
<program>               ::= <statement-list>
<statement-list>        :: { <statement> }
<statement>             ::= <let-statement>
                          | <return-statement>
                          | <expression-statement>
<let-statement>         ::= "let" <identifier> "=" <expression> ";"
<return-statement>      ::= "return" <expression> ";"
<expression-statement>  ::= <expression> [ ";" ]


<expression>            ::= <relational-expression>
<relational-expression> ::= <relational-expression> "==" <relation>
                          | <relational-expression> "!=" <relation>
                          | <relation>
<relation>              ::= <relation> ">" <arithmetic-expression>
                          | <relation> "<" <arithmetic-expression>
                          | <arithmetic-expression>
<arithmetic-expression> ::= <arithmetic-expression> "+" <term>
                          | <arithmetic-expression> "-" <term>
                          | <term>
<term>                  ::= <term> "*" <factor>
                          | <term> "/" <factor>
                          | <factor>
<factor>                ::= "-" <primary>
                          | "!" <primary>
                          | <primary>
<primary>               ::= "(" <expression> ")"
                          | <function-call>
                          | <identifier>
                          | <int>
                          | <bool>


<function-call>         ::= <function> "(" <argument-list> ")"
<function>              ::= <function-literal>
                          | <identifier>
<function-literal>      ::= "fn (" <parameter-list> ") <block-statement>
<block-statement>       ::= "{" <statement-list> "}"
<argument-list>         ::= { <expression> }
<parameter-list>        ::= { <identifier> }

<int>                   ::= <digit> { <digit> }
<digit>                 ::= "0..9"
<alpha>                 ::= "a..zA..Z"
<bool>                  ::= "true"
                          | "false"
```

## How to use

To run REPL just type `bazel run //java/monkey`

To run tests type `bazel test //javatests/monkey/...`

## Dependencies

* [Guava](https://github.com/google/guava) (Google common library)
* [JUnit4](http://junit.org/junit4/) (Popular testing library)
* [JUnitParams](https://github.com/Pragmatists/JUnitParams) (Parametrized tests)
* [Truth](https://github.com/google/truth) (Better assertion)

## License

MIT License
