package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Zherui Lin
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    public static final File STAGEAREA_DIR = join(GITLET_DIR, "stage area");
    public static final File head = join(GITLET_DIR, "head");
    public static final File master = join(GITLET_DIR, "master");

    /** To create the repo directory structure and make the initial commit. */
    public static void makeRepo() {
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        STAGEAREA_DIR.mkdir();
        makeInitialCommit();
    }

    /** To make the initial commit, and set head and master pointing to it. */
    private static void makeInitialCommit() {
        Commit initial = new Commit();
        initial.makeCommit(COMMIT_DIR);
        Utils.writeContents(head, sha1(Utils.serialize(initial)));
        Utils.writeContents(master, sha1(Utils.serialize(initial)));
    }
}
