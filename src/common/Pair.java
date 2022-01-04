package common;

import java.io.Serializable;

public class Pair<T,U> implements Serializable {
    public T fst;
    public U snd;

    public Pair(T t,U u){
        fst = t;
        snd = u;
    }

}

