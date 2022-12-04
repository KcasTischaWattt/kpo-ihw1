import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Сущность "Фишка"
 * Она же одновременно является и клеткой на доске, имеет 4 состояния, перечислленных в енаме Colour
 */
public final class Piece {

    /**
     * Цвет фишки
     */
    private Colour colour;

    // Up, Down, Left, Right, L-U, L-D, R-U, R-D;
    /**
     * Количество доступных к захвату фишек по всем направлениям от клетки
     * Направления указаны сверху
     */
    private Integer[] directions = new Integer[8];

    /**
     * Конструктор от цвета
     *
     * @param col Цвет фишки
     */
    public Piece(String col) {
        setColour(col);
    }

    /**
     * Конструктор от цвета
     *
     * @param col Цвет фишки
     */
    public Piece(Colour col) {
        setColour(col);
    }

    /**
     * Констуктор по умолчанию
     */
    public Piece() {
        colour = Colour.EMPTY;
    }

    /**
     * Конструктор копирования
     *
     * @param piece Фшка, которую надо скопировать
     */
    public Piece(@NotNull Piece piece) {
        this.colour = piece.getColour();
        this.directions = piece.copyDirections();
    }

    /**
     * геттер для поля Colour
     *
     * @return цвет фишки
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Устанавливает цвет фишке
     *
     * @param col Цвет
     */
    private void setColour(Colour col) {
        if (col == Colour.WHITE || col == Colour.BLACK || col == Colour.POSSIBLE_MOVE || col == Colour.EMPTY) {
            colour = col;
            return;
        }
        System.out.println("Ошибка при выбор цвета. Допустимые цвета: black, white, empty, pos_move.");
    }

    /**
     * Устанавливает цвет фишке
     *
     * @param col Цвет
     */
    private void setColour(@NotNull String col) {
        switch (col) {
            case "white" -> colour = Colour.WHITE;
            case "black" -> colour = Colour.BLACK;
            case "empty" -> colour = Colour.EMPTY;
            case "pos_move" -> colour = Colour.POSSIBLE_MOVE;
            default -> System.out.println("Ошибка при выбор цвета. Допустимые цвета: black, white, empty, pos_move.");
        }
    }

    /**
     * Меняет состояние фишки на Colour.POSSIBLE_MOVE
     */
    public void setPossibleMove() {
        if (Objects.equals(colour, Colour.EMPTY)) {
            setColour(Colour.POSSIBLE_MOVE);
        }
    }

    /**
     * Убирает состояние фишки Colour.POSSIBLE_MOVE
     */
    public void clearPossibleMove() {
        if (Objects.equals(colour, Colour.POSSIBLE_MOVE)) {
            setColour(Colour.EMPTY);
        }
    }

    /**
     * Меняет цвет фишки
     */
    public void changeColour() {
        if (Objects.equals(colour, Colour.WHITE)) {
            setColour(Colour.BLACK);
        } else if (Objects.equals(colour, Colour.BLACK)) {
            setColour(Colour.WHITE);
        }
    }

    /**
     * Установка количества фишек, оступных к захвату по направлению place
     *
     * @param place Номер направления
     * @param value Количество фишек
     */
    public void setDirection(Integer place, Integer value) {
        if (place < 8 && place > -1) {
            directions[place] = value;
        }
    }

    /**
     * Увеличение количества фишек, доступных для захвата
     * по направлению place, на 1
     *
     * @param place Номер направления
     */
    public void incDirection(Integer place) {
        if (place < 8 && place > -1) {
            if (directions[place] == null) {
                directions[place] = 0;
            }
            directions[place]++;
        }
    }

    /**
     * Возврат количества фишек, доступных
     * для захвата по направлению place
     *
     * @param pos Номер направления
     * @return Значение на позиции pos в массиве directions
     * @throws IllegalArgumentException Неверная позиция pos
     */
    public Integer getDirection(Integer pos) throws IllegalArgumentException {
        if (pos < 8 && pos > -1) {
            if (directions[pos] == null) {
                return 0;
            }
            return directions[pos];
        } else {
            throw new IllegalArgumentException("Неверная позиция");
        }
    }

    /**
     * Зануление массва directions
     */
    public void clearDirections() {
        for (int i = 0; i < 8; i++) {
            directions[i] = 0;
        }
    }

    /**
     * Метод копирования массива directions
     *
     * @return Копию массива directions
     */
    @Contract(pure = true)
    public Integer @NotNull [] copyDirections() {
        Integer[] res = new Integer[8];
        for (int i = 0; i < 8; i++) {
            res[i] = directions[i];
        }
        return res;
    }

    /**
     * Подсчёт суммы всех значений в масиве directions
     *
     * @return Сумма всех значений в масиве directions
     */
    public Integer getSumDirections() {
        int res = 0;
        for (var a : directions) {
            if (a != null) {
                res += a;
            }
        }
        return res;
    }

    /**
     * Переопределённый toSting()
     *
     * @return Красивую текстовую версию фишки
     */
    @Override
    public @NotNull String toString() {
        if (Objects.equals(colour, Colour.WHITE)) {
            return "●";
        } else if (Objects.equals(colour, Colour.BLACK)) {
            return "◯";
        } else if (Objects.equals(colour, Colour.EMPTY)) {
            return " ";
        } else {
            return "◌";
        }
    }
}
