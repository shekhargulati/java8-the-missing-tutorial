#!/usr/bin/jjs

var Paths = Java.type("java.nio.file.Paths");
var Files = Java.type("java.nio.file.Files");

Files.lines(Paths.get($ARG[0])).forEach(function(line){print(line);})
