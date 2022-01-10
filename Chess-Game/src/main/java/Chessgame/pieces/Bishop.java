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

public class Bishop extends Piece{
    private final static int[] POSSIBLE_MOVE_VECTOR_COORDINATES = {-9, -7, 7, 9};

    public Bishop(final int piecePos, final Color pieceColor) {
        super(piecePos, pieceColor, PieceType.BISHOP, true);
    }

    public Bishop(final int piecePos, final Color pieceColor, final boolean isFirstMove){
        super(piecePos,pieceColor,PieceType.BISHOP, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int possibleCoordinateOffset: POSSIBLE_MOVE_VECTOR_COORDINATES){
            int possibleDestinationCoordinate = this.piecePos;

            while(BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)){

                if(isFirstColumnExclusion(possibleDestinationCoordinate,possibleCoordinateOffset) ||
                isEighthColumnExclusion(possibleDestinationCoordinate,possibleCoordinateOffset)){
                    break;
                }
                possibleDestinationCoordinate += possibleCoordinateOffset;

                if(BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)){

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
                        break;
                    }
                }
            }
        }

        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getDestinationCoord(),move.getMovedPiece().getPieceColor());
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int possibleOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (possibleOffset == -9 || possibleOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int possibleOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (possibleOffset == -7 || possibleOffset == 9);
    }

    @Override
    public String toString(){
        return PieceType.BISHOP.toString();
    }

}
