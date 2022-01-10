package Chessgame.board;

import Chessgame.pieces.Piece;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Tile {
    protected final int tileCoord;

    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for(int i=0;i<BoardUtils.NUM_TILES; i++)
            emptyTileMap.put(i, new EmptyTile(i));

        return Collections.unmodifiableMap(emptyTileMap);
    }

    public static Tile createTile(final int tileCoord, final Piece piece){
        return piece != null ? new OccupiedTile(tileCoord, piece) : EMPTY_TILES_CACHE.get(tileCoord);
    }

    private Tile(final int tileCoord){
        this.tileCoord = tileCoord;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public int getTileCoordinate() {
        return this.tileCoord;
    }

    public static final class EmptyTile extends Tile{
        private EmptyTile(final int tileCoord){
            super(tileCoord);
        }

        @Override
        public String toString(){
            return "-";
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }

    public static final class OccupiedTile extends Tile{
        private final Piece pieceOnTile;

        private OccupiedTile(final int tileCoord, final Piece pieceOnTile){
            super(tileCoord);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString(){
            return getPiece().getPieceColor().isBlack() ? getPiece().toString().toLowerCase() : getPiece().toString();
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return pieceOnTile;
        }
    }
}
