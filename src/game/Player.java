package game;

import java.io.Serializable;

public class Player implements Serializable {

    public enum EState {

        init,
        leader,
        lost,
        sleep
    }


    private static final long serialVersionUID = -2777283647638357088L;

    int id;
    String name;
    private EState state;
    
    public Player(String name) {
        super();
        this.name = name;
        this.id = 0;
        this.state = EState.sleep;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player)) {
            return false;
        }
        Player other = (Player) obj;
        return name.equals(other.name);
    }

    @Override
    public String toString() {
        return name + " " + id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }
    
    public void setState(EState state) {
        this.state = state;
    }
    
    public EState getState() {
        return this.state;
    }
}
