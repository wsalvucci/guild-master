package com.example.demo.data.context

import com.example.demo.domain.ActiveGuildContext
import org.koin.dsl.module

val gameContextModule = module {
    single<ActiveGuildContext> { ActiveGuildContextImpl() }
}