#include <jni.h>
#include <errno.h>
//include <stdlib.h>
#include <string.h>
#include "aocode_shared.h"

extern int errno;

// Gets the proper exception type for the provided errno
const char* getErrorType(const int err) {
    const char* errString;
    if(err==EACCES) errString=SECURITY_EXCEPTION;
    else if(err==EBADF) errString=IO_EXCEPTION;
    else if(err==EEXIST) errString=IO_EXCEPTION;
    else if(err==EFAULT) errString=RUNTIME_EXCEPTION;
    else if(err==EINTR) errString=INTERRUPTED_IO_EXCEPTION;
    else if(err==EINVAL) errString=ILLEGAL_ARGUMENT_EXCEPTION;
    else if(err==EIO) errString=IO_EXCEPTION;
    else if(err==ELOOP) errString=FILE_NOT_FOUND_EXCEPTION;
    else if(err==EMLINK) errString=IO_EXCEPTION;
    else if(err==ENAMETOOLONG) errString=ILLEGAL_ARGUMENT_EXCEPTION;
    else if(err==ENOENT) errString=FILE_NOT_FOUND_EXCEPTION;
    else if(err==ENOMEM) errString=OUT_OF_MEMORY_EXCEPTION;
    else if(err==ENOSPC) errString=IO_EXCEPTION;
    else if(err==ENOSYS) errString=NO_SUCH_METHOD_EXCEPTION;
    else if(err==ENOTDIR) errString=IO_EXCEPTION;
    else if(err==EPERM) errString=SECURITY_EXCEPTION;
    else if(err==EROFS) errString=IO_EXCEPTION;
    else if(err==EXDEV) errString=IO_EXCEPTION;
    else errString=RUNTIME_EXCEPTION;
    return errString;
}

/*
 * Converts a jbyteArray to a newly-allocated char*
 *
 * @return NULL on error, errno set accordingly and exception thrown
 */
char* getCharsForByteArray(JNIEnv* env, jbyteArray bytes) {
    jclass newExcCls=NULL;
    jsize len = (*env)->GetArrayLength(env, bytes);
    char* chars=malloc(sizeof(char)*(len+1));
    if(chars!=NULL) {
        int c;
        jbyte* body=(*env)->GetByteArrayElements(env, bytes, 0);
        if(body!=NULL) {
            for(c=0;c<len;c++) {
                chars[c]=body[c];
            }
            chars[c]='\0';
            (*env)->ReleaseByteArrayElements(env, bytes, body, 0);
        } else {
            newExcCls=(*env)->FindClass(env, getErrorType(errno));
            free(chars);
            chars=NULL;
        }
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return chars;
}

/*
 * Converts a char* to a new jbyteArray
 *
 * @return NULL on error, errno set accordingly and exception thrown
 */
jbyteArray getByteArrayForChars(JNIEnv* env, char* chars) {
    jclass newExcCls=NULL;
    jbyteArray bytes=NULL;
    int len=0;
    while(chars[len]!='\0') len++;
    bytes=(*env)->NewByteArray(env, len);
    if(bytes!=NULL) {
        (*env)->SetByteArrayRegion(env, bytes, 0, len, chars);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    return bytes;
}
