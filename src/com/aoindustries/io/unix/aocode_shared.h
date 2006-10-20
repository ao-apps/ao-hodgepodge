#include <jni.h>
#ifndef _Included_aocode_shared
#define _Included_aocode_shared
#ifdef __cplusplus
extern "C" {
#endif
static const char* FILE_NOT_FOUND_EXCEPTION="java/io/FileNotFoundException";
static const char* IO_EXCEPTION="java/io/IOException";
static const char* ILLEGAL_ARGUMENT_EXCEPTION="java/lang/IllegalArgumentException";
static const char* INTERRUPTED_IO_EXCEPTION="java/io/InterruptedIOException";
static const char* NO_SUCH_METHOD_EXCEPTION="java/lang/NoSuchMethodException";
static const char* OUT_OF_MEMORY_EXCEPTION="java/lang/OutOfMemoryError";
static const char* RUNTIME_EXCEPTION="java/lang/RuntimeException";
static const char* SECURITY_EXCEPTION="java/lang/SecurityException";

extern const char* getErrorType(const int err);
#ifdef __cplusplus
}
#endif
#endif
