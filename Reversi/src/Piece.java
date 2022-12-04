import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Piece {
    private Colour colour;

    // Up, Down, Left, Right, L-U, L-D, R-U, R-D;
    private Integer[] directions = new Integer[8];

    public Piece(String col) {
        setColour(col);
    }

    public Piece(Colour col) {
        setColour(col);
    }

    public Piece() {
        colour = Colour.EMPTY;
    }

    public Piece(@NotNull Piece piece) {
        this.colour = piece.getColour();
        this.directions = piece.copyDirections();
    }

    public Colour getColour() {
        return colour;
    }

    private void setColour(Colour col) {
        if (col == Colour.WHITE || col == Colour.BLACK || col == Colour.POSSIBLE_MOVE || col == Colour.EMPTY) {
            colour = col;
            return;
        }
        System.out.println("Ошибка при выбор цвета. Допустимые цвета: black, white, empty, pos_move.");
    }

    private void setColour(@NotNull String col) {
        switch (col) {
            case "white" -> colour = Colour.WHITE;
            case "black" -> colour = Colour.BLACK;
            case "empty" -> colour = Colour.EMPTY;
            case "pos_move" -> colour = Colour.POSSIBLE_MOVE;
            default -> System.out.println("Ошибка при выбор цвета. Допустимые цвета: black, white, empty, pos_move.");
        }
    }

    public void setPossibleMove() {
        if (Objects.equals(colour, Colour.EMPTY)) {
            setColour(Colour.POSSIBLE_MOVE);
        }
    }

    public void clearPossibleMove() {
        if (Objects.equals(colour, Colour.POSSIBLE_MOVE)) {
            setColour(Colour.EMPTY);
        }
    }

    public void changeColour() {
        if (Objects.equals(colour, Colour.WHITE)) {
            setColour(Colour.BLACK);
        } else if (Objects.equals(colour, Colour.BLACK)) {
            setColour(Colour.WHITE);
        }
    }

    public void setDirection(Integer place, Integer value) {
        if (place < 8 && place > -1) {
            directions[place] = value;
        }
    }

    public void incDirection(Integer place) {
        if (place < 8 && place > -1) {
            if (directions[place] == null) {
                directions[place] = 0;
            }
            directions[place]++;
        }
    }

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

    public void clearDirections() {
        for (int i = 0; i < 8; i++) {
            directions[i] = 0;
        }
    }

    @Contract(pure = true)
    public Integer @NotNull [] copyDirections() {
        Integer[] res = new Integer[8];
        for (int i = 0; i < 8; i++) {
            res[i] = directions[i];
        }
        return res;
    }

    public Integer getSumDirections() {
        int res = 0;
        for (var a : directions) {
            if (a != null) {
                res += a;
            }
        }
        return res;
    }

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
