package Chessgame.pieces;

import Chessgame.board.Move;
import Chessgame.Color;
import Chessgame.board.Board;

import java.util.List;

public abstract class Piece {
    protected final int piecePos;
    protected final Color pieceColor;
    protected final boolean isFirstMove;
    protected final PieceType pieceType;
    private final int cachedHashCode;

    Piece(final int piecePos, final Color pieceColor, final PieceType pieceType, final boolean isFirstMove){
        this.piecePos = piecePos;
        this.pieceColor = pieceColor;
        this.isFirstMove = isFirstMove;
        this.pieceType = pieceType;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        int res = pieceType.hashCode();
        res = 31 * res + pieceColor.hashCode();
        res = 31 * res + piecePos;
        res = 31 * res + (isFirstMove ? 1 : 0);
        return res;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }
        if(!(other instanceof Piece)){
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return piecePos == otherPiece.getPiecePos() && pieceType == otherPiece.getPieceType() &&
                pieceColor == otherPiece.getPieceColor() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode(){
        return this.cachedHashCode;
    }

    public Color getPieceColor() {
        return this.pieceColor;
    }

    public PieceType getPieceType(){
        return this.pieceType;
    }

    public int getPiecePos(){
        return this.piecePos;
    }

    public boolean isFirstMove(){
        return this.isFirstMove;
    }

    public abstract List<Move> calculateLegalMoves(final Board board);
    public abstract Piece movePiece(Move move);

    public enum PieceType{
        PAWN("P"){
            @Override
            public boolean isKing(){
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isPawn() {
                return true;
            }
        },
        BISHOP("B") {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isPawn() {
                return false;
            }
        },
        KNIGHT("N") {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }
            @Override
            public boolean isPawn() {
                return false;
            }
        },
        ROOK("R") {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return true;
            }
            @Override
            public boolean isPawn() {
                return false;
            }
        },
        QUEEN("Q") {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }
            @Override
            public boolean isPawn() {
                return false;
            }
        },
        KING("K") {
            @Override
            public boolean isKing() {
                return true;
            }
            @Override
            public boolean isRook() {
                return false;
            }
            @Override
            public boolean isPawn() {
                return false;
            }
        };

        private final String pieceName;

        PieceType(final String pieceName){
            this.pieceName = pieceName;
        }

        @Override
        public String toString(){
            return this.pieceName;
        }
        public abstract boolean isKing();

        public abstract boolean isRook();
        public abstract boolean isPawn();
    }
}
