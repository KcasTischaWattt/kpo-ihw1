import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Coords(int x, int y) {
    public boolean equals(@NotNull String str) {
        return Objects.equals(this.toString(), str);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        char a = (char) (y + 'a');
        int b = 8 - x;
        return a + String.valueOf(b);
    }
}
