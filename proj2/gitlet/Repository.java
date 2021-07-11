package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
    /** The .gitlet directory and its subdirectory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");
    /** The staging area is a file containing the map of staged files. */
    public static final File staging_area = join(GITLET_DIR, "staging area");
    public static final File head = join(GITLET_DIR, "head");
    public static final File master = join(GITLET_DIR, "master");
    public static final File current = join(GITLET_DIR, "current");
    public static HashMap<String, String> stagedfilemap;

    /** To create the repo directory structure and make the initial commit. */
    public static void makeRepo() {
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        try {
            staging_area.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        makeInitialCommit();
    }

    /** To make the initial commit, and set head and master pointing to it. */
    private static void makeInitialCommit() {
        Commit initial = new Commit();
        initial.saveCommit(COMMIT_DIR);
        Utils.writeContents(head, sha1(Utils.serialize(initial)));
        Utils.writeContents(master, sha1(Utils.serialize(initial)));
        Utils.writeContents(current, sha1(Utils.serialize(initial)));
    }

    /** To make a blob of a file and update the staging_area with a new filename-blobhash pair.
     *  If the filename already exists in the staging_area, previous content will be overwritten
     *  by put(). */
    public static void addFile(String filename) {
        String blobhash = addBlob(filename);
        if (!Utils.readContentsAsString(staging_area).equals("")) {
            stagedfilemap = Utils.readObject(staging_area, HashMap.class);
        } else {
            stagedfilemap = new HashMap<>();
        }
        updateStage(filename, blobhash);
        Utils.writeObject(staging_area, stagedfilemap);
    }

    /** To add a new blob form a file, save it in the blob_dir, and return the blob sha1 hash. */
    private static String addBlob(String filename) {
        Blob add = new Blob(filename);
        return add.makeBlob(BLOB_DIR);
    }

    /** To check the content of a file in the current commit. If the file exists,
     *  return the content sha1 hash, otherwise return null. */
    private static String checkCurrentVersion(String filename) {
        Commit currentcommit = getCurrentCommit();
        if (currentcommit.fileVersion != null && currentcommit.fileVersion.containsKey(filename)) {
            return currentcommit.fileVersion.get(filename);
        }
        return null;
    }

    /** To get the current Commit. */
    private static Commit getCurrentCommit() {
        File currentcommitfile = Utils.join(COMMIT_DIR, Utils.readContentsAsString(current));
        return Utils.readObject(currentcommitfile, Commit.class);
    }

    /** To update staging area. If the current working version of the file (represented as blobhash)
     *  is identical to the version in the current commit, do not stage it to be added, and remove it from
     *  the staging area if it is already there. Otherwise, add the file to staging area. */
    private static void updateStage(String filename, String blobhash) {
        if (blobhash.equals(checkCurrentVersion(filename))) {
            stagedfilemap.remove(filename);
        } else {
            stagedfilemap.put(filename, blobhash);
        }
    }

    /** To make a new commit with a msg based on the staging area. Set the current commit as the parent
     *  of the new commit. Set head and master pointing to the new commmit. And finally clean the staging area. */
    public static void makeCommit(String[] msg) {
        Commit make = new Commit(stringArrayToString(msg));
        Commit currentcommit = getCurrentCommit();
        make.copyFileVersion(currentcommit);
        updateFileVersion(make);
        make.setParent(Utils.readContentsAsString(current));
        make.saveCommit(COMMIT_DIR);
        Utils.writeContents(head, sha1(Utils.serialize(make)));
        Utils.writeContents(master, sha1(Utils.serialize(make)));
        Utils.writeContents(current, sha1(Utils.serialize(make)));
        Utils.writeContents(staging_area);
    }

    /** To convert a string array to a whole string with space among each origin string. */
    private static String stringArrayToString(String[] sa) {
        String result = "";
        for (String s : sa) {
            result += s + " ";
        }
        return result.trim();
    }

    /** To update the file version of a commit based on the staging area. */
    private static void updateFileVersion(Commit commit) {
        if (!Utils.readContentsAsString(staging_area).equals("")) {
            stagedfilemap = Utils.readObject(staging_area, HashMap.class);
        } else {
            stagedfilemap = new HashMap<>();
        }
        for (String filename : stagedfilemap.keySet()) {
            commit.changeFileVersion(filename, stagedfilemap.get(filename));
        }
    }

}
