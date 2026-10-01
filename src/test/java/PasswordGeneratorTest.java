import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordGenerator — тесты бизнес-логики")
class PasswordGeneratorTest {

    private PasswordGenerator gen;

    @BeforeEach
    void setUp() {
        gen = new PasswordGenerator();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 8, 12, 16, 64, 128})
    @DisplayName("generatePassword: длина пароля соответствует запрошенной")
    void testGeneratePasswordLength(int length) {
        String pwd = gen.generatePassword(length, true, true, true, true);
        assertNotNull(pwd);
        assertEquals(length, pwd.length());
    }

    @Test
    @DisplayName("generatePassword: length = 0 выбрасывает IllegalArgumentException")
    void testGeneratePasswordZeroLength() {
        assertThrows(IllegalArgumentException.class,
                () -> gen.generatePassword(0, true, true, true, true));
    }

    @Test
    @DisplayName("generatePassword: length < 0 выбрасывает IllegalArgumentException")
    void testGeneratePasswordNegativeLength() {
        assertThrows(IllegalArgumentException.class,
                () -> gen.generatePassword(-10, true, true, true, true));
    }

    @Test
    @DisplayName("generatePassword: все наборы выключены выбрасывает IllegalArgumentException")
    void testGeneratePasswordNoCharset() {
        assertThrows(IllegalArgumentException.class,
                () -> gen.generatePassword(10, false, false, false, false));
    }

    @Test
    @DisplayName("generatePassword: только цифры дают строку из [0-9]")
    void testGeneratePasswordOnlyDigits() {
        String pwd = gen.generatePassword(50, false, false, true, false);
        assertTrue(pwd.matches("[0-9]+"),
                "Ожидались только цифры, получено: " + pwd);
    }

    @Test
    @DisplayName("generatePassword: только заглавные дают строку из [A-Z]")
    void testGeneratePasswordOnlyUpperCase() {
        String pwd = gen.generatePassword(50, true, false, false, false);
        assertTrue(pwd.matches("[A-Z]+"),
                "Ожидались только A-Z, получено: " + pwd);
    }

    @Test
    @DisplayName("generatePassword: только строчные дают строку из [a-z]")
    void testGeneratePasswordOnlyLowerCase() {
        String pwd = gen.generatePassword(50, false, true, false, false);
        assertTrue(pwd.matches("[a-z]+"),
                "Ожидались только a-z, получено: " + pwd);
    }

    @Test
    @DisplayName("generatePassword: все символы входят в разрешённый алфавит")
    void testGeneratePasswordAlphabet() {
        String allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789"
                + "!@#$%^&*()-_=+[]{}";
        String pwd = gen.generatePassword(300, true, true, true, true);
        for (char c : pwd.toCharArray()) {
            assertTrue(allowed.indexOf(c) >= 0,
                    "Найден символ вне алфавита: '" + c + "'");
        }
    }

    @Test
    @DisplayName("generatePassword: два вызова подряд дают разные пароли")
    void testGeneratePasswordRandomness() {
        String a = gen.generatePassword(32, true, true, true, true);
        String b = gen.generatePassword(32, true, true, true, true);
        assertNotEquals(a, b, "Дважды получен одинаковый пароль");
    }

    @Test
    @DisplayName("generateMultiplePasswords: возвращает список запрошенного размера")
    void testGenerateMultiplePasswordsCount() {
        List<String> list = gen.generateMultiplePasswords(5, 12, true, true, true, true);
        assertEquals(5, list.size());
    }

    @Test
    @DisplayName("generateMultiplePasswords: у всех паролей запрошенная длина")
    void testGenerateMultiplePasswordsLength() {
        List<String> list = gen.generateMultiplePasswords(10, 16, true, true, true, true);
        for (String pwd : list) {
            assertEquals(16, pwd.length());
        }
    }

    @Test
    @DisplayName("generateMultiplePasswords: count = 0 возвращает пустой список")
    void testGenerateMultiplePasswordsZeroCount() {
        List<String> list = gen.generateMultiplePasswords(0, 12, true, true, true, true);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("generateMultiplePasswords: все пароли уникальны")
    void testGenerateMultiplePasswordsUnique() {
        List<String> list = gen.generateMultiplePasswords(50, 16, true, true, true, true);
        assertEquals(50, new HashSet<>(list).size(),
                "Найдены дубликаты среди сгенерированных паролей");
    }


    @Test
    @DisplayName("containsUpperCase: возвращает true при наличии заглавной буквы")
    void testContainsUpperCaseTrue() {
        assertTrue(gen.containsUpperCase("abcA"));
        assertTrue(gen.containsUpperCase("ABCDEF"));
        assertTrue(gen.containsUpperCase("1A2b3"));
    }

    @Test
    @DisplayName("containsUpperCase: возвращает false при отсутствии заглавных букв")
    void testContainsUpperCaseFalse() {
        assertFalse(gen.containsUpperCase("abcdef"));
        assertFalse(gen.containsUpperCase("123!@#"));
        assertFalse(gen.containsUpperCase(""));
    }

    @Test
    @DisplayName("containsDigit: возвращает true при наличии цифры")
    void testContainsDigitTrue() {
        assertTrue(gen.containsDigit("abc1"));
        assertTrue(gen.containsDigit("0"));
        assertTrue(gen.containsDigit("!@#9"));
    }

    @Test
    @DisplayName("containsDigit: возвращает false при отсутствии цифр")
    void testContainsDigitFalse() {
        assertFalse(gen.containsDigit("abcXYZ!@#"));
        assertFalse(gen.containsDigit(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"!", "@", "#", "$", "%"})
    @DisplayName("containsSpecialChar: распознаёт символы из набора '!@#$%'")
    void testContainsSpecialCharKnown(String pwd) {
        assertTrue(gen.containsSpecialChar(pwd),
                "Не распознан спецсимвол из '!@#$%': " + pwd);
    }

    @ParameterizedTest
    @ValueSource(strings = {"^", "&", "*", "(", ")", "-", "_", "=", "+", "[", "]", "{", "}"})
    @DisplayName("containsSpecialChar: должен распознавать ВСЕ символы из SPECIAL_CHARS (демонстрация бага)")
    void testContainsSpecialCharAllGeneratedSpecials(String pwd) {
        assertTrue(gen.containsSpecialChar(pwd),
                "ЗАЛОЖЕННАЯ ОШИБКА: '" + pwd +
                        "' входит в SPECIAL_CHARS генератора, но не распознаётся как спецсимвол");
    }

    @Test
    @DisplayName("containsSpecialChar: возвращает false при отсутствии спецсимволов")
    void testContainsSpecialCharFalse() {
        assertFalse(gen.containsSpecialChar("abcXYZ123"));
        assertFalse(gen.containsSpecialChar(""));
    }


    @Test
    @DisplayName("checkPasswordStrength: null возвращает «Слабый»")
    void testStrengthNull() {
        assertEquals("Слабый", gen.checkPasswordStrength(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "Ab1!", "Abc1!x", "abcdefg"})
    @DisplayName("checkPasswordStrength: длина < 8 возвращает «Слабый»")
    void testStrengthShort(String pwd) {
        assertEquals("Слабый", gen.checkPasswordStrength(pwd));
    }

    @Test
    @DisplayName("checkPasswordStrength: только строчные при длине >= 8 возвращают «Средний»")
    void testStrengthMediumOnlyLower() {
        assertEquals("Средний", gen.checkPasswordStrength("abcdefgh"));
    }

    @Test
    @DisplayName("checkPasswordStrength: 2 условия из 4 при длине >= 8 возвращают «Средний»")
    void testStrengthMediumTwoCriteria() {
        assertEquals("Средний", gen.checkPasswordStrength("Abcdefgh"));
    }

    @Test
    @DisplayName("checkPasswordStrength: 3 условия из 4 возвращают «Сильный»")
    void testStrengthStrongThreeCriteria() {
        assertEquals("Сильный", gen.checkPasswordStrength("Abcdefg1"));
    }

    @Test
    @DisplayName("checkPasswordStrength: 4 условия из 4 со спецсимволом из '!@#$%' возвращают «Сильный»")
    void testStrengthStrongAllCriteria() {
        assertEquals("Сильный", gen.checkPasswordStrength("Abcdef1!"));
    }

    @Test
    @DisplayName("checkPasswordStrength: 4 условия из 4 со спецсимволом '&' должны давать «Сильный» (демонстрация бага)")
    void testStrengthStrongWithSpecialBug() {
        assertEquals("Сильный", gen.checkPasswordStrength("Abcdef1&"),
                "ЗАЛОЖЕННАЯ ОШИБКА: '&' генерируется, но не распознаётся " +
                        "как спецсимвол — пароль ошибочно занижен до 'Средний'");
    }
}