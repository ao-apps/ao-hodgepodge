package com.aoindustries.io.unix;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Access and modify all the Unix specific file attributes.  These updates are made using
 * a Linux shared library provided as a resource.  The source code is also supplied.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class UnixFile {

    /**
     * The minimum UID that is considered a normal user.
     */
    public static final int MINIMUM_USER_UID = 500;

    /**
     * The minimum GID that is considered a normal user.
     */
    public static final int MINIMUM_USER_GID = 500;

    /**
     * The UID of the root user.
     */
    public static final int ROOT_UID = 0;

    /**
     * The GID of the root user.
     */
    public static final int ROOT_GID = 0;

    /**
     * The mode mask for just the file permissions.
     */
    public static final long PERMISSION_MASK=07777;

    /**
     * World execute permissions.
     */
    public static final long OTHER_EXECUTE = 01;
    public static final long NOT_OTHER_EXECUTE = -1L - OTHER_EXECUTE;

    /**
     * World write permissions.
     */
    public static final long OTHER_WRITE = 02;
    public static final long NOT_OTHER_WRITE = -1L - OTHER_WRITE;

    /**
     * World read permission.
     */
    public static final long OTHER_READ = 04;
    public static final long NOT_OTHER_READ = -1L - OTHER_READ;

    /**
     * Group execute permissions.
     */
    public static final long GROUP_EXECUTE = 010;
    public static final long NOT_GROUP_EXECUTE = -1L - GROUP_EXECUTE;

    /**
     * Group write permissions.
     */
    public static final long GROUP_WRITE = 020;
    public static final long NOT_GROUP_WRITE = -1L - GROUP_WRITE;

    /**
     * Group read permissions.
     */
    public static final long GROUP_READ = 040;
    public static final long NOT_GROUP_READ = -1L - GROUP_READ;

    /**
     * Owner execute permissions.
     */
    public static final long USER_EXECUTE = 0100;
    public static final long NOT_USER_EXECUTE = -1L - USER_EXECUTE;

    /**
     * Owner write permissions.
     */
    public static final long USER_WRITE = 0200;
    public static final long NOT_USER_WRITE = -1L - USER_WRITE;

    /**
     * Owner read permissions.
     */
    public static final long USER_READ = 0400;
    public static final long NOT_USER_READ = -1L - USER_READ;

    /**
     * Save text image.
     */
    public static final long SAVE_TEXT_IMAGE = 01000;
    public static final long NOT_SAVE_TEXT_IMAGE = -1L - SAVE_TEXT_IMAGE;

    /**
     * Set GID on execute.
     */
    public static final long SET_GID = 02000;
    public static final long NOT_SET_GID = -1L - SET_GID;

    /**
     * Set UID on execute.
     */
    public static final long SET_UID = 04000;
    public static final long NOT_SET_UID = -1L - SET_UID;

    /**
     * The mode mask for just the file type.
     */
    public static final long TYPE_MASK=0170000;

    /**
     * Is a FIFO.
     */
    public static final long IS_FIFO = 010000;

    /**
     * Is a character special device.
     */
    public static final long IS_CHARACTER_DEVICE = 020000;

    /**
     * Is a directory.
     */
    public static final long IS_DIRECTORY = 040000;

    /**
     * Is a block device.
     */
    public static final long IS_BLOCK_DEVICE = 060000;

    /**
     * Is a regular file.
     */
    public static final long IS_REGULAR_FILE = 0100000;

    /**
     * Is a symbolic link.
     */
    public static final long IS_SYM_LINK = 0120000;

    /**
     * Is a socket.
     */
    public static final long IS_SOCKET = 0140000;

    volatile private static boolean loaded=false;
    public static void loadLibrary() {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "loadLibrary()", null);
        try {
            if(!loaded) {
                synchronized(UnixFile.class) {
                    if(!loaded) {
                        System.loadLibrary("aocode");
                        loaded=true;
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * The filename.
     */
    protected final String filename;
    
    volatile private File file;

    /**
     * Strictly requires the parent to be a directory if it exists.
     *
     * @deprecated  Please call #UnixFile(UnixFile,String,boolean) to explicitly control whether strict parent checking is performed
     */
    public UnixFile(UnixFile parent, String filename) throws IOException {
        this(parent, filename, true);
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "<init>(UnixFile,String)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    /**
     * When strictly checking, a parent must be a directory if it exists.
     */
    public UnixFile(UnixFile parent, String filename, boolean strict) throws IOException {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "<init>(UnixFile,String)", null);
        try {
            if(parent==null) throw new NullPointerException("parent is null");
            if(strict && parent.exists() && !parent.isDirectory()) throw new IOException("parent is not a directory: " + parent.filename);
            if(parent.filename.equals("/")) this.filename=parent.filename+filename;
            else this.filename = parent.filename + '/' + filename;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public UnixFile(File file) {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "<init>(File)", null);
        try {
            if(file==null) throw new NullPointerException("file is null");
            this.filename = file.getPath();
            this.file=file;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public UnixFile(File parent, String filename) {
        this(parent.getPath(), filename);
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "<init>(File,String)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public UnixFile(String filename) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "<init>(String)", null);
        try {
            this.filename = filename;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public UnixFile(String parent, String filename) {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "<init>(String,String)", null);
        try {
            if(parent.equals("/")) this.filename=parent+filename;
            else this.filename = parent + '/' + filename;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Ensures that the calling thread is allowed to read this
     * <code>UnixFile</code> in any way.
     */
    final public void checkRead() throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "checkRead()", null);
        try {
            SecurityManager security=System.getSecurityManager();
            if(security!=null) security.checkRead(getFile().getCanonicalPath());
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Ensures that the calling thread is allowed to read this
     * <code>filename</code> in any way.
     */
    static public void checkRead(String filename) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "checkRead(String)", null);
        try {
            SecurityManager security=System.getSecurityManager();
            if(security!=null) security.checkRead(new File(filename).getCanonicalPath());
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Ensures that the calling thread is allowed to write to or modify this
     * <code>UnixFile</code> in any way.
     */
    final public void checkWrite() throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "checkWrite()", null);
        try {
            SecurityManager security=System.getSecurityManager();
            if(security!=null) security.checkWrite(getFile().getCanonicalPath());
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Ensures that the calling thread is allowed to write to or modify this
     * <code>filename</code> in any way.
     */
    static public void checkWrite(String filename) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "checkWrite(String)", null);
        try {
            SecurityManager security=System.getSecurityManager();
            if(security!=null) security.checkWrite(new File(filename).getCanonicalPath());
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Changes both the owner and group for a file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     */
    final public UnixFile chown(int uid, int gid) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "chown(int,int)", null);
        try {
            checkWrite();
            loadLibrary();
            chown0(filename, uid, gid);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    private static native void chown0(String filename, int uid, int gid) throws IOException;

    /**
     * Stats the file.  Please consider calling getStat(Stat) to avoid memory allocation.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     */
    public Stat getStat() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getStat()", null);
        try {
            return getStat(null);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Stats the file into the provided Stat buffer.  If no buffer is provided, a new one will be created.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     */
    public Stat getStat(Stat stat) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "getStat(Stat)", null);
        try {
            checkRead();
            loadLibrary();
            if(stat==null) stat=new Stat();
            getStat0(filename, stat);
            return stat;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    // Combine to one native method
    /*private static native long getDevice0(String filename) throws IOException;
    private static native long getInode0(String filename) throws IOException;
    private static native long getMode0(String filename) throws IOException;
    private static native int getLinkCount0(String filename) throws IOException;
    private static native int getUID0(String filename) throws IOException;
    private static native int getGID0(String filename) throws IOException;
    private static native long getDeviceIdentifier0(String filename) throws IOException;
    private static native int getBlockSize0(String filename) throws IOException;
    private static native long getBlockCount0(String filename) throws IOException;
    private static native long getAccessTime0(String filename) throws IOException;
    private static native long getModifyTime0(String filename) throws IOException;
    private static native long getChangeTime0(String filename) throws IOException;
    */
    private native void getStat0(String filename, Stat stat) throws IOException;

    /**
     * Compares this contents of this file to the contents of another file.
     *
     * This method will follow both path symbolic links and a final symbolic link.
     */
    public boolean contentEquals(UnixFile otherUF) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "contentEquals(UnixFile)", null);
        try {
            Stat stat = getStat();
            if(!stat.isRegularFile()) throw new IOException("Not a regular file: "+filename);
            Stat otherStat = otherUF.getStat();
            if(!otherStat.isRegularFile()) throw new IOException("Not a regular file: "+otherUF.filename);
            long size=stat.getSize();
            if(size!=otherStat.getSize()) return false;
            int buffSize=size<BufferManager.BUFFER_SIZE?(int)size:BufferManager.BUFFER_SIZE;
            if(buffSize<64) buffSize=64;
            InputStream in1=new BufferedInputStream(new FileInputStream(getFile()), buffSize);
            try {
                InputStream in2=new BufferedInputStream(new FileInputStream(otherUF.getFile()), buffSize);
                try {
                    while(true) {
                        int b1=in1.read();
                        int b2=in2.read();
                        if(b1!=b2) return false;
                        if(b1==-1) break;
                    }
                } finally {
                    in2.close();
                }
            } finally {
                in1.close();
            }
            return true;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Compares the contents of a file to a byte[].
     *
     * This method will follow both path symbolic links and a final symbolic link.
     */
    public boolean contentEquals(byte[] otherFile) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "contentEquals(byte[])", null);
        try {
            Stat stat = getStat();
            if(!stat.isRegularFile()) throw new IOException("Not a regular file: "+filename);
            long size=stat.getSize();
            if(size!=otherFile.length) return false;
            int buffSize=size<BufferManager.BUFFER_SIZE?(int)size:BufferManager.BUFFER_SIZE;
            if(buffSize<64) buffSize=64;
            InputStream in1=new BufferedInputStream(new FileInputStream(getFile()), buffSize);
            try {
                for(int c=0;c<otherFile.length;c++) {
                    int b1=in1.read();
                    int b2=otherFile[c]&0xff;
                    if(b1!=b2) return false;
                }
            } finally {
                in1.close();
            }
            return true;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Compares this contents of this file to the contents of another file.
     *
     * This method will not follow any symbolic links and is not subject to race conditions.
     */
    public boolean secureContentEquals(UnixFile otherUF) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "contentEquals(UnixFile)", null);
        try {
            Stat stat = getStat();
            if(!stat.isRegularFile()) throw new IOException("Not a regular file: "+filename);
            Stat otherStat = otherUF.getStat();
            if(!otherStat.isRegularFile()) throw new IOException("Not a regular file: "+otherUF.filename);
            long size=stat.getSize();
            if(size!=otherStat.getSize()) return false;
            int buffSize=size<BufferManager.BUFFER_SIZE?(int)size:BufferManager.BUFFER_SIZE;
            if(buffSize<64) buffSize=64;
            InputStream in1=new BufferedInputStream(getSecureInputStream(), buffSize);
            try {
                InputStream in2=new BufferedInputStream(otherUF.getSecureInputStream(), buffSize);
                try {
                    while(true) {
                        int b1=in1.read();
                        int b2=in2.read();
                        if(b1!=b2) return false;
                        if(b1==-1) break;
                    }
                } finally {
                    in2.close();
                }
            } finally {
                in1.close();
            }
            return true;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Compares the contents of a file to a byte[].
     *
     * This method will not follow any symbolic links and is not subject to race conditions.
     */
    public boolean secureContentEquals(byte[] otherFile) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "contentEquals(byte[])", null);
        try {
            Stat stat = getStat();
            if(!stat.isRegularFile()) throw new IOException("Not a regular file: "+filename);
            long size=stat.getSize();
            if(size!=otherFile.length) return false;
            int buffSize=size<BufferManager.BUFFER_SIZE?(int)size:BufferManager.BUFFER_SIZE;
            if(buffSize<64) buffSize=64;
            InputStream in1=new BufferedInputStream(getSecureInputStream(), buffSize);
            try {
                for(int c=0;c<otherFile.length;c++) {
                    int b1=in1.read();
                    int b2=otherFile[c]&0xff;
                    if(b1!=b2) return false;
                }
            } finally {
                in1.close();
            }
            return true;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    volatile private static Random random;
    private static Random getRandom() {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "getRandom()", null);
        try {
            if(random==null) {
                synchronized(UnixFile.class) {
                    if(random==null) {
                        String algorithm="SHA1PRNG";
                        try {
                            random=SecureRandom.getInstance(algorithm);
                        } catch(NoSuchAlgorithmException err) {
                            throw new WrappedException(err, new Object[] {"algorithm="+algorithm});
                        }
                    }
                }
            }
            return random;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Copies one filesystem object to another.  It supports block devices, directories, fifos, regular files, and symbolic links.  Directories are not
     * copied recursively.
     *
     * This method will follow both path symbolic links and a final symbolic link.
     */
    public void copyTo(UnixFile otherUF, boolean overwrite) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "copyTo(UnixFile,boolean)", null);
        try {
            checkRead();
            otherUF.checkWrite();
            Stat stat = getStat();
            long mode=stat.getRawMode();
            Stat otherStat = otherUF.getStat();
            boolean oExists=otherStat.exists();
            if(!overwrite && oExists) throw new IOException("File already exists: "+otherUF);
            if(isBlockDevice(mode) || isCharacterDevice(mode)) {
                if(oExists) otherUF.delete();
                otherUF.mknod(mode, stat.getDeviceIdentifier()).chown(stat.getUID(), stat.getGID());
            } else if(isDirectory(mode)) {
                if(!oExists) otherUF.mkdir();
                otherUF.setMode(mode).chown(stat.getUID(), stat.getGID());
            } else if(isFIFO(mode)) {
                if(oExists) otherUF.delete();
                otherUF.mkfifo(mode).chown(stat.getUID(), stat.getGID());
            } else if(isRegularFile(mode)) {
                InputStream in=new FileInputStream(getFile());
                try {
                    OutputStream out=new FileOutputStream(otherUF.getFile());
                    try {
                        otherUF.setMode(mode).chown(stat.getUID(), stat.getGID());
                        byte[] buff=BufferManager.getBytes();
                        try {
                            int ret;
                            while((ret=in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) out.write(buff, 0, ret);
                        } finally {
                            BufferManager.release(buff);
                        }
                    } finally {
                        out.close();
                    }
                } finally {
                    in.close();
                }
            } else if(isSocket(mode)) throw new IOException("Unable to copy socket: "+filename);
            else if(isSymLink(mode)) {
                // This takes the byte[] from readLink directory to symLink to avoid conversions from byte[]->String->byte[]
                otherUF.symLink(readLink()).chown(stat.getUID(), stat.getGID());
            } else throw new RuntimeException("Unknown mode type: "+Long.toOctalString(mode));
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * crypt is not thread safe due to static data in the return value
     */
    private static final Object cryptLock = new Object();

    /**
     * Hashes a password using the MD5 crypt algorithm and the internal random source.
     */
    public static String crypt(String password) {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "crypt(String)", null);
        try {
            // crypt is not thread safe due to static data in the return value
            synchronized(cryptLock) {
                return crypt(password, getRandom());
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Hashes a password using the MD5 crypt algorithm and the provided random source.
     */
    public static String crypt(String password, Random random) {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "crypt(String,Random)", null);
        try {
            StringBuilder salt=new StringBuilder(11);
            salt.append("$1$");
            for(int c=0;c<8;c++) {
                int num=random.nextInt(64);
                if(num<10) salt.append((char)(num+'0'));
                else if(num<36) salt.append((char)(num-10+'A'));
                else if(num<62) salt.append((char)(num-36+'a'));
                else if(num==62) salt.append('.');
                else salt.append('/');
            }
            return crypt(password, salt.toString());
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Hashes a password using the provided salt.  If the salt starts with $1$ MD5 crypt will be used, otherwise
     * standard (not very secure) Unix crypt will be used.
     */
    public static String crypt(String password, String salt) {
        Profiler.startProfile(Profiler.UNKNOWN, UnixFile.class, "crypt(String,String)", null);
        try {
            synchronized(UnixFile.class) {
                loadLibrary();
                return crypt0(password, salt);
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    private static native String crypt0(String password, String salt);

    /**
     * Deletes this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @see  java.io.File#delete
     */
    final public void delete() throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "delete()", null);
        try {
            if (!getFile().delete()) throw new IOException("Unable to delete file: " + filename);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Deletes this file and if it is a directory, all files below it.
     *
     * Due to a race conditition, this method will follow symbolic links.  Please use
     * <code>secureDeleteRecursive</code> instead.
     *
     * @see  java.io.File#delete
     */
    final public void deleteRecursive() throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "deleteRecursive()", null);
        try {
            deleteRecursive(this);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @see  #deleteRecursive()
     */
    private static void deleteRecursive(UnixFile file) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "deleteRecursive(UnixFile)", null);
        try {
            try {
                Stat stat = file.getStat();
                // This next line matches directories specifically to avoid listing and recursing into symlink references
                if(stat.isDirectory()) {
                    // TODO: Race condition between getStat and list(), how can we avoid this from pure Java???
                    String[] list = file.list();
                    if (list != null) {
                        int len = list.length;
                        for (int c = 0; c < len; c++) deleteRecursive(new UnixFile(file, list[c], false));
                    }
                }
                file.delete();
            } catch(FileNotFoundException err) {
                // OK if it was deleted while we're trying to delete it
            } catch(IOException err) {
                System.err.println("Error recursively delete: "+file.filename);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    private static class SecuredDirectory {
        private UnixFile directory;
        private long mode;
        private int uid, gid;
        
        private SecuredDirectory(UnixFile directory, long mode, int uid, int gid) {
            this.directory=directory;
            this.mode=mode;
            this.uid=uid;
            this.gid=gid;
        }
    }

    final public void secureParents(List<SecuredDirectory> parentsChanged) throws IOException {
        // Build a stack of all parents
        Stack<UnixFile> parents=new Stack<UnixFile>();
        {
            UnixFile parent=getParent();
            while(!parent.isRootDirectory()) {
                parents.push(parent);
                parent=parent.getParent();
            }
        }
        // Set any necessary permissions from root to file's immediate parent while looking for symbolic links
        Stat parentStat = new Stat();
        while(!parents.isEmpty()) {
            UnixFile parent=parents.pop();
            parent.getStat(parentStat);
            long statMode = parentStat.getRawMode();
            if(isSymLink(statMode)) throw new IOException("Symbolic link found in path: "+parent.getFilename());
            int uid=parentStat.getUID();
            int gid=parentStat.getGID();
            if(
                uid>=MINIMUM_USER_UID
                || gid>=MINIMUM_USER_GID
                || (statMode&(OTHER_WRITE|SET_GID|SET_UID))!=0
            ) {
                parentsChanged.add(new SecuredDirectory(parent, statMode, uid, gid));
                parent
                    .setMode(statMode&(NOT_OTHER_WRITE & NOT_SET_GID & NOT_SET_UID))
                    .chown(
                        uid>=MINIMUM_USER_UID ? ROOT_UID : uid,
                        gid>=MINIMUM_USER_GID ? ROOT_GID : gid
                    )
                ;
            }
        }
    }

    final public void restoreParents(List<SecuredDirectory> parentsChanged) throws IOException {
        for(int c=parentsChanged.size()-1;c>=0;c--) {
            SecuredDirectory directory=parentsChanged.get(c);
            directory.directory.chown(directory.uid, directory.gid).setMode(directory.mode);
        }
    }

    /**
     * Securely deletes this file entry and all files below it while not following symbolic links.  This method must be called with
     * root privileges to properly avoid race conditions.  If not running with root privileges, use <code>deleteRecursive</code> instead.<br>
     * <br>
     * In order to avoid race conditions, all directories above this directory will have their permissions set
     * so that regular users cannot modify the directories.  After each parent directory has its permissions set
     * it will then check for symbolic links.  Once all of the parent directories have been completed, the filesystem
     * will recursively have its permissions reset, scans for symlinks, and deletes performed in such a way all
     * race conditions are avoided.  Finally, the parent directory permissions that were modified will be restored.
     *
     * @see  java.io.File#delete
     */
    final public void secureDeleteRecursive() throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "secureDeleteRecursive()", null);
        try {
            List<SecuredDirectory> parentsChanged=new ArrayList<SecuredDirectory>();
            try {
                secureParents(parentsChanged);
                secureDeleteRecursive(this);
            } finally {
                restoreParents(parentsChanged);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @see  #secureDeleteRecursive()
     */
    private static void secureDeleteRecursive(UnixFile file) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "secureDeleteRecursive(UnixFile)", null);
        try {
            try {
                Stat stat = file.getStat();
                long mode=stat.getRawMode();
                // Race condition does not exist because the parents have been secured already
                if(!isSymLink(mode) && isDirectory(mode)) {
                    // Secure the current directory before the recursive calls
                    if(stat.getUID()!=ROOT_UID || stat.getGID()!=ROOT_GID) file.chown(ROOT_UID, ROOT_GID);
                    if(stat.getMode()!=0700) file.setMode(0700);
                    String[] list = file.list();
                    if (list != null) {
                        int len = list.length;
                        for (int c = 0; c < len; c++) secureDeleteRecursive(new UnixFile(file, list[c]));
                    }
                }
                file.delete();
            } catch(IOException err) {
                System.err.println("Error recursively delete: "+file.filename);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Determines if a file exists, a symbolic link with an invalid destination
     * is still considered to exist.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).exists()
     */
    final public boolean exists() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "exists()", null);
        try {
            return getStat().exists();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the last access to this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getAccessTime()
     */
    final public long getAccessTime() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getAccessTime()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getAccessTime();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the block count for this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(State).getBlockCount()
     */
    final public long getBlockCount() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getBlockCount()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getBlockCount();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the block size for this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getBlockSize()
     */
    final public int getBlockSize() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getBlockSize()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getBlockSize();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the change time of this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getChangeTime()
     */
    final public long getChangeTime() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getChangeTime()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getChangeTime();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the device for this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getDevice()
     */
    final public long getDevice() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getDevice()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getDevice();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the device identifier for this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getDeviceIdentifier()
     */
    final public long getDeviceIdentifier() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getDeviceIdentifier()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getDeviceIdentifier();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the extension from the filename.
     */
    public static String getExtension(String filename) {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "getExtension(String)", null);
        try {
            int pos=filename.lastIndexOf('.');
            if(pos<1) return "";
            // If a / follows the ., then no extension
            int pos2=filename.indexOf('/', pos+1);
            if(pos2!=-1) return "";
            return filename.substring(pos+1);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    /**
     * Gets the extension for this file.
     */
    final public String getExtension() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getExtension()", null);
        try {
            return getExtension(filename);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the <code>File</code> for this <code>UnixFile</code>.
     */
    final public File getFile() {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "getFile()", null);
        try {
            if (file == null) {
                synchronized (this) {
                    if (file == null) file = new File(filename);
                }
            }
            return file;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the filename for this <code>UnixFile</code>.
     */
    final public String getFilename() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getFilename()", null);
        try {
            return filename;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the group ID for this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getGID()
     */
    final public int getGID() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getGID()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getGID();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the inode for this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getInode()
     */
    final public long getInode() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getInode()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getInode();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the link count for this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getNumberLinks()
     */
    final public int getLinkCount() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getLinkCount()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getNumberLinks();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the permission bits of the mode of this file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getMode()
     */
    final public long getMode() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getMode()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getMode();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets a String representation of a mode similar to the output of the Unix ls command.
     */
    public static String getModeString(long mode) {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "getModeString(long)", null);
        try {
            StringBuilder SB=new StringBuilder(10);
            if(isFIFO(mode)) SB.append('p');
            else if(isCharacterDevice(mode)) SB.append('c');
            else if(isDirectory(mode)) SB.append('d');
            else if(isBlockDevice(mode)) SB.append('b');
            else if(isRegularFile(mode)) SB.append('-');
            else if(isSymLink(mode)) SB.append('l');
            else if(isSocket(mode)) SB.append('s');
            else throw new IllegalArgumentException("Unknown mode type: "+Long.toOctalString(mode));
            
            return SB
                .append((mode&USER_READ)!=0?'r':'-')
                .append((mode&USER_WRITE)!=0?'w':'-')
                .append((mode&USER_EXECUTE)!=0?((mode&SET_UID)!=0?'s':'x'):((mode&SET_UID)!=0?'S':'-'))
                .append((mode&GROUP_READ)!=0?'r':'-')
                .append((mode&GROUP_WRITE)!=0?'w':'-')
                .append((mode&GROUP_EXECUTE)!=0?((mode&SET_GID)!=0?'s':'x'):((mode&SET_GID)!=0?'S':'-'))
                .append((mode&OTHER_READ)!=0?'r':'-')
                .append((mode&OTHER_WRITE)!=0?'w':'-')
                .append((mode&OTHER_EXECUTE)!=0?((mode&SAVE_TEXT_IMAGE)!=0?'t':'x'):((mode&SAVE_TEXT_IMAGE)!=0?'T':'-'))
                .toString()
            ;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets a String representation of the mode of this file similar to the output of the Unix ls command.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getModeString()
     */
    final public String getModeString() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getModeString()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getModeString();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Securely gets a <code>FileInputStream</code> to this file, temporarily performing permission
     * changes and ensuring that no symbolic links are anywhere in the path.
     */
    final public FileInputStream getSecureInputStream() throws IOException {
        Profiler.startProfile(Profiler.UNKNOWN, UnixFile.class, "getSecureInputStream()", null);
        try {
            List<SecuredDirectory> parentsChanged=new ArrayList<SecuredDirectory>();
            try {
                secureParents(parentsChanged);

                // Make sure the file does not exist
                if(!getStat().isRegularFile()) throw new IOException("Not a regular file: "+filename);

                // Create the new file with the correct owner and permissions
                return new FileInputStream(getFile());
            } finally {
                restoreParents(parentsChanged);
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Securely gets a <code>FileOutputStream</code> to this file, temporarily performing permission
     * changes and ensuring that no symbolic links are anywhere in the path.
     */
    final public FileOutputStream getSecureOutputStream(int uid, int gid, long mode, boolean overwrite) throws IOException {
        Profiler.startProfile(Profiler.UNKNOWN, UnixFile.class, "getSecureOutputStream(int,int,int,boolean)", null);
        try {
            List<SecuredDirectory> parentsChanged=new ArrayList<SecuredDirectory>();
            try {
                secureParents(parentsChanged);

                // Make sure the file does not exist
                Stat stat = getStat();
                if(overwrite) {
                    if(stat.exists() && !stat.isRegularFile()) throw new IOException("Not a regular file: "+filename);
                } else {
                    if(stat.exists()) throw new IOException("File already exists: "+filename);
                }

                // Create the new file with the correct owner and permissions
                FileOutputStream out=new FileOutputStream(getFile());
                chown(uid, gid).setMode(mode);
                return out;
            } finally {
                restoreParents(parentsChanged);
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Securely gets a <code>RandomAccessFile</code> to this file, temporarily performing permission
     * changes and ensuring that no symbolic links are anywhere in the path.
     */
    final public RandomAccessFile getSecureRandomAccessFile(String mode) throws IOException {
        Profiler.startProfile(Profiler.UNKNOWN, UnixFile.class, "getSecureRandomAccessFile(String)", null);
        try {
            List<SecuredDirectory> parentsChanged=new ArrayList<SecuredDirectory>();
            try {
                secureParents(parentsChanged);

                // Make sure the file does not exist
                if(!getStat().isRegularFile()) throw new IOException("Not a regular file: "+filename);

                // Create the new file with the correct owner and permissions
                return new RandomAccessFile(getFile(), mode);
            } finally {
                restoreParents(parentsChanged);
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Gets the parent of this file.
     */
    final public UnixFile getParent() {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "getParent()", null);
        try {
            return new UnixFile(getFile().getParentFile());
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the complete mode of the file, including the bits representing the
     * file type.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getRawMode()
     */
    final public long getStatMode() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getStatMode()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getRawMode();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the modification time of the file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getModifyTime()
     */
    final public long getModifyTime() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getModifyTime()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getModifyTime();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the size of the file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getSize()
     */
    final public long getSize() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "getSize()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getSize();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Securely creates a temporary file.  In order to be secure, though, the directory
     * needs to be secure, or at least have the sticky bit set.
     *
     * This method will follow symbolic links in the path but not final links.
     */
    public static UnixFile mktemp(String template) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "mktemp(String)", null);
        try {
            String filename=template+"XXXXXX";
            checkWrite(filename);
            loadLibrary();
            return new UnixFile(mktemp0(filename));
        } catch(IOException err) {
            System.err.println("UnixFile.mktemp: IOException: template="+template);
            throw err;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    private static native String mktemp0(String template) throws IOException;

    /**
     * Gets the user ID of the file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).getUID()
     */
    public final int getUID() throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "getUID()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.getUID();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Determines if a specific mode represents a block device.
     */
    public static boolean isBlockDevice(long mode) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isBlockDevice(long)", null);
        try {
            return (mode & TYPE_MASK) == IS_BLOCK_DEVICE;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if this file represents a block device.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).isBlockDevice()
     */
    final public boolean isBlockDevice() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isBlockDevice()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.isBlockDevice();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if a specific mode represents a character device.
     */
    public static boolean isCharacterDevice(long mode) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isCharacterDevice(long)", null);
        try {
            return (mode & TYPE_MASK) == IS_CHARACTER_DEVICE;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if this file represents a character device.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).isCharacterDevice()
     */
    final public boolean isCharacterDevice() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isCharacterDevice()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.isCharacterDevice();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if a specific mode represents a directory.
     */
    public static boolean isDirectory(long mode) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isDirectory(long)", null);
        try {
            return (mode & TYPE_MASK) == IS_DIRECTORY;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if this file represents a directory.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).isDirectory()
     */
    final public boolean isDirectory() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isDirectory()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.isDirectory();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if a specific mode represents a FIFO.
     */
    public static boolean isFIFO(long mode) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isFIFO(long)", null);
        try {
            return (mode & TYPE_MASK) == IS_FIFO;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if this file represents a FIFO.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).isFIFO()
     */
    final public boolean isFIFO() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isFIFO()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.isFIFO();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if a specific mode represents a regular file.
     */
    public static boolean isRegularFile(long mode) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isRegularFile(long)", null);
        try {
            return
                (mode & TYPE_MASK) == IS_REGULAR_FILE
                || (mode & TYPE_MASK) == 0
            ;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    /**
     * Determines if this file represents a regular file.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).isRegularFile()
     */
    final public boolean isRegularFile() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isRegularFile()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.isRegularFile();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if this file is the root directory.
     */
    final public boolean isRootDirectory() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isRootDirectory()", null);
        try {
            return filename.equals("/");
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if a specific mode represents a socket.
     */
    public static boolean isSocket(long mode) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isSocket(long)", null);
        try {
            return (mode & TYPE_MASK) == IS_SOCKET;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if this file represents a socket.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).isSocket()
     */
    final public boolean isSocket() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isSocket()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.isSocket();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if a specific mode represents a symbolic link.
     */
    public static boolean isSymLink(long mode) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isSymLink(long)", null);
        try {
            return (mode & TYPE_MASK) == IS_SYM_LINK;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Determines if this file represents a sybolic link.
     *
     * This method will follow symbolic links in the path but not a final symbolic link.
     *
     * @deprecated  Please use getStat(Stat).isSymLink()
     */
    final public boolean isSymLink() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "isSymLink()", null);
        try {
            Stat stat = getStat();
            if(!stat.exists()) throw new FileNotFoundException(filename);
            return stat.isSymLink();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Lists the contents of the directory.
     *
     * This method will follow symbolic links in the path, including a final symbolic link.
     *
     * @see java.io.File#list
     */
    final public String[] list() {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "list()", null);
        try {
            return getFile().list();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Creates a directory.
     *
     * This method will follow symbolic links in the path.
     */
    final public UnixFile mkdir() throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "mkdir()", null);
        try {
            if(!getFile().mkdir()) throw new IOException("Unable to make directory: " + filename);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
    
    /**
     * Creates a directory and sets its permissions, optionally creating all the parent directories if they
     * do not exist.
     *
     * This method will follow symbolic links in the path.
     */
    final public UnixFile mkdir(boolean makeParents, long mode) throws IOException {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "mkdir(boolean,long)", null);
        try {
            if(makeParents) {
                Stat stat = new Stat();
                UnixFile dir=getParent();
                Stack<UnixFile> neededParents=new Stack<UnixFile>();
                while(!dir.isRootDirectory() && !dir.getStat(stat).exists()) {
                    neededParents.push(dir);
                    dir=dir.getParent();
                }
                while(!neededParents.isEmpty()) {
                    dir=neededParents.pop();
                    dir.mkdir().setMode(mode);
                }
            }
            return mkdir().setMode(mode);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Creates a directory and sets its permissions, optionally creating all the parent directories if they
     * do not exist.
     *
     * This method will follow symbolic links in the path.
     */
    final public UnixFile mkdir(boolean makeParents, long mode, int uid, int gid) throws IOException {
        Profiler.startProfile(Profiler.FAST, UnixFile.class, "mkdir(boolean,long,int,int)", null);
        try {
            if(makeParents) {
                Stat stat = new Stat();
                UnixFile dir=getParent();
                Stack<UnixFile> neededParents=new Stack<UnixFile>();
                while(!dir.isRootDirectory() && !dir.getStat(stat).exists()) {
                    neededParents.push(dir);
                    dir=dir.getParent();
                }
                while(!neededParents.isEmpty()) {
                    dir=neededParents.pop();
                    dir.mkdir().setMode(mode).chown(uid, gid);
                }
            }
            return mkdir().setMode(mode).chown(uid, gid);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Creates a device file.
     *
     * This method will follow symbolic links in the path.
     */
    final public UnixFile mknod(long mode, long device) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "mknod(long,long)", null);
        try {
            checkWrite();
            loadLibrary();
            mknod0(filename, mode, device);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    private static native void mknod0(String filename, long mode, long device) throws IOException;

    /**
     * Creates a FIFO.
     *
     * This method will follow symbolic links in the path.
     */
    final public UnixFile mkfifo(long mode) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "mkfifo(long)", null);
        try {
            checkWrite();
            loadLibrary();
            mkfifo0(filename, mode&PERMISSION_MASK);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    private static native void mkfifo0(String filename, long mode) throws IOException;

    /**
     * Sets the access time for this file.
     *
     * This method will follow symbolic links in the path.
     *
     * @deprecated  This method internally performs an extra stat.  Please try to use utime(long,long) directly to avoid this extra stat.
     */
    final public UnixFile setAccessTime(long atime) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "setAccessTime(long)", null);
        try {
            checkWrite();
            // getStat does loadLibrary already: loadLibrary();
            long mtime = getStat().getModifyTime();
            utime0(filename, atime, mtime);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Sets the group ID for this file.
     *
     * This method will follow symbolic links in the path.
     *
     * @deprecated  This method internally performs an extra stat.  Please try to use chown(int,int) directly to avoid this extra stat.
     */
    final public UnixFile setGID(int gid) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "setGID(int)", null);
        try {
            checkWrite();
            // getStat does loadLibrary already: loadLibrary();
            int uid = getStat().getUID();
            chown0(filename, uid, gid);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Sets the permissions for this file.
     *
     * This method will follow symbolic links in the path.
     */
    final public UnixFile setMode(long mode) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "setMode(long)", null);
        try {
            checkWrite();
            loadLibrary();
            setMode0(filename, mode & PERMISSION_MASK);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    private static native void setMode0(String filename, long mode) throws IOException;

    /**
     * Sets the modification time for this file.
     *
     * This method will follow symbolic links in the path.
     *
     * @deprecated  This method internally performs an extra stat.  Please try to use utime(long,long) directly to avoid this extra stat.
     */
    final public UnixFile setModifyTime(long mtime) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "setModifyTime(long)", null);
        try {
            checkWrite();
            // getStat does loadLibrary already: loadLibrary();
            long atime = getStat().getAccessTime();
            utime0(filename, atime, mtime);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Sets the user ID for this file.
     *
     * This method will follow symbolic links in the path.
     *
     * @deprecated  This method internally performs an extra stat.  Please try to use chown(int,int) directly to avoid this extra stat.
     */
    final public UnixFile setUID(int uid) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "setUID(int)", null);
        try {
            checkWrite();
            // getStat does loadLibrary already: loadLibrary();
            int gid = getStat().getGID();
            chown0(filename, uid, gid);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Creates a symbolic link.
     *
     * This method will follow symbolic links in the path.
     */
    final public UnixFile symLink(String destination) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "symLink(String)", null);
        try {
            checkWrite();
            loadLibrary();
            symLink0(filename, destination);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final static native private void symLink0(String filename, String destination) throws IOException;

    /**
     * Creates a hard link.
     *
     * This method will follow symbolic links in the path.
     */
    final public UnixFile link(UnixFile destination) throws IOException {
        return link(destination.getFilename());
    }

    /**
     * Creates a hard link.
     *
     * This method will follow symbolic links in the path.
     */
    final public UnixFile link(String destination) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "link(String)", null);
        try {
            checkWrite();
            loadLibrary();
            link0(filename, destination);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final static native private void link0(String filename, String destination) throws IOException;

    /**
     * Reads a symbolic link.
     *
     * This method will follow symbolic links in the path.
     */
    final public String readLink() throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "readLink()", null);
        try {
            checkRead();
            loadLibrary();
            return readLink0(filename);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
    
    final static native private String readLink0(String filename) throws IOException;

    /**
     * Renames this file, possibly overwriting any previous file.
     *
     * This method will follow symbolic links in the path.
     *
     * @see File#renameTo(File)
     */
    final public void renameTo(UnixFile uf) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "renameTo(UnixFile)", null);
        try {
            if(!getFile().renameTo(uf.getFile())) throw new IOException("Unable to rename "+filename+" to "+uf.filename);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final public String toString() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "toString()", null);
        try {
            return filename;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Sets the access and modify times for this file.
     *
     * This method will follow symbolic links in the path.
     */
    final public UnixFile utime(long atime, long mtime) throws IOException {
        Profiler.startProfile(Profiler.IO, UnixFile.class, "utime(long,long)", null);
        try {
            checkWrite();
            loadLibrary();
            utime0(filename, atime, mtime);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    private static native void utime0(String filename, long atime, long mtime) throws IOException;
    
    public int hashCode() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "hashCode()", null);
        try {
            return filename.hashCode();
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public boolean equals(Object O) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, UnixFile.class, "equals(Object)", null);
        try {
            return
                O!=null
                && (O instanceof UnixFile)
                && ((UnixFile) O).filename.equals(filename)
            ;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
}
