package Chessgame.board;

import Chessgame.board.Board.Builder;
import Chessgame.pieces.King;
import Chessgame.pieces.Pawn;
import Chessgame.pieces.Piece;
import Chessgame.pieces.Rook;

public abstract class Move{

    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate){
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final Board board, final int destinationCoordinate){
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode(){
        int res = 1;
        res = 31 * res + this.destinationCoordinate;
        res = 31 * res + this.movedPiece.hashCode();
        res = 31 * res + this.movedPiece.getPiecePos();
        return res;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }
        if(!(other instanceof Move)){
            return false;
        }

        final Move otherMove = (Move) other;
        return getDestinationCoord() == otherMove.getDestinationCoord() &&
                getMovedPiece().equals(otherMove.getMovedPiece()) && getCurrentCoord() == otherMove.getCurrentCoord();
    }

    public int getDestinationCoord(){
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece(){
        return this.movedPiece;
    }

    public int getCurrentCoord(){
        return this.getMovedPiece().getPiecePos();
    }

    public Board getBoard() {
        return board;
    }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastlingMove(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    public Board execute(){

        final Builder builder = new Builder();

        for(final Piece piece: this.board.currentPlayer().getActivePieces()){
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }

        for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }

        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());

        return builder.build();
    }

    public static final class MajorMove extends Move{

        public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType().isPawn() ? BoardUtils.getPosAtCoord(this.destinationCoordinate) :
                    movedPiece.getPieceType().toString() + BoardUtils.getPosAtCoord(this.destinationCoordinate);
        }

    }

    public static class AttackMove extends Move{

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other){
            if(this == other){
                return true;
            }
            if(!(other instanceof AttackMove)){
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece() == otherAttackMove.getAttackedPiece();
        }

        @Override
        public boolean isAttack(){
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType().toString() + "x" + BoardUtils.getPosAtCoord(destinationCoordinate);
        }
    }

    public static class PawnMove extends Move{

        public PawnMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString(){
            return BoardUtils.getPosAtCoord(destinationCoordinate);
        }

    }

    public static class PawnPromotionMove extends Move{

        final Move decoratedMove;
        final Pawn promotedPawn;
        Piece promotedPiece;

        public PawnPromotionMove(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoord());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
            this.promotedPiece = null;
        }

        @Override
        public boolean isAttack(){
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece(){
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString(){
            return this.isAttack() ? BoardUtils.getPosAtCoord(this.decoratedMove.getDestinationCoord()).charAt(0) + "x"+
                    BoardUtils.getPosAtCoord(this.decoratedMove.getDestinationCoord()) + "=" + this.promotedPiece.getPieceType().toString().charAt(0):
                    BoardUtils.getPosAtCoord(this.decoratedMove.getDestinationCoord()) + "=" + this.promotedPiece.getPieceType().toString().charAt(0);
        }

        @Override
        public int hashCode(){
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnPromotionMove && this.decoratedMove.equals(other);
        }

        @Override
        public Board execute(){
            final Board pawnPromotedBoard = this.decoratedMove.execute();
            final Builder builder = new Builder();

            for (final Piece piece: pawnPromotedBoard.currentPlayer().getActivePieces()){
                if(!this.promotedPawn.equals(piece)){
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece: pawnPromotedBoard.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }

            final Piece promotedPiece = this.promotedPawn.getPromotionPiece();
            this.promotedPiece = promotedPiece;
            builder.setPiece(promotedPiece.movePiece(this));
            builder.setMoveMaker(pawnPromotedBoard.currentPlayer().getColor());
            return builder.build();
        }
    }

    public static class PawnAttackMove extends AttackMove{

        public PawnAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            return BoardUtils.getPosAtCoord(this.movedPiece.getPiecePos()).charAt(0) + "x" + BoardUtils.getPosAtCoord(this.destinationCoordinate);
        }

    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove{

        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();

            for (final Piece piece: this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece))
                    builder.setPiece(piece);
            }

            for(final Piece piece:this.board.currentPlayer().getOpponent().getActivePieces()){
                if(!piece.equals(this.getAttackedPiece()))
                    builder.setPiece(piece);
            }

            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
            return builder.build();
        }

    }

    public static final class PawnJump extends Move{

        public PawnJump(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece: this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }

            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }

            final Pawn movedPawn = (Pawn)this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
            return builder.build();
        }

        @Override
        public String toString(){
            return BoardUtils.getPosAtCoord(destinationCoordinate);
        }

    }

    static abstract class CastleMove extends Move{

        protected final Rook castleRook;
        protected final int castleRookStartPos;
        protected final int castleRookDestination;

        public CastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                          final Rook castleRook, final int castleRookStartPos, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStartPos = castleRookStartPos;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove(){
            return true;
        }

        @Override
        public boolean equals(final Object other){
            if(this == other){
                return true;
            }
            if(!(other instanceof CastleMove)){
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }

        @Override
        public int hashCode(){
            int res = super.hashCode();
            res = 31 * res + this.castleRook.hashCode();
            res = 31 * res + this.castleRookDestination;
            return res;
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece: this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPiece(piece);
                }
            }

            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }

            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceColor()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
            return builder.build();
        }

    }

    public static final class KingSideCastleMove extends CastleMove{

        public KingSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                  final Rook castleRook, final int castleRookStartPos, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStartPos, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString(){
            return "O-O";
        }

    }

    public static final class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                   final Rook castleRook, final int castleRookStartPos, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStartPos, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }

        @Override
        public String toString(){
            return "O-O-O";
        }

    }

    public static final class NullMove extends Move{

        public NullMove() {
            super(null,  -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Can't execute a null move");
        }
    }

    public static class MoveFactory{
        private MoveFactory(){
            throw  new RuntimeException("Can't instantiate MoveFactory class");
        }

        public static Move createMove(final Board board, final int currentCoord, final int destinationCoord){

            for(final Move move: board.getAllLegalMoves()){
                if(move.getCurrentCoord() == currentCoord && move.getDestinationCoord() == destinationCoord){
                    System.out.println("move");
                    if(move.isAttack()) System.out.println("attack move");
                    return move;
                }
            }

            System.out.println(currentCoord + " " + destinationCoord);

            System.out.println("null move");
            return NULL_MOVE;
        }
    }

}
