package Chessgame.gui;

import Chessgame.board.Board;
import Chessgame.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static Chessgame.gui.Table.*;

public class HistoryPanel extends JPanel {
    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_DIMENSION = new Dimension(150, 40);

    HistoryPanel(){
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public void clear(){
        this.model.clear();
    }

    void redo(final Board board, final MoveLog moveLog){
        int currRow = 0;
        this.model.clear();
        for(final Move move: moveLog.getMoves()){
            final String moveText = move.toString();
            if(move.getMovedPiece().getPieceColor().isWhite()){
                this.model.setValueAt((currRow+1) + ". " + moveText,currRow,0);
            }else if(move.getMovedPiece().getPieceColor().isBlack()){
                this.model.setValueAt(moveText,currRow,1);
                currRow++;
            }
        }

        if (moveLog.getMoves().size() > 0){
            final Move lastMove = moveLog.getMoves().get(moveLog.size()-1);

            final String moveText = lastMove.toString();

            if(lastMove.getMovedPiece().getPieceColor().isWhite()){
                this.model.setValueAt((currRow+1) + ". " + moveText + isInCheckOrCheckmate(board), currRow, 0);
            }
            else if(lastMove.getMovedPiece().getPieceColor().isBlack()){
                this.model.setValueAt(moveText + isInCheckOrCheckmate(board), currRow -1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private String isInCheckOrCheckmate(final Board board) {
        if(board.currentPlayer().isInCheckMate()){
            return "#";
        }else if(board.currentPlayer().isInCheck()){
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel{
        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};


        DataModel(){
            this.values = new ArrayList<>();
        }

        public void clear(){
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount(){
            if(this.values == null){
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount(){
            return 2;
        }

        @Override
        public Object getValueAt(final int row, final int column){
            final Row currRow = this.values.get(row);
            if(column == 0){
                return currRow.getWhiteMove();
            }else if(column == 1) {
                return currRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int col){
            final Row currRow;
            if(this.values.size() <= row){
                currRow = new Row();
                this.values.add(currRow);
            }else{
                currRow = this.values.get(row);
            }

            if (col == 0){
                currRow.setWhiteMove((String) value);
                fireTableRowsInserted(row,row);
            }else if(col == 1){
                currRow.setBlackMove((String) value);
                fireTableCellUpdated(row,col);
            }

        }

        @Override
        public Class<?> getColumnClass(final int col){
            return Move.class;
        }

        @Override
        public String getColumnName(final int col){
            return NAMES[col];
        }

    }

    private static class Row{
        private String whiteMove;
        private String blackMove;

        Row(){
        }

        public String getWhiteMove(){
            return this.whiteMove;
        }

        public String getBlackMove() {
            return blackMove;
        }

        public void setWhiteMove(final String whiteMove) {
            this.whiteMove = whiteMove;
        }

        public void setBlackMove(final String blackMove) {
            this.blackMove = blackMove;
        }
    }
}
