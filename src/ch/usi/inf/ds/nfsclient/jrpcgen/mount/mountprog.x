/* The size in bytes of the opaque file handle. */
const FHSIZE = 32;

/* The maximum number of bytes in a name argument. */
const MNTNAMLEN = 255;

/* The maximum number of bytes in a pathname argument. */
const MNTPATHLEN = 1024;

typedef string dirpath<MNTPATHLEN>;
typedef opaque fhandle[FHSIZE];
typedef string name<MNTNAMLEN>;

union fhstatus switch (unsigned status) {
    case 0:
        fhandle directory;
    default:
        void;
};

struct mountlist {
    name      hostname;
    dirpath   directory;
    mountlist nextentry;
};

struct groups {
    name grname;
    groups grnext;
};

struct exportlist {
    dirpath filesys;
    groups groups;
    exportlist next;
};

/*
 * Protocol description for the mount program
 */
program MOUNTPROG {
    /*
     * Version 1 of the mount protocol used with
     * version 2 of the NFS protocol.
     */
    version MOUNTVERS {

        void
        MOUNTPROC_NULL(void) = 0;

        fhstatus
        MOUNTPROC_MNT(dirpath) = 1;

        mountlist
        MOUNTPROC_DUMP(void) = 2;

        void
        MOUNTPROC_UMNT(dirpath) = 3;

        void
        MOUNTPROC_UMNTALL(void) = 4;

        exportlist
        MOUNTPROC_EXPORT(void)  = 5;
   } = 1;
} = 100005;