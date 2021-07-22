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
    public static final File BRANCH_DIR = join(GITLET_DIR, "branch");
    /** The staging area is a file containing the map of staged files. */
    public static final File staging_area = join(GITLET_DIR, "staging area");
    public static final File head = join(GITLET_DIR, "head");
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
        BRANCH_DIR.mkdir();
        try {
            staging_area.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        makeInitialCommit();
    }

    /** To make the initial commit, create master branch pointing to it, and set head to master branch. */
    private static void makeInitialCommit() {
        Commit initial = new Commit();
        initial.saveCommit(COMMIT_DIR);
        File master = join(BRANCH_DIR, "master");
        writeContents(master, sha1(serialize(initial)));
        writeContents(head, "master");

    }

    /** To make a blob of a file and update the staging_area with a new filename-blobhash pair.
     *  If the filename already exists in the staging_area, previous content will be overwritten
     *  by put(). */
    public static void addFile(String filename) {
        String blobhash = addBlob(filename);
        stagedfile = getStagedFile();
        updateStagedAddition(filename, blobhash);
        writeObject(staging_area, stagedfile);
    }

    /** To get the stagedfile object from staging_area file. */
    private static StagingArea getStagedFile() {
        if (!readContentsAsString(staging_area).equals("")) {
            return readObject(staging_area, StagingArea.class);
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

    /** To get the current branch file from head.*/
    private static File getCurrentBranch() {
        return join(BRANCH_DIR, readContentsAsString(head));
    }

    /** To get the current Commit from the branch that head points to. */
    private static Commit getCurrentCommit() {
        return findCommitWithID(readContentsAsString(getCurrentBranch()));
    }

    /** To get the current Commit ID. */
    private static String getCurrentCommitID() {
        return readContentsAsString(getCurrentBranch());
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
            restrictedDelete(join(CWD, filename));
            changed = true;
        }
        if (changed == false) {
            throw error("No reason to remove the file.");
        }
        writeObject(staging_area, stagedfile);
    }

    /** To make a new commit with a msg based on the staging area. Set the current commit as the parent
     *  of the new commit. Set head and master pointing to the new commmit. And finally clean the staging area. */
    public static void makeCommit(String msg) {
        Commit make = new Commit(msg);
        Commit currentcommit = getCurrentCommit();
        make.copyFileVersion(currentcommit);
        updateFileVersion(make);
        make.setParent(getCurrentCommitID());
        make.saveCommit(COMMIT_DIR);
        writeContents(getCurrentBranch(), sha1(serialize(make)));
        writeContents(staging_area);
    }

    /** To update the file version of a commit based on the staging area including addition and removal.
     *  If no files have been staged, abort. Print the message No changes added to the commit.*/
    private static void updateFileVersion(Commit commit) {
        stagedfile = getStagedFile();
        if (stagedfile == null) {
            throw error("No changes added to the commit.");
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
        File target = join(COMMIT_DIR, commitID);
        if (!target.exists()) {
            throw error("No commit with that id exists.");
        }
        return readObject(target, Commit.class);
    }

    /** To display information about all commits ever made. The order of the commits does not matter.*/
    public static void showGlobalLog() {
        List<String> commitIDlist = plainFilenamesIn(COMMIT_DIR);
        for (String commitID: commitIDlist) {
            findCommitWithID(commitID).printCommit();
        }
    }

    /** To print out the ID of the commit with the given message.
     *  If no such commit exists, prints the error message Found no commit with that message.*/
    public static void printCommitWithMsg(String msg) {
        List<String> commitIDlist = plainFilenamesIn(COMMIT_DIR);
        boolean found = false;
        for (String commitID :commitIDlist) {
            if (findCommitWithID(commitID).message.equals(msg)) {
                message(commitID);
                found = true;
            }
        }
        if (!found) {
            throw error("Found no commit with that message.");
        }
    }

    /** Creates a new branch with the given name, and points it at the current head commit.
     *  This command does NOT immediately switch to the newly created branch (just as in real Git).
     *  If a branch with the given name already exists, print the error message A branch with that name already exists. */
    public static void createBranch(String branchname) {
        File branch = join(BRANCH_DIR, branchname);
        if (branch.exists()) {
            throw error("A branch with that name already exists.");
        }
        writeContents(branch, getCurrentCommitID());
    }

    /** Deletes the branch with the given name. If a branch with the given name does not exist, aborts.
     *  Print the error message A branch with that name does not exist.
     *  If you try to remove the branch you’re currently on, aborts,
     *  printing the error message Cannot remove the current branch.*/
    public static void removeBranch(String branchname) {
        File branch = join(BRANCH_DIR, branchname);
        if (!branch.exists()) {
            throw error("A branch with that name does not exist.");
        }
        if (branch.equals(getCurrentBranch())) {
            throw error("Cannot remove the current branch.");
        }
        branch.delete();
    }

    /** Takes the version of the file as it exists in the head commit and puts it in the working directory. */
    public static void checkoutFile(String filename) {
        checkoutFileFromCommit(filename, getCurrentCommit());
    }

    /** To take the version of the file in the commit and put or overwrite it in the working directory.
     *  If the file does not exist in the commit, abort. Do not change the CWD.*/
    private static void checkoutFileFromCommit(String filename, Commit commit) {
        if (!commit.fileVersion.containsKey(filename)) {
            throw error("File does not exist in that commit.");
        }
        Blob target = readObject(join(BLOB_DIR, commit.fileVersion.get(filename)), Blob.class);
        writeContents(join(CWD, filename), target.getContent());
    }

    /** Takes the version of the file as it exists in the commit with the given id,
     *  and puts it in the working directory, overwriting the version of the file that’s already there if there is one.
     *  The new version of the file is not staged.
     *  If no commit with the given id exists, print No commit with that id exists.*/
    public static void checkoutCommitFile(String commitID, String filename) {
        checkoutFileFromCommit(filename, findCommitWithID(commitID));
    }

    /** Takes all files in the commit at the head of the given branch, and puts them in the working directory,
     *  overwriting the versions of the files that are already there if they exist. Also, at the end of this command,
     *  the given branch will now be considered the current branch (HEAD). Any files that are tracked
     *  in the current branch but are not present in the checked-out branch are deleted.
     *  The staging area is cleared, unless the checked-out branch is the current branch. */
    public static void checkoutBranch(String branchname) {
        checkoutBranchFailureCase(branchname);
        File checkoutbranch = join(BRANCH_DIR, branchname);
        Commit checkoutcommit = findCommitWithID(readContentsAsString(checkoutbranch));
        checkoutCommit(checkoutcommit);
        writeContents(head, branchname);
        writeContents(staging_area);
    }

    /** If no branch with that name exists, print No such branch exists. If that branch is the current branch,
     *  print No need to checkout the current branch. If a working file is untracked in the current branch
     *  and would be overwritten by the checkout, print There is an untracked file in the way; delete it,
     *  or add and commit it first. and exit; perform this check before doing anything else. Do not change the CWD. */
    private static void checkoutBranchFailureCase(String branchname) {
        File checkoutbranch = join(BRANCH_DIR, branchname);
        if (!checkoutbranch.exists()) {
            throw error("No such branch exists.");
        }
        if (checkoutbranch.equals(getCurrentBranch())) {
            throw error("No need to checkout the current branch.");
        }
        Commit checkoutcommit = findCommitWithID(readContentsAsString(checkoutbranch));
        untrackedFileFailureCase(checkoutcommit);
    }

    /** To get the set of untracked filename in the CWD in the current commit. Untracked files are files present
     *  in the working directory but neither staged for addition nor tracked.
     *  This includes files that have been staged for removal, but then re-created without Gitlet’s knowledge.
     *  Ignore any subdirectories that may have been introduced, since Gitlet does not deal with them.*/
    private static Set<String> getUntrackedFileName() {
        List<String> allfiles = plainFilenamesIn(CWD);
        Commit currentcommit = getCurrentCommit();
        stagedfile = getStagedFile();
        Set<String> untrackedfiles = new TreeSet<>();
        for (String filename : allfiles) {
            untrackedfiles.add(filename);
        }
        if (currentcommit.fileVersion != null) {
            for (String filename : currentcommit.fileVersion.keySet()) {
                untrackedfiles.remove(filename);
            }
        }
        for (String filename : stagedfile.addition.keySet()) {
            untrackedfiles.remove(filename);
        }
        return untrackedfiles;
    }

    /** To get the set of deleted filename in the CWD in the current commit. A file is deleted if it is
     *  staged for addition, but deleted in the working directory; or not staged for removal,
     *  but tracked in the current commit and deleted from the working directory. */
    private static Set<String> getDeletedFileName() {
        List<String> allfiles = plainFilenamesIn(CWD);
        Commit currentcommit = getCurrentCommit();
        stagedfile = getStagedFile();
        Set<String> deletedfiles = new TreeSet<>();
        if (currentcommit.fileVersion != null) {
            for (String filename : currentcommit.fileVersion.keySet()) {
                deletedfiles.add(filename);
            }
        }
        for (String filename : stagedfile.addition.keySet()) {
            deletedfiles.add(filename);
        }
        for (String filename : allfiles) {
            deletedfiles.remove(filename);
        }
        for (String filename : stagedfile.removal) {
            deletedfiles.remove(filename);
        }
        return deletedfiles;
    }

    /** To get the set of modified filename in the CWD in the current commit.  A file is modified if it is tracked
     *  in the current commit, changed in the working directory, but not staged; or staged for addition,
     *  but with different contents than in the working directory. */
    private static Set<String> getModifiedFileName() {
        List<String> allfiles = plainFilenamesIn(CWD);
        Commit currentcommit = getCurrentCommit();
        stagedfile = getStagedFile();
        Set<String> modifiedfiles = new TreeSet<>();
        for (String filename : allfiles) {
            String filecontenthash = sha1(readContentsAsString(new File(filename)));
            if (stagedfile.addition.containsKey(filename)) {
                if (!filecontenthash.equals(stagedfile.addition.get(filename))) {
                    modifiedfiles.add(filename);
                }
            } else if (currentcommit.fileVersion != null && currentcommit.fileVersion.containsKey(filename) && !filecontenthash.equals(currentcommit.fileVersion.get(filename))) {
                modifiedfiles.add(filename);
            }
        }
        return modifiedfiles;
    }

    /** If a working file is untracked in the current branch and would be overwritten by the commit reset/checkout,
     *  print There is an untracked file in the way; delete it, or add and commit it first.
     *  and exit; perform this check before doing anything else.*/
    private static void untrackedFileFailureCase(Commit commit) {
        Set<String> untrackedFile = getUntrackedFileName();
        for (String filename : commit.fileVersion.keySet()) {
            if (untrackedFile.contains(filename)) {
                throw error("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
    }

    /** Checks out all the files tracked by the given commit. Removes tracked files that are not present
     *  in that commit. Also moves the current branch’s head to that commit node. The staging area is cleared.
     *  The command is essentially checkout of an arbitrary commit that also changes the current branch head.*/
    public static void resetCurrentBranch(String commitID) {
        Commit resetcommit = findCommitWithID(commitID);
        untrackedFileFailureCase(resetcommit);
        checkoutCommit(resetcommit);
        writeContents(getCurrentBranch(), commitID);
        writeContents(staging_area);
    }

    /** To remove all the files tracked by the current commit and overwrite the files tracked by the given commit.*/
    private static void checkoutCommit(Commit checkoutcommit) {
        for (String filename : getCurrentCommit().fileVersion.keySet()) {
            restrictedDelete(join(CWD, filename));
        }
        for (String filename : checkoutcommit.fileVersion.keySet()) {
            checkoutFileFromCommit(filename, checkoutcommit);
        }
    }

    /** Displays what branches currently exist, and marks the current branch with a *.
     *  Also displays what files have been staged for addition or removal.
     *  Also displays files modified or deleted without stage and untracked files.
     *  Entries should be listed in lexicographic order, using the Java string-comparison order. */
    public static void showStatus() {
        stagedfile = getStagedFile();
        message("=== Branches ===");
        List<String> branchlist = plainFilenamesIn(BRANCH_DIR);
        branchlist.sort(Comparator.naturalOrder());
        for (String branchname : branchlist) {
            if (branchname.equals(readContentsAsString(head))) {
                message("*%s", branchname);
            } else {
                message(branchname);
            }
        }
        message("");
        message("=== Staged Files ===");
        for (String stagedfile : stagedfile.addition.keySet()) {
            message(stagedfile);
        }
        message("");
        message("=== Removed Files ===");
        for (String removedfile : stagedfile.removal) {
            message(removedfile);
        }
        message("");
        message("=== Modifications Not Staged For Commit ===");
        for (String deletedfile : getDeletedFileName()) {
            message("%s (deleted)", deletedfile);
        }
        for (String modifiedfile : getModifiedFileName()) {
            message("%s (modified)", modifiedfile);
        }
        message("");
        message("=== Untracked Files ===");
        for (String untrackedfile : getUntrackedFileName()) {
            message(untrackedfile);
        }
        message("");
    }

    /***/
    public static void mergeBranch(String branchname) {
        Commit givencommit = findCommitWithID(readContentsAsString(join(BRANCH_DIR, branchname)));
        Commit currentcommit = getCurrentCommit();
        Commit splitpoint = findSplitPoint(givencommit, currentcommit);
    }

    /** To find the split point commit of two commits. The split point is a latest common ancestor of the current
     *  and given branch heads: - A common ancestor is a commit to which there is a path (of 0 or more parent pointers)
     *  from both branch heads. - A latest common ancestor is a common ancestor that is not an ancestor of any other common ancestor.*/
    private static Commit findSplitPoint(Commit c1, Commit c2) {
        PriorityQueue<Commit> pq1 = new PriorityQueue<>();
        PriorityQueue<Commit> pq2 = new PriorityQueue<>();
        while (!c1.equals(c2)) {
            if (c1.compareTo(c2) <= 0) {
                pq1.add(findCommitWithID(c1.parent));
                if (c1.secondparent != null) {
                    pq1.add(findCommitWithID(c1.secondparent));
                }
                c1 = pq1.poll();
            } else {
                pq2.add(findCommitWithID(c2.parent));
                if (c2.secondparent != null) {
                    pq2.add(findCommitWithID(c2.secondparent));
                }
                c2 = pq2.poll();
            }
        }
        return c1;
    }

}
