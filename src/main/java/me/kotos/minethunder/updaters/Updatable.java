package me.kotos.minethunder.updaters;

public interface Updatable {
    void tick();
    default void register(){
        Updater.addUpdatable(this);
    }

    default void destroy(){
        Updater.removeUpdatable(this);
    }
}
