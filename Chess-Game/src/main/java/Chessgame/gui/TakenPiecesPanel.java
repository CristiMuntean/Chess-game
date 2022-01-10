package Chessgame.gui;

import Chessgame.board.Move;
import Chessgame.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static Chessgame.gui.Table.*;

public class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.white;
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40,80);

    public TakenPiecesPanel(){
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        this.setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8,2));
        this.southPanel = new JPanel(new GridLayout(8,2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void clear(final MoveLog moveLog){
        this.southPanel.removeAll();;
        this.northPanel.removeAll();
        moveLog.clear();
    }

    public void redo(final MoveLog moveLog){
        this.southPanel.removeAll();
        this.northPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();
        for(final Move move: moveLog.getMoves()){
            if(move.isAttack()){
                final Piece takenPiece = move.getAttackedPiece();
                BufferedImage icon = null;
                try{
                    String path = "images/pieces/" + takenPiece.getPieceColor().toString().charAt(0) + takenPiece.getPieceType().toString() + ".gif";
                    icon = ImageIO.read(new File(path));
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(takenPiece.getPieceColor().isWhite()){
                    this.southPanel.add(new JLabel(new ImageIcon(icon)));
                }else{
                    this.northPanel.add(new JLabel(new ImageIcon(icon)));
                }
            }
        }
        validate();
    }
}
