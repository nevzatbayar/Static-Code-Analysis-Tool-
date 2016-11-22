package Arayuz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

import org.apache.commons.io.FilenameUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import BufferCopy.BufferCopyHelper;
import BufferCopy.BufferCopyVisitor;
import DeclarationCatches.DeclarationCatchesVisitor;
import DeclarationCatches.DeclarationHelper;
import EndOfBufferAccess.EndOfBufferAccessHelper;
import EndOfBufferAccess.EndOfBufferAccessVisitor;
import MisInitialization.Visitor;
import MissingResource.MissingResourceHelper;
import MissingResource.MissingResourceVisitor;
import Recursion.RecursionVisitor;
import Recursion.SolveRecursion;
import ScaHelper.ExpressionHelper;
import SymbolTable.Symbol;
import TypeCast.CastHelper;
import TypeCast.CastVisitor;
import UnCheckedReturnParameter.SolveMethodCallExpr;
import UnCheckedReturnParameter.UnCheckedVisitor;

public class ArayuzGui extends JFrame {

	private static final long serialVersionUID = 1L;

	JTextArea lines;

	private Highlighter.HighlightPainter painter;

	private JTextArea tarea, tarea1;

	int i, trn;

	private int counter;

	private JFrame frame;

	private JPanel panel2;

	private JPanel panel;

	private JLabel tp = new JLabel("Number Of True Positive : "), fp = new JLabel(" Number Of False Positive : ");

	File file;
	String userDir = System.getProperty("user.home");
	JFileChooser fileopen = new JFileChooser(userDir + "/Desktop");

	JScrollPane scroll, scroll1;

	JButton dosya, Anlz, testOran;

	ArrayList<String> messages = new ArrayList<>();

	FileFilter filter = new FileNameExtensionFilter("Java Class files", "class");

	FileFilter filter1 = new FileNameExtensionFilter("Java  files", "java");

	ArrayList<JLabel> checkBoxes = new ArrayList<>();
	ArrayList<Integer> isCheck = new ArrayList<>();

	public ArayuzGui() {
		initPencere();

	}

	public void initPencere() {
		frame = new JFrame("STATIC CODE ANALYSIS");
//		new Font("sansserif", Font.BOLD, 12);
		frame.setBounds(0, 0, 900, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		lines = new JTextArea("1");
		lines.setBackground(Color.LIGHT_GRAY);
		lines.setEditable(false);

		panel = new JPanel();
		JScrollPane jsp = new JScrollPane(panel);
		panel.setPreferredSize(new Dimension(2000, 2000));
		panel.setLayout(null);

		panel2 = new JPanel();
		panel2.setLayout(new GridBagLayout());

		panel.add(panel2);

		scroll1 = new JScrollPane(panel2);
		scroll1.setBounds(50, 310, 750, 200);
		panel.add(scroll1);
		panel2.setVisible(true);

		tarea = new JTextArea();
		tarea.setBounds(50, 100, 2000, 200);
		panel.add(tarea);
		tarea.setText("");

		tarea1 = new JTextArea("");
		tarea1.setBounds(500, 60, 300, 30);
		tarea1.setBackground(Color.LIGHT_GRAY);
		panel.add(tarea1);
		tarea1.setEditable(false);

		scroll = new JScrollPane(tarea);
		scroll.setBounds(50, 100, 750, 200);
		panel.add(scroll);

		dosya = new JButton("Open File");
		Anlz = new JButton("Analyze Source");
		testOran = new JButton("Analyze Rate");

		panel.add(Anlz);
		panel.add(dosya);

		tarea.getDocument().addDocumentListener(new DocumentListener() {
			public String getText() {
				int caretPosition = tarea.getDocument().getLength();
				Element root = tarea.getDocument().getDefaultRootElement();
				String text = "1" + System.getProperty("line.separator");
				for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
					text += i + System.getProperty("line.separator");
				}
				return text;
			}

			@Override
			public void changedUpdate(DocumentEvent de) {
				lines.setText(getText());
			}

			@Override
			public void insertUpdate(DocumentEvent de) {
				lines.setText(getText());
			}

			@Override
			public void removeUpdate(DocumentEvent de) {
				lines.setText(getText());
			}

		});

		tarea.setEditable(false);
		scroll.getViewport().add(tarea);
		scroll.setRowHeaderView(lines);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		dosya.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tp.setVisible(false);
				fp.setVisible(false);
				testOran.setVisible(false);
				counter = 0;
				checkBoxes.clear();
				panel2.removeAll();
				panel2.repaint();
				ExpressionHelper.getMessages().clear();
				SolveMethodCallExpr.getMessages().clear();
				BufferCopyHelper.getMessages().clear();
				EndOfBufferAccessHelper.getMessages().clear();
				SolveRecursion.getMessages().clear();
				MissingResourceHelper.getMessages().clear();
				CastHelper.getMessages().clear();
				DeclarationHelper.getMessages().clear();
				messages.clear();
				tarea.setText("");
				fileopen.addChoosableFileFilter(filter);
				fileopen.addChoosableFileFilter(filter1);

				int answer = fileopen.showDialog(fileopen, "Open file");

				if (answer == JFileChooser.APPROVE_OPTION) {
					file = fileopen.getSelectedFile();
					if (file != null) {
						String extension = FilenameUtils.getExtension(file.getName());
						if (extension.equals("java")) {
							tarea1.setText("File Name = " + file.getName());
							try {
								BufferedReader in = new BufferedReader(new FileReader(file));
								String s = in.readLine();
								while (s != null) {
									tarea.append(s);
									tarea.append("\n");
									s = in.readLine();
								}

							} catch (IOException ioe) {
								System.err.println(ioe);
							}

						} else {
							JOptionPane.showMessageDialog(Anlz, "Java Dosyasý seçmelisiniz", "alert",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(Anlz, "Lütfen bir java dosyasý seçiniz", "alert",
								JOptionPane.ERROR_MESSAGE);

					}
				} else {
					if (file != null) {

						try {
							BufferedReader in = new BufferedReader(new FileReader(file));
							String s = in.readLine();
							while (s != null) {
								tarea.append(s);
								tarea.append("\n");
								s = in.readLine();
							}

						} catch (IOException ioe) {
							System.err.println(ioe);
						}
					}
				}

				revalidate();
				repaint();
			}

		});

		Anlz.setBounds(300, 60, 150, 30);
		dosya.setBounds(100, 60, 100, 30);

		Anlz.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				testOran.setVisible(true);
				tp.setVisible(false);
				fp.setVisible(false);
				counter = 0;
				checkBoxes.clear();
				ExpressionHelper.getMessages().clear();
				SolveMethodCallExpr.getMessages().clear();
				BufferCopyHelper.getMessages().clear();
				EndOfBufferAccessHelper.getMessages().clear();
				SolveRecursion.getMessages().clear();
				MissingResourceHelper.getMessages().clear();
				CastHelper.getMessages().clear();
				DeclarationHelper.getMessages().clear();
				messages.clear();
				tarea.getHighlighter().removeAllHighlights();

				if (!tarea.getText().equals("")) {
					CompilationUnit cu = null;
					FileInputStream in = null;
					try {
						in = new FileInputStream(file);

						// parse the file
						cu = JavaParser.parse(in);

					} catch (Exception e1) {
						e1.printStackTrace();
					} finally {
						try {
							in.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					if (cu != null) {
						Visitor visitor = new Visitor();
						visitor.visitDepthFirst(cu);
						messages.addAll(ExpressionHelper.getMessages());
						UnCheckedVisitor uncheck = new UnCheckedVisitor();
						uncheck.visitDepthFirst(cu);
						messages.addAll(SolveMethodCallExpr.getMessages());
						BufferCopyVisitor buffercopy = new BufferCopyVisitor();
						buffercopy.visitDepthFirst(cu);
						messages.addAll(BufferCopyHelper.getMessages());
						EndOfBufferAccessVisitor endofbuffer = new EndOfBufferAccessVisitor();
						endofbuffer.visitDepthFirst(cu);
						messages.addAll(EndOfBufferAccessHelper.getMessages());
						RecursionVisitor recursivevisitor = new RecursionVisitor();
						recursivevisitor.visitDepthFirst(cu);
						messages.addAll(SolveRecursion.getMessages());
						DeclarationCatchesVisitor decvisitor = new DeclarationCatchesVisitor();
						decvisitor.visitDepthFirst(cu);
						messages.addAll(DeclarationHelper.getMessages());
						MissingResourceVisitor misvisitor = new MissingResourceVisitor();
						misvisitor.visitDepthFirst(cu);
						messages.addAll(MissingResourceHelper.getMessages());
						CastVisitor castVisitor = new CastVisitor();
						castVisitor.visitDepthFirst(cu);
						messages.addAll(CastHelper.getMessages());
						Table<String, Node, Symbol> st = visitor.st;
						for (Cell<String, Node, Symbol> cell : st.cellSet()) {
							if (cell.getValue().getReferencedline() == -1
									&& !(cell.getValue().getKind().equals("Method parameter")
											|| cell.getValue().getKind().equals("Constructor parameter")
											|| cell.getValue().getKind().equals("Catch parameter"))) {
								messages.add("Unused variable : " + cell.getValue().getName() + " define at line : "
										+ cell.getValue().getDeclaretedline());
							}
						}
					}

					panel2.removeAll();
					repaint();

					GridBagConstraints gbc = new GridBagConstraints();
					gbc.gridx = 0;
					gbc.gridy = 0;
					gbc.gridwidth = 1;
					gbc.gridheight = 1;

					gbc.anchor = GridBagConstraints.WEST;
					gbc.fill = GridBagConstraints.BOTH;
					gbc.weightx = 0.05;
					gbc.weighty = 0.05;
					Insets ins = new Insets(3, 3, 3, 3);
					gbc.insets = ins;
					GridBagConstraints gbc1 = new GridBagConstraints();
					gbc1.gridx = 1;
					gbc1.gridy = 0;
					gbc1.gridwidth = 1;
					gbc1.gridheight = 1;

					// gbc1.anchor = GridBagConstraints.WEST;
					gbc1.insets = ins;
					gbc1.anchor = GridBagConstraints.WEST;
					gbc1.fill = GridBagConstraints.BOTH;
					gbc1.weightx = 1.0;
					gbc1.weighty = 1.0;
					panel2.updateUI();
					for (i = 0; i < messages.size(); i++) {
						JCheckBox chk = new JCheckBox();
						gbc.gridy = i;
						panel2.add(chk, gbc);
						gbc1.gridy = i;
						JLabel txt = new JLabel(messages.get(i));
						checkBoxes.add(txt);
						panel2.add(txt, gbc1);
						txt.setVisible(true);
						chk.setVisible(true);
						txt.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								JLabel l = (JLabel) e.getSource(); // here

								int index = checkBoxes.indexOf(l);

								JLabel l1 = (JLabel) checkBoxes.get(index);

								String s = messages.get(index);

								s = s.substring(s.lastIndexOf(":") + 2, s.length());//

								int a = Integer.parseInt(s);
								if (!isCheck.contains(index)) {
									try {
										for (JLabel z : checkBoxes) {
											z.setForeground(null);
										}
										tarea.getHighlighter().removeAllHighlights();
										int endIndex = tarea.getLineEndOffset(a - 1);
										int startIndex = tarea.getLineStartOffset(a - 1);
										painter = new DefaultHighlighter.DefaultHighlightPainter(
												new Color(250, 50, 100));
										tarea.getHighlighter().addHighlight(startIndex, endIndex, painter);
										tarea.setCaretPosition(tarea.getDocument().getDefaultRootElement()
												.getElement(a - 1).getStartOffset());

										l1.setForeground(Color.blue);
										isCheck.add(index);

									} catch (BadLocationException ble) {
										ble.printStackTrace();
									}
								} else {
									isCheck.clear();
								}
							}

						});
						chk.addItemListener(new ItemListener() {
							public void itemStateChanged(ItemEvent e) {

								if (chk.isSelected()) {
									counter++;
								} else if (counter > 0) {
									counter--;
								}

							}
						});
						panel.add(testOran);
						testOran.setBounds(650, 530, 150, 30);
						panel.repaint();
						testOran.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								tp.setVisible(true);
								fp.setVisible(true);
								trn = i - counter;
								tp.setText("Number Of False Positive : " + counter);
								fp.setText("Number Of True Positive : " + trn);
								tp.setBounds(50, 530, 200, 30);
								panel.add(tp);

								fp.setBounds(350, 530, 200, 30);
								panel.add(fp);
								panel.repaint();
							}
						});
					}

				} else {
					JOptionPane.showMessageDialog(Anlz, "Dosya seçilmedi", "alert", JOptionPane.ERROR_MESSAGE);
				}
				panel.repaint();
			}
		});

		frame.getContentPane().add(jsp);
	}
}
