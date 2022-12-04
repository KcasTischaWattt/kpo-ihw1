import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class Board {

    private final Piece[][] board = new Piece[8][8];
    private int whites;
    private int blacks;

    public Board(Board board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = new Piece(board.getPiece(i, j));
            }
        }
        this.whites = board.getWhites();
        this.blacks = board.getBlacks();
    }

    public Board() {
        for (var i = 0; i < 8; i++) {
            for (var j = 0; j < 8; j++) {
                board[i][j] = new Piece();
            }
        }
        String w = "white";
        String b = "black";
        board[4][4] = new Piece(w);
        board[3][3] = new Piece(w);
        board[3][4] = new Piece(b);
        board[4][3] = new Piece(b);
    }

    @Override
    public String toString() {
        AtomicReference<StringBuilder> res = new AtomicReference<>(new StringBuilder());
        res.get().append("Белые : ").append(getWhites())
                .append(" Чёрные : ").append(getBlacks())
                .append('\n');
        for (int i = 0; i < 8; ++i) {
            res.get().append(8 - i).append(" ┃ ");
            for (int j = 0; j < 8; ++j) {
                res.get().append(board[i][j]).append(j != 7 ? " │ " : " ┃\n");
            }
            if (i != 7) {
                res.get().append("  ┠───┼───┼───┼───┼───┼───┼───┼───┨\n");
            }
        }
        res.get().append("  ┗━━━┷━━━┷━━━┷━━━┷━━━┷━━━┷━━━┷━━━┛\n");
        res.get().append("    a   b   c   d   e   f   g   h  \n");
        return res.toString();
    }

    public void set(Integer x, Integer y, Piece piece) throws IllegalArgumentException {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException("Неверные координаты поля");
        }
        board[x][y] = piece;
    }

    public void set(@NotNull String field, Piece piece) throws IllegalArgumentException {
        char x = field.charAt(0);
        char y = field.charAt(1);
        if (x < 'a' || x > 'h' || y < '1' || y > '8') {
            throw new IllegalArgumentException("Неверные координаты поля");
        }
        x -= 'a';
        y -= '1';
        if (!Objects.equals(board[7 - y][x].getColour(), Colour.POSSIBLE_MOVE)) {
            throw new IllegalArgumentException("Поле уже занято!");
        }
        board[7 - y][x] = piece;
    }

    public Piece getPiece(Integer x, Integer y) throws IllegalArgumentException {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException("Неверные координаты поля");
        }
        return board[x][y];
    }

    public int getWhites() {
        countPieces();
        return whites;
    }

    public int getBlacks() {
        countPieces();
        return blacks;
    }

    private void countPieces() {
        whites = 0;
        blacks = 0;
        for (var s : board) {
            for (var g : s) {
                if (Objects.equals(g.getColour(), Colour.WHITE)) {
                    whites++;
                } else if (Objects.equals(g.getColour(), Colour.BLACK)) {
                    blacks++;
                }
            }
        }
    }

    public void check(int i, int j, int diff_i, int diff_j, int pos, Colour turn, Colour opp) {
        int k = i;
        int m = j;
        do {
            if (k + diff_i != -1 && k + diff_i != 8 && m + diff_j != -1 && m + diff_j != 8) {
                k += diff_i;
                m += diff_j;
            } else {
                break;
            }
            if (Objects.equals(board[k][m].getColour(), opp)) {
                board[i][j].incDirection(pos);
            } else if (Objects.equals(board[k][m].getColour(), turn)) {
                if (board[i][j].getDirection(pos) != 0) {
                    board[i][j].setPossibleMove();
                }
                return;
            } else {
                break;
            }
        } while (0 <= k && k <= 7 && m <= 7 && 0 <= m);
        board[i][j].setDirection(pos, 0);
    }


    public void clearAllDirections() {
        for (var a : board) {
            for (var b : a) {
                b.clearDirections();
            }
        }
    }

    public void changeColours(@NotNull String str) {
        int x = str.charAt(1);
        char y = str.charAt(0);
        x -= '1';
        y -= 'a';
        x = 7 - x;
        Piece cur = board[x][y];
        for (int i = 1; i <= cur.getDirection(0); i++) {
            board[x - i][y].changeColour();
        }
        for (int i = 1; i <= cur.getDirection(1); i++) {
            board[x + i][y].changeColour();
        }
        for (int i = 1; i <= cur.getDirection(2); i++) {
            board[x][y - i].changeColour();
        }
        for (int i = 1; i <= cur.getDirection(3); i++) {
            board[x][y + i].changeColour();
        }
        for (int i = 1; i <= cur.getDirection(4); i++) {
            board[x - i][y - i].changeColour();
        }
        for (int i = 1; i <= cur.getDirection(5); i++) {
            board[x + i][y - i].changeColour();
        }
        for (int i = 1; i <= cur.getDirection(6); i++) {
            board[x - i][y + i].changeColour();
        }
        for (int i = 1; i <= cur.getDirection(7); i++) {
            board[x + i][y + i].changeColour();
        }
    }

    public void changeColours(@NotNull Coords coords) {
        changeColours(coords.toString());
    }

    public int evaluateDirection(int i, int j, int diff_i, int diff_j, Colour turn, Colour opp) {
        int k = i;
        int m = j;
        int sum = 0;
        do {
            if (k + diff_i != -1 && k + diff_i != 8 && m + diff_j != -1 && m + diff_j != 8) {
                k += diff_i;
                m += diff_j;
            } else {
                return 0;
            }
            if (Objects.equals(board[k][m].getColour(), opp)) {
                sum++;
                if (k == 0 || k == 7 || m == 0 || m == 7) {
                    sum++;
                }
            } else if (Objects.equals(board[k][m].getColour(), turn)) {
                return sum;
            } else {
                return 0;
            }
        } while (0 <= k && k <= 7 && m <= 7 && 0 <= m);
        return 0;
    }

    public void clearAllPossibleMoves() {
        for (var a : board) {
            for (var b : a) {
                b.clearPossibleMove();
            }
        }
    }
}
