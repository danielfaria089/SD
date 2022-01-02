package client.View.GUI;

import java.awt.*;

public class DupleCompPos {
    private final Component first;
    private final String second;

    public DupleCompPos(Component first,String second){
        this.first=first;
        this.second=second;
    }

    public DupleCompPos(DupleCompPos duple){
        this.first= duple.first;
        this.second=duple.second;
    }

    public Component getFirst(){
        return first;
    }

    public String getSecond(){
        return second;
    }
}
