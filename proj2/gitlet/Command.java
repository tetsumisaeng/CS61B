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

}
