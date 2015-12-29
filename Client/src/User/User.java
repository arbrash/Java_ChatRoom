package User;
import java.io.*;
import java.util.ArrayList;

public class User extends DataObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String message;
    private String flag = "new";
    private ArrayList<String> userlist;
    public User(){}
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setFlag(String flag){
        this.flag = flag;
    }
    public String getFlag(){
        return flag;
    }
    public void setUserList(ArrayList<String> userlist){
        this.userlist = userlist;
    }
    public ArrayList<String> getUserList(){
        return userlist;
    }
}
