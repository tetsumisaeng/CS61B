package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

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
    public static StagingArea stagedfile;

    /** A StagingArea object can tell the staged addition map and staged removal set.*/
    private static class StagingArea implements Serializable {
        Map<String, String> addition;
        Set<String> removal;
        StagingArea() {
            addition = new HashMap<>();
            removal = new HashSet<>();
        }
    }

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
    }

    /** To make a blob of a file and update the staging_area with a new filename-blobhash pair.
     *  If the filename already exists in the staging_area, previous content will be overwritten
     *  by put(). */
    public static void addFile(String filename) {
        String blobhash = addBlob(filename);
        stagedfile = getStagedFile();
        updateStagedAddition(filename, blobhash);
        Utils.writeObject(staging_area, stagedfile);
    }

    /** To get the stagedfile object from staging_area file. */
    private static StagingArea getStagedFile() {
        if (!Utils.readContentsAsString(staging_area).equals("")) {
            return Utils.readObject(staging_area, StagingArea.class);
        } else {
            return new StagingArea();
        }
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
        return findCommitWithID(Utils.readContentsAsString(head));
    }

    /** To update staged addition map in the staging area. If the current working version of the file (represented as blobhash)
     *  is identical to the version in the current commit, do not stage it to be added, and remove it from
     *  the staging area if it is already there. Otherwise, add the file to staging area. */
    private static void updateStagedAddition(String filename, String blobhash) {
        if (blobhash.equals(checkCurrentVersion(filename))) {
            stagedfile.addition.remove(filename);
        } else {
            stagedfile.addition.put(filename, blobhash);
        }
    }

    /** Unstage the file if it is currently staged for addition. If the file is tracked in the current commit,
     *  stage it for removal and REMOVE the file from the working directory. If the file is neither staged
     *  nor tracked by the head commit, print the error message No reason to remove the file.*/
    public static void rmFile(String filename) {
        stagedfile = getStagedFile();
        Commit currentcommit = getCurrentCommit();
        boolean changed = false;
        if (stagedfile.addition.containsKey(filename)) {
            stagedfile.addition.remove(filename);
            changed = true;
        }
        if (currentcommit.fileVersion != null && currentcommit.fileVersion.containsKey(filename)) {
            stagedfile.removal.add(filename);
            Utils.restrictedDelete(join(CWD, filename));
            changed = true;
        }
        if (changed == false) {
            throw Utils.error("No reason to remove the file.");
        }
        Utils.writeObject(staging_area, stagedfile);
    }

    /** To make a new commit with a msg based on the staging area. Set the current commit as the parent
     *  of the new commit. Set head and master pointing to the new commmit. And finally clean the staging area. */
    public static void makeCommit(String msg) {
        Commit make = new Commit(msg);
        Commit currentcommit = getCurrentCommit();
        make.copyFileVersion(currentcommit);
        updateFileVersion(make);
        make.setParent(Utils.readContentsAsString(head));
        make.saveCommit(COMMIT_DIR);
        Utils.writeContents(head, sha1(Utils.serialize(make)));
        Utils.writeContents(master, sha1(Utils.serialize(make)));
        Utils.writeContents(staging_area);
    }

    /** To update the file version of a commit based on the staging area including addition and removal.
     *  If no files have been staged, abort. Print the message No changes added to the commit.*/
    private static void updateFileVersion(Commit commit) {
        stagedfile = getStagedFile();
        if (stagedfile == null) {
            throw Utils.error("No changes added to the commit.");
        }
        for (String filename : stagedfile.addition.keySet()) {
            commit.changeFileVersion(filename, stagedfile.addition.get(filename));
        }
        for (String filename : stagedfile.removal) {
            commit.removeFile(filename);
        }
    }

    /** Starting at the current head commit, display information about each commit backwards
     *  along the commit tree until the initial commit, following the first parent commit links,
     *  ignoring any second parents found in merge commits.*/
    public static void showLog() {
        showLogFrom(getCurrentCommit());
    }

    /** To display information about each commit backwards along the commit tree form commit c. */
    private static void showLogFrom(Commit c) {
        c.printCommit();
        if (c.parent != null) {
            showLogFrom(findCommitWithID(c.parent));
        }
    }

    /** To find a commit in the directory based on its sha1code.
     *  If no commit with the given id exists, print No commit with that id exists.*/
    private static Commit findCommitWithID(String commitID) {
        File target = Utils.join(COMMIT_DIR, commitID);
        if (!target.exists()) {
            throw Utils.error("No commit with that id exists.");
        }
        return Utils.readObject(target, Commit.class);
    }

    /** To display information about all commits ever made. The order of the commits does not matter.*/
    public static void showGlobalLog() {
        List<String> commitIDlist = Utils.plainFilenamesIn(COMMIT_DIR);
        for (String commitID: commitIDlist) {
            findCommitWithID(commitID).printCommit();
        }
    }

    /** To print out the ID of the commit with the given message.
     *  If no such commit exists, prints the error message Found no commit with that message.*/
    public static void printCommitWithMsg(String msg) {
        List<String> commitIDlist = Utils.plainFilenamesIn(COMMIT_DIR);
        boolean found = false;
        for (String commitID :commitIDlist) {
            if (findCommitWithID(commitID).message.equals(msg)) {
                Utils.message(commitID);
                found = true;
            }
        }
        if (!found) {
            throw Utils.error("Found no commit with that message.");
        }
    }

    /** Takes the version of the file as it exists in the head commit and puts it in the working directory. */
    public static void checkoutFile(String filename) {
        checkoutFileFromCommit(filename, getCurrentCommit());
    }

    /** To take the version of the file in the commit and put or overwrite it in the working directory.
     *  If the file does not exist in the commit, abort. Do not change the CWD.*/
    private static void checkoutFileFromCommit(String filename, Commit commit) {
        if (!commit.fileVersion.containsKey(filename)) {
            throw Utils.error("File does not exist in that commit.");
        }
        Blob target = Utils.readObject(join(BLOB_DIR, commit.fileVersion.get(filename)), Blob.class);
        Utils.writeContents(join(CWD, filename), target.getContent());
    }

    /** Takes the version of the file as it exists in the commit with the given id,
     *  and puts it in the working directory, overwriting the version of the file that’s already there if there is one.
     *  The new version of the file is not staged.
     *  If no commit with the given id exists, print No commit with that id exists.*/
    public static void checkoutCommitFile(String commitID, String filename) {
        checkoutFileFromCommit(filename, findCommitWithID(commitID));
    }

}
