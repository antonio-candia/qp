#include <jni.h>
#include "qpnativecrypt.h"
#include <unistd.h>
#include <string.h>

#define TRUE	1
#define FALSE	0

JNIEXPORT jboolean JNICALL Java_qpnativecrypt_testWord (JNIEnv *env, jobject obj, jstring word, jstring target) {
  const char *strword = (*env)->GetStringUTFChars(env,word,0);
  const char *strtarget = (*env)->GetStringUTFChars(env,target,0);
  
  if (strcmp(crypt(strword,strtarget), strtarget) == 0)
  	return TRUE;
	
  return FALSE;

}
