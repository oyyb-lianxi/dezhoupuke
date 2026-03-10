package com.example.depu.model;

public class Card implements java.io.Serializable {
    /**
     * 花色
     */
    private Suit suit;
    
    /**
     * 点数
     */
    private Rank rank;

    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(11), QUEEN(12), KING(13), ACE(14);

        private final int value;

        Rank(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() { return suit; }
    public void setSuit(Suit suit) { this.suit = suit; }

    public Rank getRank() { return rank; }
    public void setRank(Rank rank) { this.rank = rank; }

    public int getRankValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        return rank.name() + " of " + suit.name();
    }
}
