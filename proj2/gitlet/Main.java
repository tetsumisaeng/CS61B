package gitlet;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Zherui Lin
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            error("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Command.initCommand(args);
                break;
            case "add":
                Command.addCommand(args);
                break;
            case "commit":
                Command.commitCommand(args);
                break;
            case "rm":
                Command.rmCommand(args);
                break;
            case "log": // have not finished merged information
                Command.logCommand(args);
                break;
            case "global-log":
                Command.globalLogCommand(args);
                break;
            case "find":
                Command.findCommand(args);
                break;
            case "checkout":
                Command.checkoutCommand(args);
                break;
            case "branch":
                Command.branchCommand(args);
                break;
            case "rm-branch":
                Command.removeBranchCommand(args);
                break;
            case "reset":
                Command.resetCommand(args);
                break;
            case "status":
                Command.statusCommand(args);
                break;
            default:
                throw error("No command with that name exists.");
        }
    }

}
