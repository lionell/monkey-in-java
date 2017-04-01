package(default_visibility = ["//visibility:public"])

java_binary(
    name = "monkey",
    main_class = "monkey.Main",
    runtime_deps = [
        ":monkey_main",
    ],
)

java_library(
    name = "monkey_main",
    srcs = ["Main.java"],
    deps = [
        "//java/monkey/repl",
    ],
)
