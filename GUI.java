package Window;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.Stream;

public class GUI extends JFrame {

    public JPanel input;
    public JPanel output;
    public JPanel buttoms;
    public JTextArea inputText;
    public JTextArea outputText;
    public JButton Do;

    public GUI() {
        inputText = new JTextArea(20, 40);
        outputText = new JTextArea(20, 40);
        Do = new JButton(ь"Do");

        Do.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = inputText.getText();

                Program.CSharpCodeProcessor processor = new Program.CSharpCodeProcessor();
                Stream<String> processedLines = processor.processCode(Stream.of(code));

                String processedCode = processedLines.reduce("", (acc, line) -> acc + line + "\n");

                outputText.setText(processedCode);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Input Text:"));
        panel.add(new JScrollPane(inputText));
        panel.add(new JLabel("Output Text:"));
        panel.add(new JScrollPane(outputText));
        panel.add(Do);

        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI());
    }
}