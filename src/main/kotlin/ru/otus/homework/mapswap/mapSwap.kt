package ru.otus.homework.mapswap

/**
 * Меняет местами ключи и значения
 * При повторяющихся значениях сохраняет последний ключ в порядке обхода исходной Map.
 */
fun <K, V> Map<K, V>.swap(): Map<V, K> = entries.associate { (key, value) -> value to key }
