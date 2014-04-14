/**
 * 
 */
package uk.ac.ox.cs.GPT9.augox;

/**
 * @author Joshua Allows quick differentiation between different types of
 *         activity (This is an activity for the user, not the program)
 */
public enum Session {
	BREAKFAST ("Breakfast"),
    MORNING ("Morning"),
    LUNCH ("Lunch"),
    AFTERNOON ("Afternoon"),
    TEA ("Tea"),
    EVENING ("Evening"),
    NIGHT ("Night");

    private final String name;       

    private Session(String s) {
        name = s;
    }

    public boolean equalsName(String otherName){
        return (otherName == null)? false:name.equals(otherName);
    }

    public String toString(){
       return name;
    }
}