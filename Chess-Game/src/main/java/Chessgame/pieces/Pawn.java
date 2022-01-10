package Chessgame.pieces;

import Chessgame.board.Board;
import Chessgame.board.BoardUtils;
import Chessgame.board.Move;
import Chessgame.Color;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Chessgame.board.Move.*;

public class Pawn extends Piece {
    private final static int[] POSSIBLE_MOVE_COORDINATE = {7, 8, 9, 16};

    public Pawn(final int piecePos, final Color pieceColor) {
        super(piecePos, pieceColor, PieceType.PAWN, true);
    }
    public Pawn(final int piecePos, final Color pieceColor, final boolean isFirstMove){
        super(piecePos,pieceColor,PieceType.PAWN, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentPossibleOffset : POSSIBLE_MOVE_COORDINATE) {
            final int possibleDestinationCoordinate = this.piecePos + (currentPossibleOffset * this.getPieceColor().getDirection());

            if (!BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)) {
                continue;
            }

            if (currentPossibleOffset == 8 && !board.getTile(possibleDestinationCoordinate).isTileOccupied()) {

                if(this.pieceColor.isPawnPromotionTile(possibleDestinationCoordinate)){
                    legalMoves.add(new PawnPromotionMove(new PawnMove(board, this, possibleDestinationCoordinate)));
                }
                else{
                    legalMoves.add(new PawnMove(board, this, possibleDestinationCoordinate));
                }

            } else if (currentPossibleOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.SEVENTH_RANK[this.piecePos] && this.pieceColor.isBlack()) ||
                    (BoardUtils.SECOND_RANK[this.piecePos] && this.pieceColor.isWhite())) ) {

                final int behindPieceDestinationCoordinate = this.piecePos + (this.pieceColor.getDirection() * 8);
                if (!board.getTile(behindPieceDestinationCoordinate).isTileOccupied() && !board.getTile(possibleDestinationCoordinate).isTileOccupied())
                    legalMoves.add(new PawnJump(board, this, possibleDestinationCoordinate));

            } else if (currentPossibleOffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePos] && this.pieceColor.isWhite() ||
                            (BoardUtils.FIRST_COLUMN[this.piecePos] && this.pieceColor.isBlack())))) {

                if (board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnPossibleTile = board.getTile(possibleDestinationCoordinate).getPiece();
                    if (this.pieceColor != pieceOnPossibleTile.pieceColor) {

                        if(this.pieceColor.isPawnPromotionTile(possibleDestinationCoordinate)){
                            legalMoves.add(new PawnPromotionMove(new PawnAttackMove(board, this, possibleDestinationCoordinate,pieceOnPossibleTile)));
                        }else{
                            legalMoves.add(new PawnAttackMove(board, this, possibleDestinationCoordinate, pieceOnPossibleTile));
                        }

                    }
                }else if(board.getEnPassantPawn() != null){
                    if(board.getEnPassantPawn().getPiecePos() == (this.piecePos + (this.pieceColor.getOppositeDirection()))){
                        final Piece pieceOnPossible = board.getEnPassantPawn();
                        if(this.pieceColor != pieceOnPossible.getPieceColor()){
                            legalMoves.add(new PawnEnPassantAttackMove(board,this,possibleDestinationCoordinate,pieceOnPossible));
                        }
                    }

                }

            } else if (currentPossibleOffset == 9 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePos] && this.pieceColor.isBlack() ||
                            (BoardUtils.FIRST_COLUMN[this.piecePos] && this.pieceColor.isWhite())))) {

                if (board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnPossibleTile = board.getTile(possibleDestinationCoordinate).getPiece();
                    if (this.pieceColor != pieceOnPossibleTile.pieceColor) {

                        if(this.pieceColor.isPawnPromotionTile(possibleDestinationCoordinate)){
                            legalMoves.add(new PawnPromotionMove(new PawnAttackMove(board, this, possibleDestinationCoordinate,pieceOnPossibleTile)));
                        }else{
                            legalMoves.add(new PawnAttackMove(board, this, possibleDestinationCoordinate, pieceOnPossibleTile));
                        }

                    }

                }else if(board.getEnPassantPawn() != null){
                    if(board.getEnPassantPawn().getPiecePos() == (this.piecePos - (this.pieceColor.getOppositeDirection()))){
                        final Piece pieceOnPossible = board.getEnPassantPawn();
                        if(this.pieceColor != pieceOnPossible.getPieceColor()){
                            legalMoves.add(new PawnEnPassantAttackMove(board,this,possibleDestinationCoordinate,pieceOnPossible));
                        }
                    }

                }

            }

        }

        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoord(),move.getMovedPiece().getPieceColor());
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }

    public Piece getPromotionPiece(){
        String[] options = new String[]{"Rook", "Knight", "Bishop", "Queen"};
        int choice = JOptionPane.showOptionDialog(null,null,"Select pawn promotion",
                JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null, options, null);
        System.out.println(choice);
        switch (choice){
            case 0:return new Rook(this.piecePos, this.pieceColor, false);
            case 1:return new Knight(this.piecePos,this.pieceColor, false);
            case 2:return new Bishop(this.piecePos, this.pieceColor, false);
            case 3:return new Queen(this.piecePos, this.pieceColor, false);
        }
        return null;
    }
}
