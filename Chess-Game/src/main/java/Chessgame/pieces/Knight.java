package Chessgame.pieces;

import Chessgame.board.Board;
import Chessgame.board.BoardUtils;
import Chessgame.board.Move;
import Chessgame.board.Tile;
import Chessgame.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Chessgame.board.Move.*;

public class Knight extends Piece{

    private final static int[] POSSIBLE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final int piecePos, final Color pieceColor) {
        super(piecePos, pieceColor, PieceType.KNIGHT, true);
    }
    public Knight(final int piecePos, final Color pieceColor, final boolean isFirstMove){
        super(piecePos,pieceColor,PieceType.KNIGHT, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentMoveOffset: POSSIBLE_MOVE_COORDINATES){
            final int possibleDestinationCoordinate = this.piecePos + currentMoveOffset;

            if(BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)){
                
                if(isInFirstColumnExclusion(this.piecePos, currentMoveOffset) ||
                    isInSecondColumnExclusion(this.piecePos, currentMoveOffset) ||
                    isInSeventhColumnExclusion(this.piecePos, currentMoveOffset) ||
                    isInEighthColumnExclusion(this.piecePos, currentMoveOffset)) {
                    continue;
                }
                
                final Tile destinationTile = board.getTile(possibleDestinationCoordinate);

                if(!destinationTile.isTileOccupied()){
                    legalMoves.add(new MajorMove(board,this, possibleDestinationCoordinate));
                }
                else{
                    final Piece pieceAtLocation = destinationTile.getPiece();
                    final Color pieceColor = pieceAtLocation.getPieceColor();

                    if(this.pieceColor != pieceColor){
                        legalMoves.add(new AttackMove(board,this,possibleDestinationCoordinate,pieceAtLocation));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationCoord(),move.getMovedPiece().getPieceColor());
    }


    private static boolean isInFirstColumnExclusion(final int currentPosition, final int possibleOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (possibleOffset == -17 || possibleOffset == -10 || possibleOffset == 6 ||
                possibleOffset == 15);
    }

    private static boolean isInSecondColumnExclusion(final int currentPosition, final int possibleOffset){
        return BoardUtils.SECOND_COLUMN[currentPosition] && (possibleOffset == -10 || possibleOffset == 6);
    }

    private static boolean isInSeventhColumnExclusion(final int currentPosition, final int possibleOffset){
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (possibleOffset == -6 || possibleOffset == 10);
    }

    private static boolean isInEighthColumnExclusion(final int currentPosition, final int possibleOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (possibleOffset == -15|| possibleOffset == -6 || possibleOffset == 10 ||
                possibleOffset == 17);
    }

    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }

}
