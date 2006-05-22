#include <jni.h>
#include "../aocode_shared.h"
#include "com_aoindustries_io_unix_linux_DevRandom.h"
#include <errno.h>
#include <fcntl.h>
#include <linux/types.h>
#include <linux/random.h>
//include <stdlib.h>
#include <string.h>
//include <sys/ioctl.h>
//include <sys/types.h>
//include <sys/stat.h>

extern int errno;

/*
 * Class:     com_aoindustries_io_unix_linux_DevRandom
 * Method:    addEntropy0
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_com_aoindustries_io_unix_linux_DevRandom_addEntropy0(JNIEnv* env, jclass cls, jbyteArray randomData) {
    jclass newExcCls=NULL;

    // First, build up the rand_pool_info structure
    jsize len = (*env)->GetArrayLength(env, randomData);
    struct rand_pool_info* rand_info=(struct rand_pool_info*)malloc(sizeof(struct rand_pool_info) + sizeof(char)*len);
    if(rand_info!=NULL) {
	rand_info->entropy_count=len<<3;
	rand_info->buf_size=len;
	jbyte* body=(*env)->GetByteArrayElements(env, randomData, 0);
	if(body!=NULL) {
	    int c;
	    for(c=0;c<len;c++) {
	        ((char*)rand_info->buf)[c]=body[c];
	    }
	    (*env)->ReleaseByteArrayElements(env, randomData, body, 0);

            // Second, add this random data to the kernel
            int fdout=open("/dev/random", O_WRONLY);
            if(fdout>0) {
                if(ioctl(fdout, RNDADDENTROPY, rand_info)!=0) newExcCls=(*env)->FindClass(env, getErrorType(errno));
	        close(fdout);
	    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
	} else newExcCls=(*env)->FindClass(env, getErrorType(errno));
	free(rand_info);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));

    // Throw any exceptions
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return;
}
