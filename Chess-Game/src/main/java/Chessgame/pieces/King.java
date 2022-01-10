package Chessgame.pieces;

import Chessgame.board.Tile;
import Chessgame.Color;
import Chessgame.board.Board;
import Chessgame.board.BoardUtils;
import Chessgame.board.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Chessgame.board.Move.*;

public class King extends Piece{

     private final static int[] POSSIBLE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(final int piecePos, final Color pieceColor) {
        super(piecePos, pieceColor, PieceType.KING,true);
    }
    public King(final int piecePos, final Color pieceColor, final boolean isFirstMove){
        super(piecePos,pieceColor,PieceType.KING, isFirstMove);
    }


    @Override
    public List<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int possibleCoordinateOffset: POSSIBLE_MOVE_COORDINATES){

            if(isInFirstColumnExclusion(this.piecePos, possibleCoordinateOffset) ||
            isInEighthColumnExclusion(this.piecePos, possibleCoordinateOffset)){
                continue;
            }

            final int possibleDestinationCoordinate = this.piecePos + possibleCoordinateOffset;

            if(BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)){
                final Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinate);
                if(!possibleDestinationTile.isTileOccupied()){

                    legalMoves.add(new MajorMove(board, this, possibleDestinationCoordinate));

                }else{

                    final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                    final Color pieceColor = pieceAtDestination.pieceColor;
                    if(this.pieceColor != pieceColor){
                        legalMoves.add(new AttackMove(board, this, possibleDestinationCoordinate, pieceAtDestination));
                    }

                }
            }

        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoord(),move.getMovedPiece().getPieceColor());
    }

    private static boolean isInFirstColumnExclusion(final int currentPosition, final int possibleOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (possibleOffset == -9 || possibleOffset == -1 || possibleOffset == 7);
    }

    private static boolean isInEighthColumnExclusion(final int currentPosition, final int possibleOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (possibleOffset == -7 || possibleOffset == 1 || possibleOffset == 9);
    }

    @Override
    public String toString(){
        return PieceType.KING.toString();
    }
}
