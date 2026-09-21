import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Простой графический интерфейс (Swing) для генератора паролей.
 *
 * Интерфейс позволяет:
 * - задать длину пароля;
 * - выбрать, какие типы символов включать;
 * - сгенерировать пароль по кнопке;
 * - увидеть оценку сложности сгенерированного пароля.
 *
 * Использует методы класса PasswordGenerator, интерфейс не содержит
 * собственной бизнес-логики (только вызовы методов и отображение).
 */
public class PasswordGeneratorGUI extends JFrame {

    private final JSpinner lengthSpinner;
    private final JCheckBox upperCheckBox;
    private final JCheckBox lowerCheckBox;
    private final JCheckBox digitsCheckBox;
    private final JCheckBox specialCheckBox;
    private final JTextField resultField;
    private final JLabel strengthLabel;
    private final PasswordGenerator passwordGenerator;

    public PasswordGeneratorGUI() {
        super("Генератор паролей");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel settingsPanel = new JPanel(new GridLayout(5, 2, 8, 8));

        settingsPanel.add(new JLabel("Длина пароля:"));
        lengthSpinner = new JSpinner(new SpinnerNumberModel(12, 1, 128, 1));
        settingsPanel.add(lengthSpinner);

        upperCheckBox = new JCheckBox("Заглавные буквы (A-Z)", true);
        settingsPanel.add(upperCheckBox);
        settingsPanel.add(new JLabel());

        lowerCheckBox = new JCheckBox("Строчные буквы (a-z)", true);
        settingsPanel.add(lowerCheckBox);
        settingsPanel.add(new JLabel());

        digitsCheckBox = new JCheckBox("Цифры (0-9)", true);
        settingsPanel.add(digitsCheckBox);
        settingsPanel.add(new JLabel());

        specialCheckBox = new JCheckBox("Спецсимволы (!@#$%...)", true);
        settingsPanel.add(specialCheckBox);
        settingsPanel.add(new JLabel());

        add(settingsPanel, BorderLayout.NORTH);

        JButton generateButton = new JButton("Сгенерировать пароль");
        generateButton.addActionListener(this::onGenerateClicked);
        add(generateButton, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        resultField = new JTextField();
        resultField.setEditable(false);
        resultField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        resultPanel.add(resultField);

        strengthLabel = new JLabel(" ");
        strengthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultPanel.add(strengthLabel);

        add(resultPanel, BorderLayout.SOUTH);

        setSize(520, 400);
        setLocationRelativeTo(null);

        passwordGenerator = new PasswordGenerator();
    }

    /**
     * Обработчик нажатия на кнопку «Сгенерировать пароль».
     * Считывает параметры из формы, вызывает методы PasswordGenerator
     * и отображает результат.
     */
    private void onGenerateClicked(ActionEvent event) {
        int length = (Integer) lengthSpinner.getValue();
        boolean useUpper = upperCheckBox.isSelected();
        boolean useLower = lowerCheckBox.isSelected();
        boolean useDigits = digitsCheckBox.isSelected();
        boolean useSpecial = specialCheckBox.isSelected();

        try {
            String password = passwordGenerator.generatePassword(
                    length, useUpper, useLower, useDigits, useSpecial);
            String strength = passwordGenerator.checkPasswordStrength(password);

            resultField.setText(password);
            strengthLabel.setText("Сложность: " + strength);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Некорректные параметры",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PasswordGeneratorGUI gui = new PasswordGeneratorGUI();
            gui.setVisible(true);
        });
    }
}
