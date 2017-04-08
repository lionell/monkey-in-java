# Monkey language in Java

[![Build Status](https://travis-ci.org/lionell/monkey-in-java.svg?branch=master)](https://travis-ci.org/lionell/monkey-in-java)

<div align="center">
  <img width="300px" src="http://tinyclipart.com/resource/monkey-cartoon/monkey-cartoon-142.jpg" />
</div>

## Language specification

Here will be formal language specification in EBNF.

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
