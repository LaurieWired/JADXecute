package jadx.gui.plugins.jadxecute;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import jadx.gui.jobs.BackgroundExecutor;
import jadx.gui.settings.JadxSettings;
import jadx.gui.ui.MainWindow;
import jadx.gui.utils.UiUtils;

public class JadxecuteDialog extends JDialog {
	private static final Logger LOG = LoggerFactory.getLogger(JadxecuteDialog.class);

	private final transient JadxSettings settings;
	private final transient MainWindow mainWindow;

	public JadxecuteDialog(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		this.settings = mainWindow.getSettings();
		initUI();
	}

	private void initUI() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		// Input and output code areas
		JLabel codeInputDescription = new JLabel("Java Input");
		codeInputDescription.setPreferredSize(new Dimension(80, 16));
		RSyntaxTextArea codeInputArea = new RSyntaxTextArea(getDefaultCodeInputText());
		codeInputArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		JScrollPane codeInputScrollPanel = new JScrollPane(codeInputArea);
		codeInputScrollPanel.setPreferredSize(new Dimension(550, 200));
		
		JLabel consoleOutputDescription = new JLabel("Console Output");
		consoleOutputDescription.setPreferredSize(new Dimension(80, 16));
		consoleOutputDescription.setBorder(new EmptyBorder(10, 0, 10, 0));
		JTextArea consoleOutputArea = new JTextArea("  ");
		consoleOutputArea.setEditable(false);
		consoleOutputArea.setBackground(Color.BLACK);
		consoleOutputArea.setForeground(Color.WHITE);

		JScrollPane consoleOutputScrollPanel = new JScrollPane(consoleOutputArea);
		consoleOutputScrollPanel.setPreferredSize(new Dimension(550, 100));

		// Input and output code areas
		JPanel codePanel = initCodePanel(codeInputDescription, codeInputScrollPanel, consoleOutputDescription, consoleOutputScrollPanel);
		JPanel filePanel = initFilePanel(codeInputArea);
		
		// Buttons for running and closing the jadxecute dialog
		JPanel bottomPan = new JPanel();
		bottomPan.setLayout(new BorderLayout());
		JPanel buttonPane = new JPanel();
		JLabel statusLabel = new JLabel("Status: Ready");
		statusLabel.setPreferredSize(new Dimension(80, 16));
		JButton run = new JButton("Run");
		JButton close = new JButton("Close");
		close.addActionListener(event -> close());
		run.addActionListener(event -> runUserCode(codeInputArea, consoleOutputArea, statusLabel, run));
		buttonPane.add(run);
		buttonPane.add(close);
		bottomPan.add(statusLabel, BorderLayout.WEST);
		bottomPan.add(buttonPane, BorderLayout.CENTER);
		getRootPane().setDefaultButton(close);
		
		// Add file panel and code panel to a sub panel
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(codePanel, BorderLayout.CENTER);
		
		// Add left panel and buttons panel to main panel
		mainPanel.add(leftPanel, BorderLayout.WEST);
		mainPanel.add(bottomPan, BorderLayout.SOUTH);
		
		// Add code examples panel to east position of main panel
		mainPanel.add(initCodeExamplesPanel(codeInputArea, filePanel), BorderLayout.EAST);
		finishUI(mainPanel);
		
	}

	private JPanel initCodePanel(JLabel codeInputDescription, JScrollPane codeInputScrollPanel, 
			JLabel consoleOutputDescription, JScrollPane consoleOutputScrollPanel) {

		JPanel codePanel = new JPanel();
		codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.Y_AXIS));
		codePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		codeInputDescription.setAlignmentX(Component.LEFT_ALIGNMENT);
		codeInputScrollPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		consoleOutputDescription.setAlignmentX(Component.LEFT_ALIGNMENT);
		consoleOutputScrollPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		codePanel.add(codeInputDescription);
		codePanel.add(codeInputScrollPanel);
		codePanel.add(consoleOutputDescription);
		codePanel.add(consoleOutputScrollPanel);

		return codePanel;
	}

	private JPanel initCodeExamplesPanel(RSyntaxTextArea codeInputArea, JPanel filePanel) {
		// Add options on the right to display different code examples
		JPanel codeExamplesPanel = new JPanel();
		codeExamplesPanel.setLayout(new BorderLayout());
		codeExamplesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		JLabel scriptSelection = new JLabel("Select Template:");
		
		JScrollPane exampleScrollPane = initCodeExamplesListeners(codeInputArea);
		JPanel southExamplesPanel = new JPanel();
		southExamplesPanel.setLayout(new BorderLayout());
		southExamplesPanel.add(scriptSelection, BorderLayout.NORTH);
		southExamplesPanel.add(exampleScrollPane, BorderLayout.CENTER);
		codeExamplesPanel.add(filePanel, BorderLayout.NORTH);
		codeExamplesPanel.add(southExamplesPanel, BorderLayout.CENTER);
		codeExamplesPanel.setPreferredSize(new Dimension(200, 400));

		return codeExamplesPanel;
	}

	private void finishUI(JPanel mainPanel) {
		Container contentPane = getContentPane();
		contentPane.add(mainPanel);
		
		setTitle("JADXecute");
		pack();
		if (!mainWindow.getSettings().loadWindowPos(this)) {
			setSize(800, 500);
		}
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		UiUtils.addEscapeShortCutToDispose(this);
	}

	private JScrollPane initCodeExamplesListeners(RSyntaxTextArea codeInputArea) {
		// Populate code examples
		Map<String, String> keyValuePairs = addCodeExamplesList();
		String[] keyArray = keyValuePairs.keySet().toArray(new String[0]);
		JList<String> exampleList = new JList<>(keyArray);
		// loop through key-value pair and create action listener for each
		for (int i = 0; i < keyArray.length; i++) {
			exampleList.addListSelectionListener(event -> {
				if (!event.getValueIsAdjusting()) {
					codeInputArea.setText(keyValuePairs.get(exampleList.getSelectedValue()));
				}
			});
		}

		exampleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		return new JScrollPane(exampleList);
	}

	private JPanel initFilePanel(RSyntaxTextArea codeInputArea) {
		JPanel filePanel = new JPanel();
		filePanel.setLayout(new BorderLayout());
		filePanel.setBorder(new EmptyBorder(10, 0, 10, 0));
		
		JLabel fileLabel = new JLabel("Input Java File: ");
		JTextField fileField = new JTextField(20);
		JButton fileButton = new JButton("Browse");
		fileButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			int returnValue = fileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				fileField.setText(selectedFile.getAbsolutePath());

				// Now read in the file and populate the Java input area
				try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
					String line;
					StringBuilder sb = new StringBuilder();
					while ((line = br.readLine()) != null) {
						sb.append(line);
						sb.append(System.lineSeparator());
					}
					codeInputArea.setText(sb.toString());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		filePanel.add(fileLabel, BorderLayout.NORTH);
		filePanel.add(fileField, BorderLayout.CENTER);
		filePanel.add(fileButton, BorderLayout.EAST);
		
		return filePanel;
	}

	private Map<String, String> addCodeExamplesList() {
		String resourceName = "/jadxecute/codeexamples.properties";
        try (InputStream is = JadxecuteDialog.class.getResourceAsStream(resourceName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

			Map<String, String> keyValuePairs = new HashMap<>();

            String line;
			String currentKey = null;
            StringBuilder currentValue = new StringBuilder();

			boolean readingValue = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("###")) {
                    // store the current key-value pair
                    if (currentKey != null && currentValue.length() > 0) {
                        keyValuePairs.put(currentKey, currentValue.toString());
                        currentValue = new StringBuilder();
                    }
                    // update the current key
                    currentKey = line.substring(3);
                } else if ((readingValue || line.startsWith("```")) && currentKey != null) {
					if (!readingValue) {
						readingValue = true;
					} else if(readingValue && line.startsWith("```")) {
						readingValue = false;
					} else if (readingValue) {
						// add the line to the current value
						currentValue.append(line + "\n");
					}
                }
            }

			// store the last key-value pair
			if (currentKey != null && currentValue.length() > 0) {
				keyValuePairs.put(currentKey, currentValue.toString());
			}

			// print the key-value pairs
			for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
				LOG.info("Key: " + entry.getKey());
				LOG.info("Value: " + entry.getValue());
			}

			return keyValuePairs;

        } catch (IOException e) {
            LOG.error("Failed reading codeexamples.properties file.");
            e.printStackTrace();
        }

		return null;
	}

	// Set the default text to populate the code input field
	private String getDefaultCodeInputText() {
		String defaultText = "import jadx.gui.ui.MainWindow;\n" +
			"\n" +
			"public class UserCodeClass {\n" +
			"    public static String userCodeMain(MainWindow mainWindow) {\n" +
			"        String jadxecuteOutput = \"Example output...\";\n" +
			"\n" +
			"        // Your code goes here!\n" +
			"\n" +
			"        return jadxecuteOutput;\n" +
			"    }\n" +
			"}\n";
		return defaultText;
	}

	private void runUserCode(RSyntaxTextArea codeInputArea, JTextArea consoleOutputArea, JLabel statusLabel, JButton run) {
		statusLabel.setText("Status: Running...");
		statusLabel.setForeground(Color.ORANGE);

		BackgroundExecutor executor = mainWindow.getBackgroundExecutor();
		executor.execute("Jadexecute Task", () -> executeBackgroundTask(codeInputArea, consoleOutputArea, statusLabel), 
			analysisStatus -> finishTask(consoleOutputArea, statusLabel));
	}

	private void finishTask(JTextArea consoleOutputArea, JLabel statusLabel) {
		if (consoleOutputArea.getText().contains("Java compilation error")) {
			statusLabel.setText("Status: Error");
			statusLabel.setForeground(Color.RED);
		} else {
			statusLabel.setText("Status: Done");
			statusLabel.setForeground(Color.GREEN);
		}
	}

	private void executeBackgroundTask(RSyntaxTextArea codeInputArea, JTextArea consoleOutputArea, JLabel statusLabel) {
		String codeInput = codeInputArea.getText();

		try{
            UserCodeLoader userCodeLoader = new UserCodeLoader();
			consoleOutputArea.setText(userCodeLoader.runInputCode(codeInput, mainWindow));
        } catch (Exception e) {
            e.printStackTrace();
			consoleOutputArea.setText("Error running user code");
			statusLabel.setText("Status: Error");
			statusLabel.setForeground(Color.RED);
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
