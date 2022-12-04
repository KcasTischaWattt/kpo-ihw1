import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Сущность "Доска"
 */
public final class Board {

    /**
     * Само поле - 8 рядов по 8 фишек
     */
    private final Piece[][] board = new Piece[8][8];

    /**
     * Количество белых фишек
     */
    private int whites;

    /**
     * Количество чёрных фишек
     */
    private int blacks;

    /**
     * Конструктор копирования
     *
     * @param board Доска, которая копируется
     */
    public Board(Board board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = new Piece(board.getPiece(i, j));
            }
        }
        this.whites = board.getWhites();
        this.blacks = board.getBlacks();
    }

    /**
     * Конструктор по умолчанию запускает начальное состояние игры
     */
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

    /**
     * Переопределённый toSting()
     *
     * @return Красивую текстовую версию доски
     */
    @Override
    public String toString() {
        // Это всё авторство иде-шки, она сказла, что так лучше
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

    /**
     * Поставить фишку на поле по координатам
     *
     * @param x     Первая координата клетки
     * @param y     Вторая координата клетки
     * @param piece Фишка, которую надо поставить
     * @throws IllegalArgumentException Если координаты неверные
     */
    public void setPiece(Integer x, Integer y, Piece piece) throws IllegalArgumentException {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException("Неверные координаты поля");
        }
        board[x][y] = piece;
    }

    /**
     * Поставить фишку на поле по координатам(в виде текста)
     *
     * @param field Координаты поля(например, d6)
     * @param piece Фишка, которую надо поставить
     * @throws IllegalArgumentException Если координаты неверные
     */
    public void setPiece(@NotNull String field, Piece piece) throws IllegalArgumentException {
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

    /**
     * Возвращает фишку по координатам
     * Первая координата клетки
     *
     * @param y Вторая координата клетки
     * @return Фишку, находящююся по координатам
     * @throws IllegalArgumentException Если координаты неверные
     */
    public Piece getPiece(Integer x, Integer y) throws IllegalArgumentException {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException("Неверные координаты поля");
        }
        return board[x][y];
    }

    /**
     * Подсчёт количества белых фишек
     *
     * @return количество белых фишек
     */
    public int getWhites() {
        countPieces();
        return whites;
    }

    /**
     * Подсчёт количества чёрных фишек
     *
     * @return количество чёрных фишек
     */
    public int getBlacks() {
        countPieces();
        return blacks;
    }

    /**
     * Подсчёт общего количества фишек
     */
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

    /**
     * Проверка клетки на количаство фишек, которые можно захватить по заданнаму направлению
     *
     * @param i      Первая координата клетки
     * @param j      Втораая координата клетки
     * @param diff_i Направление изменения i
     * @param diff_j Направление изменения j
     * @param pos    Позиция в массиве directions, на которую надо записать
     *               количество доступных к захвату фишек в данном направлении
     * @param turn   Очередь хода
     * @param opp    Цвет противника
     */
    public void check(int i, int j, int diff_i, int diff_j, int pos, Colour turn, Colour opp) {
        int k = i;
        int m = j;
        do {
            // Если достигли кравя доски - выходим из метода, доступных фишек нет
            if (k + diff_i != -1 && k + diff_i != 8 && m + diff_j != -1 && m + diff_j != 8) {
                k += diff_i;
                m += diff_j;
            } else {
                break;
            }
            // Если нашли фишку противоположного нам цвета - добавляем 1 к этому направлению
            if (Objects.equals(board[k][m].getColour(), opp)) {
                board[i][j].incDirection(pos);
                // Если же нашли фишку нашего цвета, и до этого были чужие фишки
                // - добавляем данный ход в список возможных
            } else if (Objects.equals(board[k][m].getColour(), turn)) {
                if (board[i][j].getDirection(pos) != 0) {
                    board[i][j].setPossibleMove();
                }
                return;
                // Если нашли пустую фишку - сворачиваемся
            } else {
                break;
            }
        } while (0 <= k && k <= 7 && m <= 7 && 0 <= m);
        board[i][j].setDirection(pos, 0);
    }

    /**
     * Зануление массива direction всех фишек
     */
    public void clearAllDirections() {
        for (var a : board) {
            for (var b : a) {
                b.clearDirections();
            }
        }
    }

    /**
     * Изменение цветов при размещении фишки на поле
     *
     * @param str Координаты фишки
     */
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

    /**
     * Изменение цветов при размещении фишки на поле
     *
     * @param coords Координаты фишки
     */
    public void changeColours(@NotNull Coords coords) {
        changeColours(coords.toString());
    }


    /**
     * Расчёт ценности фишек в конкретно заданное направление от клетки
     *
     * @param i      Первая координата клетки
     * @param j      Втораая координата клетки
     * @param diff_i Направление изменения i
     * @param diff_j Направление изменения j
     * @param turn   Очередь хода
     * @param opp    Цвет противника
     * @return Ценность фишек по указанному направлению
     */
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

    /**
     * Сначтие всех меток возможного хода с фишек
     */
    public void clearAllPossibleMoves() {
        for (var a : board) {
            for (var b : a) {
                b.clearPossibleMove();
            }
        }
    }
}
