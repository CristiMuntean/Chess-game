package Chessgame;

import Chessgame.board.BoardUtils;
import Chessgame.player.Player;
import Chessgame.player.WhitePlayer;
import Chessgame.player.BlackPlayer;

public enum Color {
    BLACK{
        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isPawnPromotionTile(int pos) {
            return BoardUtils.FIRST_RANK[pos];
        }

        @Override
        public String toString() {
            return "Black";
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    },
    WHITE{
        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public boolean isPawnPromotionTile(int pos) {
            return BoardUtils.EIGHTH_RANK[pos];
        }

        @Override
        public String toString() {
            return "White";
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    };

    public abstract boolean isWhite();
    public abstract boolean isBlack();

    public abstract int getDirection();
    public abstract int getOppositeDirection();
    public abstract boolean isPawnPromotionTile(int pos);
    @Override
    public abstract String toString();

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
