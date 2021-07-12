package gitlet;

import java.io.File;
import java.util.Arrays;

public class Command {

    public static final File CWD = new File(System.getProperty("user.dir"));


    /** Creates a new Gitlet version-control system in the current directory.
     *  This system will automatically start with one commit: a commit that
     *  contains no files and has the commit message initial commit (just like
     *  that, with no punctuation). It will have a single branch: master, which
     *  initially points to this initial commit, and master will be the current
     *  branch. The timestamp for this initial commit will be 00:00:00 UTC,
     *  Thursday, 1 January 1970 in whatever format you choose for dates.
     *  Since the initial commit in all repositories created by Gitlet will
     *  have exactly the same content, it follows that all repositories will
     *  automatically share this commit (they will all have the same UID) and
     *  all commits in all repositories will trace back to it.*/
    public static void initCommand(String[] args) {
        validateNumArgs(args, 1);
        invalidateRepoExistence();
        Repository.makeRepo();
    }

    /** To check if a user inputs a command with the wrong number of operands. */
    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            throw Utils.error("Incorrect operands.");
        }
    }

    /** To ensure a command with at least n number of operands. */
    private static void validateAtLeastNumArgs(String[] args, int n) {
        if (args.length < n) {
            throw Utils.error("Incorrect operands.");
        }
    }

    /** To tell if '.gitlet' exists in CWD. */
    private static boolean repoExistence() {
        File check = Utils.join(CWD, ".gitlet");
        return check.exists();
    }

    /** To check if a user inputs a command that requires being in an initialized
     *  Gitlet working directory, but is not in such a directory. */
    private static void validateRepoExistence() {
        if (!repoExistence()) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
    }

    /** To check if there is already a Gitlet version-control system in the current directory.
     *  If so, it should abort, since it should NOT overwrite the existing system with a new one. */
    private static void invalidateRepoExistence() {
        if (repoExistence()) {
            throw Utils.error("A Gitlet version-control system already exists in the current directory.");
        }
    }

    /** Adds a copy of the file as it currently exists to the staging area (see the description of the
     *  commit command). For this reason, adding a file is also called staging the file for addition.
     *  Staging an already-staged file overwrites the previous entry in the staging area with the new contents.
     *  The staging area should be somewhere in .gitlet. If the current working version of the file is
     *  identical to the version in the current commit, do not stage it to be added, and remove it from
     *  the staging area if it is already there (as can happen when a file is changed, added, and then
     *  changed back to it’s original version). The file will no longer be staged for removal (see gitlet
     *  rm), if it was at the time of the command. If the file does not exist, print the error message
     *  File does not exist. and exit without changing anything.*/
    public static void addCommand(String[] args) {
        validateNumArgs(args, 2);
        validateRepoExistence();
        validateFileExistence(args[1]);
        Repository.addFile(args[1]);

    }

    /** To check if the file does exist in the CWD. */
    private static void validateFileExistence(String filename) {
        File check = Utils.join(CWD, filename);
        if (!check.exists()) {
            throw Utils.error("File does not exist.");
        }
    }

    /** Saves a snapshot of tracked files in the current commit and staging area so
     *  they can be restored at a later time, creating a new commit. The commit is said
     *  to be tracking the saved files. By default, each commit’s snapshot of files will
     *  be exactly the same as its parent commit’s snapshot of files; it will keep versions
     *  of files exactly as they are, and not update them. A commit will only update the
     *  contents of files it is tracking that have been staged for addition at the time
     *  of commit, in which case the commit will now include the version of the file that
     *  was staged instead of the version it got from its parent. A commit will save and
     *  start tracking any files that were staged for addition but weren’t tracked by its
     *  parent. Finally, files tracked in the current commit may be untracked in the new
     *  commit as a result being staged for removal by the rm command (below).*/
    public static void commitCommand(String[] args) {
        validateNumArgs(args, 2);
        validateNonBlankMsg(args[1]);
        validateRepoExistence();
        Repository.makeCommit(args[1]);

    }

    /** To ensure a message is non-blank. */
    private static void validateNonBlankMsg(String msg) {
        if (msg.isBlank()) {
            throw Utils.error("Please enter a commit message.");
        }
    }

    /** Unstage the file if it is currently staged for addition. If the file is tracked in the current commit,
     *  stage it for removal and remove the file from the working directory if the user has not already done so
     *  (do not remove it unless it is tracked in the current commit). Failure cases: If the file is neither staged
     *  nor tracked by the head commit, print the error message No reason to remove the file.*/
    public static void rmCommand(String[] args) {
        validateNumArgs(args, 2);
        validateRepoExistence();
        Repository.rmFile(args[1]);
    }

    /** Starting at the current head commit, display information about each commit backwards along the commit tree
     *  until the initial commit, following the first parent commit links, ignoring any second parents found in merge
     *  commits. This set of commit nodes is called the commit’s history. For every node in this history,
     *  the information it should display is the commit id, the time the commit was made, and the commit message. */
    public static void logCommand(String[] args) {
        validateNumArgs(args, 1);
        validateRepoExistence();
        Repository.showLog();
    }

    /** Like log, except displays information about all commits ever made. The order of the commits does not matter. */
    public static void globalLogCommand(String[] args) {
        validateNumArgs(args, 1);
        validateRepoExistence();
        Repository.showGlobalLog();
    }

    /** Prints out the ids of all commits that have the given commit message, one per line.
     *  If there are multiple such commits, it prints the ids out on separate lines.
     *  The commit message is a single operand; to indicate a multiword message, put the operand in quotation marks.
     *  If no such commit exists, prints the error message Found no commit with that message. */
    public static void findCommand(String[] args) {
        validateNumArgs(args, 2);
        validateRepoExistence();
        Repository.printCommitWithMsg(args[1]);
    }

    /** Checkout is a kind of general command that can do a few different things depending on what its arguments are. */
    public static void checkoutCommand(String[] args) {
        validateAtLeastNumArgs(args, 2);
        if (args[1].equals("--")) {
            checkoutFile(args);
        } else if (args.length >= 3 && args[2].equals(("--"))) {
            checkoutCommitFile(args);
        } else {
            checkoutBranch(args);
        }
    }


    /** Takes the version of the file as it exists in the head commit and puts it in the working directory,
     *  overwriting the version of the file that’s already there if there is one.
     *  The new version of the file is not staged.
     *  If the file does not exist in the previous commit, abort,
     *  printing the error message File does not exist in that commit. Do not change the CWD. */
    private static void checkoutFile(String[] args) {
        validateNumArgs(args, 3);
        validateRepoExistence();
        Repository.checkoutFile(args[2]);
    }

    /** Takes the version of the file as it exists in the commit with the given id, and puts it
     *  in the working directory, overwriting the version of the file that’s already there if there is one.
     *  The new version of the file is not staged.
     *  If no commit with the given id exists, print No commit with that id exists.
     *  Otherwise, if the file does not exist in the given commit, print the same message as for failure case 1.
     *  Do not change the CWD.*/
    private static void checkoutCommitFile(String[] args) {
        validateNumArgs(args, 4);
        validateRepoExistence();
        Repository.checkoutCommitFile(args[1], args[3]);
    }

    /** Takes all files in the commit at the head of the given branch, and puts them in the working directory,
     * overwriting the versions of the files that are already there if they exist. Also, at the end of this command,
     * the given branch will now be considered the current branch (HEAD). Any files that are tracked
     * in the current branch but are not present in the checked-out branch are deleted. The staging area is cleared,
     * unless the checked-out branch is the current branch. */
    private static void checkoutBranch(String[] args) {
        validateNumArgs(args, 2);
        validateRepoExistence();
    }

}
