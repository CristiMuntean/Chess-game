package Chessgame.player;

import Chessgame.Color;
import Chessgame.board.Board;
import Chessgame.board.Move;
import Chessgame.pieces.King;
import Chessgame.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves){
        this.board = board;
        this.playerKing = establishKing();
        Collection<Move> kingCastles = calculateKingCastles(legalMoves, opponentMoves);
        ArrayList<Move> legMove = new ArrayList<>();
        legMove.addAll(legalMoves);
        legMove.addAll(kingCastles);
        this.legalMoves = Collections.unmodifiableCollection(legMove);
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePos(), opponentMoves).isEmpty();
    }

    public King getPlayerKing(){
        return this.playerKing;
    }
    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePos, Collection<Move> opponentMoves){
        final List<Move> attackMoves = new ArrayList<>();

        for (final Move move: opponentMoves){
            if(piecePos == move.getDestinationCoord()){
                attackMoves.add(move);
            }
        }
        return Collections.unmodifiableList(attackMoves);
    }

    private King establishKing(){
        for(final Piece piece: getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Not a valid board (there is no king)");
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }

    public boolean isInCheckMate(){
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStalemate(){
        return !this.isInCheck && !hasEscapeMoves();
    }

    ///TODO implement is... method
    public boolean isCastled(){
        return false;
    }

    protected boolean hasEscapeMoves(){
        for(final Move move: this.legalMoves){
            final MoveTransition transition = makeMove(move);
            if(transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    public MoveTransition makeMove(final Move move){
        if(!isMoveLegal(move)){
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL);
        }

        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePos(),
                transitionBoard.currentPlayer().getLegalMoves());

        if(!kingAttacks.isEmpty()){
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_IN_CHECK);
        }

        return new MoveTransition(transitionBoard,move,MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Color getColor();
    public abstract Player getOpponent();
    public abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);

}
