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
