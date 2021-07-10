package gitlet;

import java.io.File;

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
        validateNumArgs("init", args, 1);
        invalidateRepoExistence();
        Repository.makeRepo();
    }

    /** To check if a user inputs a command with the wrong number of operands. */
    private static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            Utils.error("Incorrect operands.");
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
            Utils.error("Not in an initialized Gitlet directory.");
        }
    }

    /** To check if there is already a Gitlet version-control system in the current directory.
     *  If so, it should abort, since it should NOT overwrite the existing system with a new one. */
    private static void invalidateRepoExistence() {
        if (repoExistence()) {
            Utils.error("A Gitlet version-control system already exists in the current directory.");
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
        validateNumArgs("add", args, 2);
        validateRepoExistence();
        validateFileExistence(args[1]);
        Repository.addFile(args[1]);

    }

    /** To check if the file does exist in the CWD. */
    private static void validateFileExistence(String filename) {
        File check = Utils.join(CWD, filename);
        if (!check.exists()) {
            Utils.error("File does not exist.");
        }
    }
}
