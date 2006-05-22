#include <jni.h>
#include "aocode_shared.h"
#include "com_aoindustries_io_unix_UnixFile.h"
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <utime.h>

extern int errno;

/*
 * Fills the stat structure, returning 0 if successful.  If not
 * successful, the exception is already thrown.
 */
int fillStat(JNIEnv* env, jbyteArray jfilename, struct stat* buff) {
    jclass newExcCls=NULL;
    int ret=-1;
    char* filename=getCharsForByteArray(env, jfilename);
    if(filename!=NULL) {
        if(lstat(filename, buff)==0) ret=0;
        else {
          newExcCls=(*env)->FindClass(env, getErrorType(errno));
        }
        free(filename);
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return ret;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    chown0
 * Signature: ([BII)V
 */
JNIEXPORT void JNICALL Java_com_aoindustries_io_unix_UnixFile_chown0(JNIEnv* env, jclass cls, jbyteArray jfilename, jint uid, jint gid) {
    jclass newExcCls=NULL;
    if(jfilename!=NULL) {
        char* filename=getCharsForByteArray(env, jfilename);
        if(filename!=NULL) {
            if(lchown(filename, uid, gid)!=0) newExcCls=(*env)->FindClass(env, getErrorType(errno));
            free(filename);
        }
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    crypt0
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_aoindustries_io_unix_UnixFile_crypt0(JNIEnv* env, jclass cls, jstring jpassword, jstring jsalt) {
    jclass newExcCls=NULL;
    jstring jcrypted=NULL;
    const char* password=(*env)->GetStringUTFChars(env, jpassword, NULL);
    if(password!=NULL) {
        const char* salt=(*env)->GetStringUTFChars(env, jsalt, NULL);
        if(salt!=NULL) {
            char* crypted=(char*)crypt(password, salt);
            if(crypted!=NULL) jcrypted=(*env)->NewStringUTF(env, crypted);
            else newExcCls=(*env)->FindClass(env, getErrorType(errno));
            (*env)->ReleaseStringUTFChars(env, jsalt, salt);
        }
        (*env)->ReleaseStringUTFChars(env, jpassword, password);
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return jcrypted;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getAccessTime0
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_com_aoindustries_io_unix_UnixFile_getAccessTime0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jlong st_atime0=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_atime0=(jlong)buff->st_atime;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_atime0;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getBlockCount0
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_com_aoindustries_io_unix_UnixFile_getBlockCount0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jlong st_blocks=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_blocks=(jlong)buff->st_blocks;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_blocks;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getBlockSize0
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_aoindustries_io_unix_UnixFile_getBlockSize0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jint st_blksize=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_blksize=(jint)buff->st_blksize;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_blksize;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getChangeTime0
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_com_aoindustries_io_unix_UnixFile_getChangeTime0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jlong st_ctime0=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_ctime0=(jlong)buff->st_ctime;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_ctime0;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getDevice0
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_com_aoindustries_io_unix_UnixFile_getDevice0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jlong st_dev=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_dev=(jlong)buff->st_dev;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_dev;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getDeviceIdentifier0
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_com_aoindustries_io_unix_UnixFile_getDeviceIdentifier0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jlong st_rdev=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_rdev=(jlong)buff->st_rdev;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_rdev;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getGID0
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_aoindustries_io_unix_UnixFile_getGID0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jint st_gid=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_gid=(jint)buff->st_gid;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_gid;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getInode0
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_com_aoindustries_io_unix_UnixFile_getInode0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jlong st_ino=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_ino=(jlong)buff->st_ino;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_ino;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getLinkCount0
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_aoindustries_io_unix_UnixFile_getLinkCount0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jint st_nlink=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_nlink=(jint)buff->st_nlink;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_nlink;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getMode0
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_com_aoindustries_io_unix_UnixFile_getMode0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jlong st_mode=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_mode=(jlong)buff->st_mode;
        free(buff);
    } else {
      newExcCls=(*env)->FindClass(env, getErrorType(errno));
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_mode;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getModifyTime0
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_com_aoindustries_io_unix_UnixFile_getModifyTime0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jlong st_mtime0=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_mtime0=(jlong)buff->st_mtime;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_mtime0;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    mktemp0
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_aoindustries_io_unix_UnixFile_mktemp0(JNIEnv* env, jclass cls , jstring jtemplate) {
    jclass newExcCls=NULL;
    jstring jfilename=NULL;
    const char* template=(*env)->GetStringUTFChars(env, jtemplate, NULL);
    if(template!=NULL) {
        int len=strlen(template)+1;
        char* filename=(char*)malloc(len);
        if(filename!=NULL) {
            memcpy(filename, template, len);
            {
                int fd=mkstemp(filename);
                if(fd!=-1) {
                    if(close(fd)==0) {
                        jfilename=(*env)->NewStringUTF(env, filename);
                    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
                } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
            }
            free(filename);
        } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
        (*env)->ReleaseStringUTFChars(env, jtemplate, template);
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return jfilename;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    getUID0
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_aoindustries_io_unix_UnixFile_getUID0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jint st_uid=0;
    struct stat* buff=(struct stat*)malloc(sizeof(struct stat));
    if(buff!=NULL) {
        if(fillStat(env, jfilename, buff)==0) st_uid=(jint)buff->st_uid;
        free(buff);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return st_uid;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    mknod0
 * Signature: ([BJJ)V
 */
JNIEXPORT void JNICALL Java_com_aoindustries_io_unix_UnixFile_mknod0(JNIEnv* env, jclass cls, jbyteArray jfilename, jlong mode, jlong device) {
    jclass newExcCls=NULL;
    char* filename=getCharsForByteArray(env, jfilename);
    if(filename!=NULL) {
        if(mknod(filename, mode, device)!=0) newExcCls=(*env)->FindClass(env, getErrorType(errno));
        free(filename);
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    mkfifo0
 * Signature: ([BJ)V
 */
JNIEXPORT void JNICALL Java_com_aoindustries_io_unix_UnixFile_mkfifo0(JNIEnv* env, jclass cls, jbyteArray jfilename, jlong mode) {
    jclass newExcCls=NULL;
    char* filename=getCharsForByteArray(env, jfilename);
    if(filename!=NULL) {
        if(mknod(filename, S_IFIFO|mode, 0)!=0) newExcCls=(*env)->FindClass(env, getErrorType(errno));
        free(filename);
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    setMode0
 * Signature: ([BJ)V
 */
JNIEXPORT void JNICALL Java_com_aoindustries_io_unix_UnixFile_setMode0(JNIEnv* env, jclass cls, jbyteArray jfilename, jlong mode) {
    jclass newExcCls=NULL;
    char* filename=getCharsForByteArray(env, jfilename);
    if(filename!=NULL) {
        if(chmod(filename, mode)!=0) newExcCls=(*env)->FindClass(env, getErrorType(errno));
        free(filename);
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    symLink0
 * Signature: ([B[B)V
 */
JNIEXPORT void JNICALL Java_com_aoindustries_io_unix_UnixFile_symLink0(JNIEnv* env, jclass cls, jbyteArray jfilename, jbyteArray jdestination) {
    jclass newExcCls=NULL;
    char* filename=getCharsForByteArray(env, jfilename);
    if(filename!=NULL) {
        char* deststr=getCharsForByteArray(env, jdestination);
        if(deststr!=NULL) {
            if(symlink(deststr, filename)!=0) newExcCls=(*env)->FindClass(env, getErrorType(errno));
            free(deststr);
        }
        free(filename);
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    link0
 * Signature: ([B[B)V
 */
JNIEXPORT void JNICALL Java_com_aoindustries_io_unix_UnixFile_link0(JNIEnv* env, jclass cls, jbyteArray jfilename, jbyteArray jdestination) {
    jclass newExcCls=NULL;
    char* filename=getCharsForByteArray(env, jfilename);
    if(filename!=NULL) {
        char* deststr=getCharsForByteArray(env, jdestination);
        if(deststr!=NULL) {
            if(link(deststr, filename)!=0) newExcCls=(*env)->FindClass(env, getErrorType(errno));
            free(deststr);
        }
        free(filename);
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    readLink0
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_aoindustries_io_unix_UnixFile_readLink0(JNIEnv* env, jclass cls, jbyteArray jfilename) {
    jclass newExcCls=NULL;
    jbyteArray jdestination=NULL;
    char* filename=getCharsForByteArray(env, jfilename);
    if(filename!=NULL) {
        char* destination=(char*)malloc(4097);
        if(destination!=NULL) {
            int charCount=readlink(filename, destination, 4096);
            if(charCount!=-1) {
                destination[charCount]='\0';
                jdestination=getByteArrayForChars(env, destination);
            } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
            free(destination);
        } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
        free(filename);
    }
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return jdestination;
}

/*
 * Class:     com_aoindustries_io_unix_UnixFile
 * Method:    utime0
 * Signature: ([BJJ)V
 */
JNIEXPORT void JNICALL Java_com_aoindustries_io_unix_UnixFile_utime0(JNIEnv* env, jclass cls, jbyteArray jfilename, jlong atime, jlong mtime) {
    jclass newExcCls = NULL;
    struct utimbuf* times=(struct utimbuf*)malloc(sizeof(struct utimbuf));
    if(times!=NULL) {
        char* filename=getCharsForByteArray(env, jfilename);
        if(filename!=NULL) {
            times->actime=atime;
            times->modtime=mtime;
            if(utime(filename, times)!=0) newExcCls=(*env)->FindClass(env, getErrorType(errno));
            free(filename);
        }
        free(times);
    } else newExcCls=(*env)->FindClass(env, getErrorType(errno));
    if(newExcCls!=NULL) (*env)->ThrowNew(env, newExcCls, strerror(errno));
    return;
}
