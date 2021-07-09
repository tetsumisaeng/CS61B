package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Map;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Zherui Lin
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    private Date timestamp;
    private Map<String, Blob> fileVersion;

    /** The hashcode of parent Commit. */
    private String parent;

    /** To create the initial Commit when commanding 'init'. */
    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.fileVersion = null;
        this.parent = null;
    }

    /** To make the commit and save it in a file named as its sha1 hash in a directory dir. */
    public void makeCommit(File dir) {
        Utils.writeObject(Utils.join(dir, Utils.sha1(Utils.serialize(this))), this);
    }

}
