package icu.merky.mj.compilecheck

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CompileCheckEntryPoint {
    fun marker(): String
}
