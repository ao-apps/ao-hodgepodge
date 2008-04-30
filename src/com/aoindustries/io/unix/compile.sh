#!/bin/sh
gcc -D_FILE_OFFSET_BITS=64 \
	-fPIC \
	-O2 \
	-shared -lcrypt \
	-I/opt/jdk1.6.0_05/include \
	-I/opt/jdk1.6.0_05/include/linux \
	-o libaocode.so \
	aocode_shared.c \
	jni_util.c \
	com_aoindustries_io_unix_UnixFile.c \
	linux/com_aoindustries_io_unix_linux_DevRandom.c || exit "$?"
strip libaocode.so || exit "$?"
