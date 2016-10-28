//
// Created by Mikhail Lyapich on 27.10.16.
//
#include "jni.h"

#ifndef BREAKINGNEWS_SORT_HEADER_H
#define BREAKINGNEWS_SORT_HEADER_H

extern "C" {
    JNIEXPORT jintArray JNICALL Java_smart_breakingnews_NewsListActivity_sort(JNIEnv *env, jclass clazz, jintArray freqs, jint size);
}

#endif //BREAKINGNEWS_SORT_HEADER_H
