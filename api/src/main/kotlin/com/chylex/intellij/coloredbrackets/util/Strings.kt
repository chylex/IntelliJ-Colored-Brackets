package com.chylex.intellij.coloredbrackets.util

fun fileExtension(fileName: String) = fileName.substring(fileName.lastIndexOf(".") + 1)

val memoizedFileExtension = { fileName: String -> fileExtension(fileName) }.memoize()
