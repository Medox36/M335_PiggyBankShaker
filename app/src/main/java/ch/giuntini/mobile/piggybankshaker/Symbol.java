package ch.giuntini.mobile.piggybankshaker;

public enum Symbol {
    CHERRY(R.drawable.symbol_cherry),
    SPADES(R.drawable.symbol_spades),
    COIN(R.drawable.symbol_coin),
    DIAMOND(R.drawable.symbol_diamond),
    STAR(R.drawable.symbol_star),
    SEVEN(R.drawable.symbol_seven),
    EMPTY(R.drawable.symbol_empty);

    private final int imageID;

    Symbol(int imageID) {
        this.imageID = imageID;
    }

    public int getImageID() {
        return imageID;
    }

    public static Symbol ofOrdinal(int ordinal) {
        if (ordinal == 0) return CHERRY;
        if (ordinal == 1) return SPADES;
        if (ordinal == 2) return COIN;
        if (ordinal == 3) return DIAMOND;
        if (ordinal == 4) return STAR;
        if (ordinal == 5) return SEVEN;
        return EMPTY;
    }
}
