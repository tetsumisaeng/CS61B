package gitlet;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Zherui Lin
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.error("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Command.initCommand(args);
                break;
            case "add":
                break;
            default:
                Utils.error("No command with that name exists.");
        }
    }

}
