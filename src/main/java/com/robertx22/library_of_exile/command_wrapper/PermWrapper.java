package com.robertx22.library_of_exile.command_wrapper;

public enum PermWrapper {
    OP(2),
    ANY_PLAYER(0);

    public int perm = 0;

    PermWrapper(int perm) {
        this.perm = perm;
    }
}


