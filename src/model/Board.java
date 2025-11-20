package model;

public class Board {
    public Piece[][] grid;

    public Board() {
        grid = new Piece[10][9];
        initStandardBoard();
    }

    public void initStandardBoard() {
        addPiece(new Chariot(0, 9, true));
        addPiece(new Horse(1, 9, true));
        addPiece(new Horse(7, 9, true));
        addPiece(new Chariot(8,9,true));

        addPiece(new Chariot(0, 0, false));
        addPiece(new Horse(1, 0, false));
        addPiece(new Horse(7, 0, false));
        addPiece(new Chariot(8,0,false));
    }

    private void addPiece(Piece p) {
        grid[p.y][p.x] = p;
    }

    public void printBoard() {
        System.out.println("   0   1   2   3   4   5   6   7   8");
        System.out.println(" +---+---+---+---+---+---+---+---+---+");

        for(int y = 0; y < 10; y++) {
            System.out.print(y + "|");
            for(int x = 0; x < 9; x++) {
                Piece p = grid[y][x];
                if(p != null) {
                    System.out.print(p.getSymbol() + "|");
                }
                else {
                    System.out.print(" . |");
                }
            }
            System.out.println();

            if(y == 4) {
                System.out.println(" | ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ | Sông Hà");
            } else {
                System.out.println(" +---+---+---+---+---+---+---+---+---+");
            }
        }
    }
}
