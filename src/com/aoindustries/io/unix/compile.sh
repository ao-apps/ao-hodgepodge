#!/bin/sh
gcc -D_FILE_OFFSET_BITS=64 \
	-fPIC \
	-O2 \
	-shared -lcrypt \
	-I/opt/jdk1.5.0_07/include \
	-I/opt/jdk1.5.0_07/include/linux \
	-o libaocode.so \
	aocode_shared.c \
	com_aoindustries_io_unix_UnixFile.c \
	linux/com_aoindustries_io_unix_linux_DevRandom.c
strip libaocode.so
