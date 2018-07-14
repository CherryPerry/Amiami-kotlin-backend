package com.cherryperry.amiami.controller

import org.springframework.http.CacheControl
import java.util.concurrent.TimeUnit

private const val STALE_IN_DAYS = 7L

fun createDefaultCacheControl(): CacheControl = CacheControl
    .maxAge(0, TimeUnit.SECONDS)
    .staleIfError(STALE_IN_DAYS, TimeUnit.DAYS)
    .cachePrivate()
