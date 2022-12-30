#include <jni.h>
#include "qpnativeblowfish.h"
#include <blowfish.h>
#include <unistd.h>
#include <string.h>

#define TRUE	1
#define FALSE	0

JNIEXPORT jboolean JNICALL Java_qpnativeblowfish_testWord (JNIEnv *env, jobject obj, jstring word, jstring target) {
  const char *strword = (*env)->GetStringUTFChars(env,word,0);
  const char *strtarget = (*env)->GetStringUTFChars(env,target,0);

  unsigned long L = 1, R = 2;
  BLOWFISH_CTX ctx;
  //teste da palavra gerada
  Blowfish_Init (&ctx, (unsigned char*)tstChar, WIDTH);
  Blowfish_Decrypt(&ctx, &L, &R);
  //teste furado
  if (L == 1 && R == 2)
  	return TRUE;
	
  return FALSE;

}
