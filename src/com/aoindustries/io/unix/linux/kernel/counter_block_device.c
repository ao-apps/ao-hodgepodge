// See http://lwn.net/Articles/58719/
// http://users.cis.fiu.edu/~zhaom/dmcache/index.html
// http://sources.redhat.com/dm/
#include "linux/major.h"
#include "linux/blkdrv.h"
// TODO: Borrowing AZTECH_CDROM_MAJOR since we don't have any - how do we get our own reserved?
#define MAJOR_NR AZTECH_CDROM_MAJOR
#include "blk.h"

//
// TODO: LGPL or Linux kernel-compatible license
//
// Keeps track of generation counters for the block device in order to be able
// to back-up a block device incrementally.  This basically serves the purpose
// of filesystem timestamps.  This is a very efficient way to know that a
// block has been modified.
//
// Unlike many systems where the meta data is placed at the end of the
// partition (MD, DRBD, LVM), this places the meta data throughout the volume
// to minimize seeks.
//
// To keep everything aligned with more recent hard drives, every operation is
// performed with 2^12 (4096) byte alignment.  The on-disk format is a counter
// block followed by the blocks that are being counted.
//
// The counter block contains 1024 32-bit int counters.  Each counter is
// incremented before each write to its related block.  The updated counter
// block and its associated sector writes are sent to the underlying block
// device in order - counters then blocks.
//
// With 1024 4k blocks, the counter and its related block will never be more
// than 4 MB apart, and should thus be written efficiently by the underlying
// physical media, even with caching disabled.  The goal is to introduce
// minimal or no seeking with the addition of the counters.
//
// In the event drive caching is enabled and the cache is volatile, it is hoped
// that this counter-before-block approach will minimize any write reordering
// performed by the underlying devices.  However, no barriers or other
// techniques are used to enforce this.  Thus:
//
// *** FOR MAXIMUM COUNTER ACCURACY - USE NON-VOLATILE CACHE ONLY ***
//
// However, if a write is performed out of order the worst-case scenario is
// that a block is updated and its counter is not updated.  This will result in
// the back-up system not recognizing the changed block and not correctly
// backing-up the system.
//
// If volatile caches must be used for performance reasons, then it is adequate
// to perform the next backup pass with full checksums.  This will ensure the
// backup is in sync.
//
// Because the counters must be read before they can be updated, this implies
// that writes must be preceeded by reads.  The reads, however, are physcally
// close to the writes to minimize the seek times.
//
// Due to using 32-bit counters, it is possible - although very unlikely - that
// a block is modified precisely 2^32 times between backup-passes and will be
// skipped during that back-up pass.
//
// Since one block out of every 1025 blocks is devoted to the counters, the
// block device will lose approximately 0.1% of its capacity.
//
// The first block is also reserved as a special header, containing:
//   00-28 : "counter_block_device\nversion="
//   29-31 : MAJOR_VERSION (Example "000")
//   32    : '.'
//   33-35 : MINOR_VERSION (Example "000")
//   36    : '.'
//   37-39 : RELEASE       (Example "001")
//   40    : '\n'
//
// Everything after this is per-version defined.  Version 000.000.001 is defined as:
//   41-51   : "endianness="
//   52-57   : "little" | "big   "
//   58-59   : "\n\0"
//   60-4095 : Zeros (reserved)
//
