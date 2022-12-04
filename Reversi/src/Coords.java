import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Запись для удобного хранения координат
 *
 * @param x Первая координата
 * @param y Вторая координата
 */
public record Coords(int x, int y) {

    /**
     * Сравнение координат со строковым представлением
     *
     * @param str Строковое представление колординат
     * @return true, если координаты совпаадают. false иначе
     */
    public boolean equals(@NotNull String str) {
        return Objects.equals(this.toString(), str);
    }

    /**
     * Переопределённый toSting()
     *
     * @return Красивую текстовую версию координат
     */
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        char a = (char) (y + 'a');
        int b = 8 - x;
        return a + String.valueOf(b);
    }
}
