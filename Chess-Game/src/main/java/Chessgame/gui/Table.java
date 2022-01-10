package Chessgame.gui;

import Chessgame.board.Board;
import Chessgame.board.BoardUtils;
import Chessgame.board.Move;
import Chessgame.board.Tile;
import Chessgame.pieces.Bishop;
import Chessgame.pieces.Piece;
import Chessgame.player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static Chessgame.board.Move.*;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {
    private Board chessBoard;
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final HistoryPanel historyPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final MoveLog moveLog;

    private Tile sourceTile;
    private Tile destTile;
    private Piece movedPiece;
    private BoardDirection boardDirection;

    private final static Dimension FRAME_DIMENSION = new Dimension(800, 800);
    private final static Dimension BOARD_DIMENSION = new Dimension(600, 600);
    private final static Dimension TILE_DIMENSION = new Dimension(10, 10);
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");
    private final String pieceImagePath = "images/pieces/";

    public Table(){
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());
        this.chessBoard = Board.createStandardBoard();
        this.boardDirection = BoardDirection.NORMAL;
        this.historyPanel = new HistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();

        final JMenuBar tableMenuBar = createMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);


        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.historyPanel, BorderLayout.EAST);

        this.gameFrame.setSize(FRAME_DIMENSION);
        this.gameFrame.setVisible(true);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private JMenuBar createMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(makeFileMenu());
        tableMenuBar.add(makePreferencesMenu());
        return tableMenuBar;
    }

    private JMenu makeFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem exit = new JMenuItem("Exit");
        final JMenuItem reset = new JMenuItem("Reset Board");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chessBoard = Board.createStandardBoard();
                boardPanel.drawBoard(chessBoard);
                takenPiecesPanel.clear(moveLog);
                historyPanel.clear();
            }
        });
        fileMenu.add(exit);
        fileMenu.addSeparator();
        fileMenu.add(reset);
        return fileMenu;
    }

    private JMenu makePreferencesMenu(){
        final JMenu preferenceMenu = new JMenu("Preferences");
        final JMenuItem flipBoard = new JMenuItem("Flip Board");
        flipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferenceMenu.add(flipBoard);
        return preferenceMenu;
    }

    public enum BoardDirection{
        NORMAL{
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                Collections.reverse(boardTiles);
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;

        BoardPanel(){
            super(new GridLayout(8,8));

            this.boardTiles = new ArrayList<>();
            for(int i=0; i< BoardUtils.NUM_TILES; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_DIMENSION);

            validate();
        }

        public void drawBoard(final Board board){
            removeAll();
            for (final TilePanel tilePanel: boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }

    }

    public static class MoveLog{
        private  List<Move> moves;

        MoveLog(){
            this.moves = new ArrayList<>();
        }
        public List<Move> getMoves(){
            return this.moves;
        }
        public void addMove(final Move move){
            this.moves.add(move);
        }
        public int size(){
            return this.moves.size();
        }
        public void clear(){
            this.moves.clear();
        }
        private Move removeMove(int index){
            return this.moves.remove(index);
        }
        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }
    }

    private class TilePanel extends JPanel{
        private final int tileId;
        TilePanel(final BoardPanel boardPanel, final int tileId){
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_DIMENSION);
            assignTileColor();
            assignPieceImage(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e)){
                        sourceTile = null;
                        destTile = null;
                        movedPiece = null;
                    }else if (isLeftMouseButton(e)){

                        if(sourceTile == null){
                            sourceTile = chessBoard.getTile(tileId);
                            movedPiece = sourceTile.getPiece();
                            if(movedPiece == null)sourceTile = null;
                        } else{
                            destTile = chessBoard.getTile(tileId);
                            final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()){
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destTile = null;
                            movedPiece = null;
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                historyPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoard(chessBoard);
                            }
                        });

                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            validate();
        }

        public void drawTile(final Board board){
            assignTileColor();
            assignPieceImage(board);
            highlightLegalMoves(board);
            highlightLastMove(moveLog,board);
            validate();
            repaint();
        }

        private void highlightLastMove(final MoveLog moveLog, final Board board){
            final Move lastMove;
            if(moveLog.size() > 0){
                lastMove = moveLog.getMoves().get(moveLog.size()-1);
                System.out.println(lastMove.getCurrentCoord() + ", - " + lastMove.getDestinationCoord());
                int kingPos = board.currentPlayer().getPlayerKing().getPiecePos();
                if(board.currentPlayer().isInCheck() && this.tileId == kingPos)setBackground(Color.decode("#f79790"));
                if (board.currentPlayer().isInCheckMate() && this.tileId == kingPos){
                    setBackground(Color.red);
                    String message = board.currentPlayer().getOpponent().getColor().toString() + " Player Wins";
                    JOptionPane.showMessageDialog(null, message, "Game finished", JOptionPane.PLAIN_MESSAGE);
                }
                //TODO how to fix stalemate
//                if(board.currentPlayer().isInStalemate() && (this.tileId == kingPos ||
//                        this.tileId == board.currentPlayer().getOpponent().getPlayerKing().getPiecePos())){
//                    String message = "Draw! Stalemate";
//                    setBackground(Color.decode("#5eaaef"));
//                    JOptionPane.showMessageDialog(null, message, "Game finished", JOptionPane.PLAIN_MESSAGE);
//                }
                if(this.tileId == lastMove.getCurrentCoord() ||
                   this.tileId == lastMove.getDestinationCoord())setBackground(Color.decode("#ccbf49"));
            }

        }

        private void assignTileColor() {
            if(BoardUtils.EIGHTH_RANK[this.tileId] || BoardUtils.SIXTH_RANK[this.tileId] ||
               BoardUtils.FOURTH_RANK[this.tileId] || BoardUtils.SECOND_RANK[this.tileId]){
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            }
            else if(BoardUtils.SEVENTH_RANK[this.tileId] || BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId] || BoardUtils.FIRST_RANK[this.tileId]){
                setBackground(this.tileId % 2 == 1 ? lightTileColor : darkTileColor);
            }
        }

        private void assignPieceImage(final Board board){
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){

                try {
                    final BufferedImage image = ImageIO.read(new File(pieceImagePath +
                            board.getTile(this.tileId).getPiece().getPieceColor().toString().charAt(0) +
                            board.getTile(this.tileId).toString() + ".gif"));
                    Image newImg = image.getScaledInstance(60,60, Image.SCALE_SMOOTH);

                    this.add(new JLabel(new ImageIcon(newImg)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void highlightLegalMoves(final Board board){
            for(final Move move: pieceLegalMoves(board)){
                if(move.getDestinationCoord() == this.tileId){
                    try {
                        add(new JLabel(new ImageIcon(ImageIO.read(new File("images/misc/green_dot.png")))));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        }

        private Collection<Move> pieceLegalMoves(final Board board){
            if(movedPiece != null && movedPiece.getPieceColor() == board.currentPlayer().getColor()){
                if(movedPiece.getPieceType().isKing() && movedPiece.isFirstMove()){
                    final List<Move> castleMoves = new ArrayList<>();
                    castleMoves.addAll(board.currentPlayer().calculateKingCastles(board.currentPlayer().getLegalMoves(),
                            board.currentPlayer().getOpponent().getLegalMoves()));
                    List<Move> returnMove = new ArrayList<>();
                    returnMove.addAll(movedPiece.calculateLegalMoves(board));
                    returnMove.addAll(castleMoves);
                    return Collections.unmodifiableList(returnMove);
                }
                return movedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }
    }
}
