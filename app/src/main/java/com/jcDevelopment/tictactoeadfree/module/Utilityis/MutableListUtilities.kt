package com.jcDevelopment.tictactoeadfree.module.Utilityis


    typealias I<E> = MutableList<E>
    typealias Copy<E> = (E) -> E
    private inline fun <T, R> I<T>.mapToMutable(transform: (T) -> R): I<R> = mapTo(mutableListOf(), transform)
    fun <E> I<E>.deepCopy1(c: Copy<E>) = mapToMutable { c(it) }
    fun <E> I<I<E>>.deepCopy2(c: Copy<E>) = mapToMutable { it.deepCopy1(c) }
    fun <E> I<I<I<E>>>.deepCopy3(c: Copy<E>) = mapToMutable { it.deepCopy2(c) }
    fun <E> I<I<I<I<E>>>>.deepCopy4(c: Copy<E>) = mapToMutable { it.deepCopy3(c) }
    fun <E> I<I<I<I<I<E>>>>>.deepCopy5(c: Copy<E>) = mapToMutable { it.deepCopy4(c) }
