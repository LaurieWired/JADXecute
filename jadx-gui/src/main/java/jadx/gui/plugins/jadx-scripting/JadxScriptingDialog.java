package jadx.gui.plugins.jadxscripting;

import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import javax.swing.*;
import java.awt.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.gui.settings.JadxSettings;
import jadx.gui.ui.MainWindow;
import jadx.gui.utils.UiUtils;

import jadx.gui.JadxWrapper;

public class JadxScriptingDialog extends JDialog {
	private static final Logger LOG = LoggerFactory.getLogger(JadxScriptingDialog.class);

	private final transient JadxSettings settings;
	private final transient MainWindow mainWindow;

	public JadxScriptingDialog(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		this.settings = mainWindow.getSettings();
		initUI();
	}

	private void initUI() {
        JadxWrapper jadxWrapper = new JadxWrapper(mainWindow);
        jadxWrapper.open();

		JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Input and output code areas
		JLabel codeInputDescription = new JLabel("Java input:");
        JTextArea codeInputArea = new JTextArea(getDefaultCodeInputText());
		JScrollPane codeInputScrollPanel = new JScrollPane(codeInputArea); 
		JLabel consoleOutputDescription = new JLabel("Console output:");
		JTextArea consoleOutputArea = new JTextArea("  ");
		JScrollPane consoleOutputScrollPanel = new JScrollPane(consoleOutputArea);

		// Buttons for running and closing the JADX Scripting dialog
		JPanel buttonPane = new JPanel();
		JButton run = new JButton("Run");
		JButton close = new JButton("Close");
		close.addActionListener(event -> close());
		run.addActionListener(event -> runUserCode(codeInputArea, consoleOutputArea));
		buttonPane.add(run);
		buttonPane.add(close);
		getRootPane().setDefaultButton(close);

		Container contentPane = getContentPane();

		mainPanel.add(codeInputDescription);
		mainPanel.add(codeInputScrollPanel);
		mainPanel.add(consoleOutputDescription);
		mainPanel.add(consoleOutputScrollPanel);
		mainPanel.add(buttonPane);

		contentPane.add(mainPanel);

		setTitle("JADX Scripting");
		pack();
		if (!mainWindow.getSettings().loadWindowPos(this)) {
			setSize(700, 500);
		}
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		UiUtils.addEscapeShortCutToDispose(this);
	}

	// Set the default text to populate the code input field
	private String getDefaultCodeInputText() {
		String defaultText = "import jadx.gui.ui.MainWindow;\n" +
			"\n" +
			"public class UserCodeClass {\n" +
			"    public static String userCodeMain(MainWindow mainWindow) {\n" +
			"        String jadxScriptingOutput = \"Example output...\";\n" +
			"\n" +
			"        // Your code goes here!\n" +
			"\n" +
			"        return jadxScriptingOutput;\n" +
			"    }\n" +
			"}\n";
		return defaultText;
	}

	private void runUserCode(JTextArea codeInputArea, JTextArea consoleOutputArea) {
        String codeInput = codeInputArea.getText();

		try{
            UserCodeLoader userCodeLoader = new UserCodeLoader();
			consoleOutputArea.setText(userCodeLoader.runInputCode(codeInput, mainWindow));
        } catch (Exception e) {
            e.printStackTrace();
			consoleOutputArea.setText("Error running user code");
        }
	}

	private void close() {
		dispose();
	}

	@Override
	public void dispose() {
		settings.saveWindowPos(this);
		super.dispose();
	}
}
