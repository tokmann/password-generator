import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;

/**
 * Генератор паролей.
 *
 * Приложение позволяет генерировать случайные пароли с заданными
 * параметрами (длина, набор используемых символов) и оценивать
 * их сложность (надёжность).
 */
public class PasswordGenerator {

    private final String UPPER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String LOWER_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private final String DIGIT_CHARS = "0123456789";
    private final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}";

    private final SecureRandom RANDOM = new SecureRandom();

    /**
     * Функция 1: Генерация одного случайного пароля с заданными параметрами.
     *
     * @param length      желаемая длина пароля (должна быть > 0)
     * @param useUpper    включать ли заглавные латинские буквы
     * @param useLower    включать ли строчные латинские буквы
     * @param useDigits   включать ли цифры
     * @param useSpecial  включать ли специальные символы
     * @return сгенерированный пароль в виде строки
     * @throws IllegalArgumentException если length <= 0 или не выбран ни один тип символов
     */
    public String generatePassword(int length, boolean useUpper, boolean useLower,
                                           boolean useDigits, boolean useSpecial) {
        if (length <= 0) {
            throw new IllegalArgumentException("Длина пароля должна быть положительной");
        }

        StringBuilder pool = new StringBuilder();
        if (useUpper) pool.append(UPPER_CHARS);
        if (useLower) pool.append(LOWER_CHARS);
        if (useDigits) pool.append(DIGIT_CHARS);
        if (useSpecial) pool.append(SPECIAL_CHARS);

        if (pool.length() == 0) {
            throw new IllegalArgumentException("Нужно выбрать хотя бы один тип символов");
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(pool.length());
            password.append(pool.charAt(index));
        }
        return password.toString();
    }

    /**
     * Функция 2: Генерация списка из нескольких паролей с одинаковыми параметрами.
     *
     * @param count       количество паролей, которые нужно сгенерировать
     * @param length      длина каждого пароля
     * @param useUpper    включать ли заглавные буквы
     * @param useLower    включать ли строчные буквы
     * @param useDigits   включать ли цифры
     * @param useSpecial  включать ли специальные символы
     * @return список сгенерированных паролей
     */
    public List<String> generateMultiplePasswords(int count, int length, boolean useUpper,
                                                           boolean useLower, boolean useDigits,
                                                           boolean useSpecial) {
        List<String> passwords = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            passwords.add(generatePassword(length, useUpper, useLower, useDigits, useSpecial));
        }
        return passwords;
    }

    /**
     * Функция 3: Проверка, содержит ли пароль хотя бы одну заглавную букву.
     *
     * @param password проверяемый пароль
     * @return true, если есть хотя бы одна заглавная буква
     */
    public boolean containsUpperCase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Функция 4: Проверка, содержит ли пароль хотя бы одну цифру.
     *
     * @param password проверяемый пароль
     * @return true, если есть хотя бы одна цифра
     */
    public boolean containsDigit(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Функция 5: Проверка, содержит ли пароль хотя бы один специальный символ.
     *
     * НАМЕРЕННАЯ ОШИБКА:
     * набор проверяемых символов ("!@#$%") НЕ совпадает с набором символов,
     * которые реально используются при генерации пароля (SPECIAL_CHARS,
     * включающим также ^&*()-_=+[]{}). Из-за этого пароль, содержащий,
     * например, символ '&' или '*', будет ошибочно признан НЕ содержащим
     * специальных символов.
     *
     * @param password проверяемый пароль
     * @return true, если есть хотя бы один "распознаваемый" спецсимвол
     */
    public boolean containsSpecialChar(String password) {
        String checkedChars = "!@#$%"; // BUG: неполный набор символов (должен совпадать со SPECIAL_CHARS)
        for (char c : password.toCharArray()) {
            if (checkedChars.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Функция 6: Оценка сложности (надёжности) пароля.
     *
     * Правила оценки:
     * - длина < 8 символов -> "Слабый"
     * - длина >= 8, но выполнено менее 3 из 4 условий (верхний регистр,
     *   нижний регистр, цифры, спецсимволы) -> "Средний"
     * - длина >= 8 и выполнены минимум 3 из 4 условий -> "Сильный"
     *
     * @param password проверяемый пароль
     * @return строка с оценкой: "Слабый", "Средний" или "Сильный"
     */
    public String checkPasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return "Слабый";
        }

        int criteriaMet = 0;
        if (containsUpperCase(password)) criteriaMet++;
        if (containsDigit(password)) criteriaMet++;
        if (containsSpecialChar(password)) criteriaMet++;
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        if (hasLower) criteriaMet++;

        if (criteriaMet >= 3) {
            return "Сильный";
        } else {
            return "Средний";
        }
    }
}
