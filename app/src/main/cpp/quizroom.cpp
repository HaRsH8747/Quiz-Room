#include <jni.h>
#include <string>
extern "C" JNIEXPORT jstring JNICALL
Java_com_quizroom_MainActivity_getAPIKey(JNIEnv* env, jobject /* this */) {
    std::string api_key = "y1u7U0MDlKedOJGMvfa";
    return env->NewStringUTF(api_key.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_quizroom_categorySection_CategoryFragment_getAPIKey(JNIEnv* env, jobject /* this */) {
std::string api_key = "y1u7U0MDlKedOJGMvfa";
return env->NewStringUTF(api_key.c_str());
}
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("quizroom");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("quizroom")
//      }
//    }