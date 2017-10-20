/*

from examples/src:

g++ -fPIC -shared -I /share/output-jdk9-hs-comp-dbg/images/jdk/include/ -I /share/output-jdk9-hs-comp-dbg/images/jdk/include/linux/ -o ../bin/traceClassAgent.so jvmti/traceClassAgent.cpp
 */
#include <jvmti.h>
#include <stdio.h>
#include <string.h>

const char* pattern = "";

static void printClass(jvmtiEnv* jvmti, jclass klass, const char* prefix) {
  char *className;
  if (jvmti->GetClassSignature(klass, &className, NULL) != JVMTI_ERROR_NONE) {
    fprintf(stdout, "%s%s of %p\n", prefix, "Can't get class signature", klass);
    return;
  }
  ++className; // Ignore leading 'L'
  if (strstr(className, pattern) == className) {
    className[strlen(className) - 1] = '\0'; // Strip trailing ';'
    fprintf(stdout, "%s%s\n", prefix, className);
    fflush (NULL);
  }
  jvmti->Deallocate((unsigned char*) --className);
}

void JNICALL classLoadCallback(jvmtiEnv* jvmti, JNIEnv* jni, jthread thread, jclass klass) {
  printClass(jvmti, klass, "ClassLoad:    ");
}

void JNICALL classPrepareCallback(jvmtiEnv* jvmti, JNIEnv* jni, jthread thread, jclass klass) {
  printClass(jvmti, klass, "ClassPrepare: ");
}

void JNICALL classFileLoadCallback(jvmtiEnv* jvmti, JNIEnv* jni,
                                   jclass class_being_redefined,
                                   jobject loader,
                                   const char* name,
                                   jobject protection_domain,
                                   jint class_data_len,
                                   const unsigned char* class_data,
                                   jint* new_class_data_len,
                                   unsigned char** new_class_data) {
  if (strstr(name, pattern) == name) {
    fprintf(stdout, "FileLoad:     %s (%p)\n", name, loader);
  }
}

extern "C"
JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM* jvm, char* options, void* reserved) {
  jvmtiEnv* jvmti = NULL;
  jvmtiCapabilities capa;
  jvmtiError error;

  if (options) pattern = strdup(options); // Options may contain the pattern

  jint result = jvm->GetEnv((void**) &jvmti, JVMTI_VERSION_1_1);
  if (result != JNI_OK) {
    fprintf(stderr, "Can't access JVMTI!\n");
    return JNI_ERR;
  }

  jvmtiEventCallbacks callbacks;
  memset(&callbacks, 0, sizeof(jvmtiEventCallbacks));
  callbacks.ClassLoad = classLoadCallback;
  callbacks.ClassPrepare = classPrepareCallback;
  callbacks.ClassFileLoadHook = classFileLoadCallback;
  if (jvmti->SetEventCallbacks(&callbacks, sizeof(jvmtiEventCallbacks)) != JVMTI_ERROR_NONE) {
    fprintf(stderr, "Can't set event callbacks!\n");
    return JNI_ERR;
  }
  if (jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_LOAD, NULL) != JVMTI_ERROR_NONE) {
    fprintf(stderr, "Can't enable JVMTI_EVENT_CLASS_LOAD!\n");
    return JNI_ERR;
  }
  if (jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_PREPARE, NULL) != JVMTI_ERROR_NONE) {
    fprintf(stderr, "Can't enable JVMTI_EVENT_CLASS_PREPARE!\n");
    return JNI_ERR;
  }
  if (jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, NULL) != JVMTI_ERROR_NONE) {
    fprintf(stderr, "Can't enable JVMTI_EVENT_CLASS_FILE_LOAD_HOOK!\n");
    return JNI_ERR;
  }
}
