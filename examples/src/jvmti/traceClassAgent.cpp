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
  if (strcmp(name, "java/lang/Object") == 0) {
    *new_class_data_len = class_data_len;
    jvmti->Allocate(*new_class_data_len, new_class_data);
    // Create a copy of the original java.lang.Object class
    memcpy(*new_class_data, class_data, *new_class_data_len);
    // Replace all '\0' by 'X' and terminate with '\0' such that we can search in the class file
    for (int i = 0; i < class_data_len; i++) {
      if ((*new_class_data)[i] == '\0') (*new_class_data)[i] = 'X';
    }
    (*new_class_data)[class_data_len - 1] == '\0';
    // Now search for the exception message which is thrown when we call Object.wait(long, int) with a negative argument
    char* dest = strstr((char*)*new_class_data, (char*)"timeoutMillis value is negative");
    // Restore the origial java.lang.Object class data in 'new_class_data'
    memcpy(*new_class_data, class_data, *new_class_data_len);
    // If we found the execption message before, patch it in the new class data
    if (dest != NULL) {
      int index = dest - (char*)*new_class_data;
      const char* volker = "Volker is the best!            ";
      strncpy((char*)*new_class_data + index, volker, strlen(volker));
    }
  }
}

extern "C"
JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM* jvm, char* options, void* reserved) {
  jvmtiEnv* jvmti = NULL;

  if (options) pattern = strdup(options); // Options may contain the pattern

  jint result = jvm->GetEnv((void**) &jvmti, JVMTI_VERSION_1_1);
  if (result != JNI_OK) {
    fprintf(stderr, "Can't access JVMTI!\n");
    return JNI_ERR;
  }

  jvmtiCapabilities caps;
  memset(&caps, 0, sizeof(caps));
  caps.can_redefine_classes = 1;
  caps.can_generate_all_class_hook_events = 1;
  caps.can_generate_early_vmstart = 1;
  caps.can_generate_early_class_hook_events = 1;
  jvmti->AddCapabilities(&caps);

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
