#!/bin/sh
gcc -D_FILE_OFFSET_BITS=64 \
	-O2 \
	-shared -lcrypt \
	-I/usr/j2sdk1.4.2_04/include \
	-I/usr/j2sdk1.4.2_04/include/linux \
	-o libaocode.so \
	aocode_shared.c \
	com_aoindustries_io_unix_UnixFile.c \
	linux/com_aoindustries_io_unix_linux_DevRandom.c
strip libaocode.so
