//
// Created by Mikhail Lyapich on 27.10.16.
//

#include "sort_header.h"
#include <vector>
#include <algorithm>

extern "C"{
    JNIEXPORT jintArray JNICALL Java_com_example_awfulman_breakingnews_NewsListActivity_sort(JNIEnv *env,jclass clazz, jintArray freqs, jint size) {
        jint* c_freqs = (*env).GetIntArrayElements(freqs, false);

        std::vector<jint> myVector(c_freqs, c_freqs + size);
        std::sort(myVector.begin(), myVector.end());

        jint* j_freqs = &myVector[0];
        jintArray result = (*env).NewIntArray(size);
        (*env).SetIntArrayRegion(result, 0, size, j_freqs);

        return result;


//        std::sort(myVector.begin(),myVector.end());
    }
}