import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import java.lang.reflect.*;
import java.io.File;
import java.io.FilenameFilter;

import qp.*;

public class qpsgui implements ActionListener {
    public final static int ONE_SECOND = 1000;
    
    private Timer timer;
    private Frame frame;
    private JButton startButton;
    private JTextArea taskOutput;
    private JTextField hash, etf, ips;
    private JRadioButton bydict, byscan;
    private ButtonGroup bg;
    private JComboBox algorithm;
    private String nl = "\n";
    private JScrollPane scrollPane;
    private ByscanDialog scandialog;
    private BydictDialog dictdialog;
    private SwingWorker worker = null;
    private Search search = null;
    private Benchmark b = new Benchmark();
    
    
    public qpsgui(Frame aFrame){
    	frame = aFrame;
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("Busca");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "Menu principal para disparo das buscas");
        menuBar.add(menu);

        //a group of JMenuItems
        menuItem = new JMenuItem("Por dicionário",
                                 KeyEvent.VK_D);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This doesn't really do anything");
        menu.add(menuItem);

        menuItem = new JMenuItem("Por varredura", KeyEvent.VK_V);
        menu.add(menuItem);


        //Build second menu in the menu bar.
        menu = new JMenu("Ajuda");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);

        return menuBar;
    }





    /**
     * Called when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
    	if(startButton.getText().equals("Iniciar")){
		String tst;
		qpAlgorithm qpAlg = (qpAlgorithm) createObject(""+algorithm.getSelectedItem());
		final Task t = new Task(hash.getText(), qpAlg);
		if((tst=t.pwdIsInCache()) != null){
			taskOutput.append("*** Cache Hit! ***"+'\n');
	        	taskOutput.append("*** SUCESSO *** - a chave é ["+tst+"]"+'\n');
			return;
		}

	    	if(bydict.isSelected()){
			startButton.setText("Parar");
			taskOutput.append("Iniciando busca por Dicionário..."+nl);
			dictdialog = new BydictDialog(frame, "");
			dictdialog.pack();
			dictdialog.setLocationRelativeTo(frame);
                    	dictdialog.setVisible(true);
                    	final String dict = dictdialog.getValidatedText();

			final Rules r = new Rules("rules.qp");
		        //Create a timer.
        		timer = new Timer(ONE_SECOND, new ActionListener() {
	        	    public void actionPerformed(ActionEvent evt) {
				Double dips = new Double(b.getIPS());
				ips.setText(dips.toString());
				etf.setText(b.getETFString(r));
				}
		        });
			worker = new SwingWorker() {
				public Object construct(){
					long inicio, fim, dif;
					String result = null, currentRule = t.loadState();
					search = new Search(dict, t);
					boolean proceed = true; 
					taskOutput.append("Hash: ["+t.getCurrentTarget()+"] Algoritmo: ["+t.getCurrentAlgorithmName()+"]"+'\n');
					taskOutput.append("Estado de execução corrente: "+currentRule+'\n');
					if(currentRule == null)
						currentRule = r.getNextRule();
					else{
						taskOutput.append("Continuando execução."+'\n');
						r.skipToRule(currentRule);
						currentRule = r.getNextRule();
					}	
					timer.start();
					b.start(search);
			        	inicio = System.currentTimeMillis();
					//search main loop
			        	while (currentRule != null && proceed) {
						taskOutput.append("Regra corrente: "+currentRule+'\n');
						if((result = search.byRule(currentRule)) != null)
							proceed = false;
						t.saveState(currentRule);
						currentRule = r.getNextRule();
			        	}
				        fim = System.currentTimeMillis();
					b.stop();
					timer.stop();
					if(result != null)
		        			taskOutput.append("*** SUCESSO *** - a chave é ["+result+"]"+'\n');
					else
						taskOutput.append("*** FIM DA BUSCA *** - chave não encontrada."+'\n');
					taskOutput.append(search.stringsTested()+" chaves testadas pelo método de busca por dicionário/regras."+'\n');
				        dif = fim - inicio;
				        taskOutput.append(new Double(dif).intValue()/1000+" segundos. Média:"+((dif/1000)/search.stringsTested())+" segs/iteração."+'\n');
					return result;
   				}

			};
			if(dict != null) {
				taskOutput.append("Dicionário: ["+dict+"]"+nl);
				worker.start();
			}
		} else {
			startButton.setText("Parar");
			taskOutput.append("Iniciando busca por Varredura Completa (força-bruta)..."+nl);
			scandialog = new ByscanDialog(frame);
			scandialog.pack();
			scandialog.setLocationRelativeTo(frame);
                    	scandialog.setVisible(true);
                    	final String searchwidth = scandialog.getValidatedText();
		        //Create a timer.
        		timer = new Timer(ONE_SECOND, new ActionListener() {
	        	    public void actionPerformed(ActionEvent evt) {
				Double dips = new Double(b.getIPS());
				ips.setText(dips.toString());
				etf.setText(b.getETFString());
			    }
		        });
			worker = new SwingWorker() {
				public Object construct(){
					final  char[] charSet = {	'0','1','2','3','4','5','6','7','8','9',
									'a','b','c','d','e','f','g','h','i','j',
									'k','l','m','n','o','p','q','r','s','t',
									'u','v','w','x','y','z','A','B','C','D',
									'E','F','G','H','I','J','K','L','M','N',
									'O','P','Q','R','S','T','U','V','W','X',
									'Y','Z','.','?','!','@','#','$','%','&',
									'*','(',')','-','_','=','+','[',']','{',
									'}','\\','/','|',':',';',',','<','>'};

					long inicio, fim, dif;
					String result = null;
					search = new Search(t);
					boolean proceed = true; 
					taskOutput.append("Hash: ["+t.getCurrentTarget()+"] Algoritmo: ["+t.getCurrentAlgorithmName()+"]"+'\n');
					timer.start();
					b.start(search);
			        	inicio = System.currentTimeMillis();
					//search 
					result = search.byCharacterSet(charSet, new Integer(searchwidth).intValue());
				        fim = System.currentTimeMillis();
					b.stop();
					timer.stop();
					if(result != null)
		        			taskOutput.append("*** SUCESSO *** - a chave é ["+result+"]"+'\n');
					else
						taskOutput.append("*** FIM DA BUSCA *** - chave não encontrada."+'\n');
					taskOutput.append(search.stringsTested()+" chaves testadas pelo método de Varredura Completa (força-bruta)."+'\n');
				        dif = fim - inicio;
				        taskOutput.append(new Double(dif).intValue()/1000+" segundos. Média:"+((dif/1000)/search.stringsTested())+" segs/iteração."+'\n');
				        taskOutput.append("Média: "+new Double(search.stringsTested()/(dif/1000)).intValue()+" iterações/seg."+'\n');
					return result;
   				}

			};
			if(searchwidth != null) {
				taskOutput.append("Largura de busca: ["+searchwidth+"]"+nl);
				worker.start();
			}
		}
	} else {
		search.stop();
		worker.interrupt();
		b.stop();
		timer.stop();
		startButton.setText("Iniciar");
	}
    }


   static Object createObject(String className) {
      Object object = null;
      try {
          Class classDefinition = Class.forName(className);
          object = classDefinition.newInstance();
      } catch (InstantiationException e) {
          System.out.println(e);
      } catch (IllegalAccessException e) {
          System.out.println(e);
      } catch (ClassNotFoundException e) {
          System.out.println(e);
      }
      return object;
   }


    public Container createContentPane() {
        //Create the content-pane-to-be.
	GridBagLayout gb = new GridBagLayout();
        JPanel contentPane = new JPanel(gb);
	GridBagConstraints c = new GridBagConstraints();
        contentPane.setOpaque(true);

	c.fill = GridBagConstraints.HORIZONTAL; 
        //Create the pane UI.
	JLabel hashlabel = new JLabel("Hash a testar: ");
	c.gridx=0; c.gridy=0; c.gridwidth=1;
	gb.setConstraints(hashlabel, c);
        contentPane.add(hashlabel);
	
	hash = new JTextField(40);
	c.gridx=1; c.gridy=0; c.gridwidth=4;
	gb.setConstraints(hash, c);
        contentPane.add(hash);
	
	JLabel alglabel = new JLabel("Algoritmo: ");
	c.gridx=0; c.gridy=1; c.gridwidth=1;
	gb.setConstraints(alglabel, c);
        contentPane.add(alglabel);

	algorithm = new JComboBox(this.getAlgorithms());	
	c.gridx=1; c.gridy=1; c.gridwidth=4;
	gb.setConstraints(algorithm, c);
        contentPane.add(algorithm);
	
	bydict = new JRadioButton("por Dicionário/Regras");
	c.gridx=0; c.gridy=2; c.gridwidth=2;
	gb.setConstraints(bydict, c);
	bydict.setSelected(true);
        contentPane.add(bydict);

	byscan = new JRadioButton("por Varredura Completa");
	c.gridx=0; c.gridy=3; c.gridwidth=2;
	gb.setConstraints(byscan, c);
        contentPane.add(byscan);
	
	bg = new ButtonGroup();
	bg.add(bydict);
	bg.add(byscan);
	
        startButton = new JButton("Iniciar");
        startButton.setActionCommand("Iniciar");
        startButton.addActionListener(this);
	c.gridx=4; c.gridy=2; c.gridwidth=1; c.gridheight=2;
	gb.setConstraints(startButton, c);
        contentPane.add(startButton);


        taskOutput = new JTextArea(18, 54);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);
        //Create a scrolled text area.
        scrollPane = new JScrollPane(taskOutput);
	c.gridx=0; c.gridy=4; c.gridwidth=5; c.gridheight=1;
	gb.setConstraints(scrollPane, c);
        //Add the text area to the content pane.
        contentPane.add(scrollPane);

	JLabel ipslabel = new JLabel("IPS: ");
	c.gridx=0; c.gridy=5; c.gridwidth=1;
	gb.setConstraints(ipslabel, c);
        contentPane.add(ipslabel);

	ips = new JTextField(15);
	c.gridx=1; c.gridy=5; c.gridwidth=1;
	gb.setConstraints(ips, c);
        contentPane.add(ips);

	JLabel etflabel = new JLabel("ETF: ");
	c.gridx=2; c.gridy=5; c.gridwidth=1;
	gb.setConstraints(etflabel, c);
        contentPane.add(etflabel);

	etf = new JTextField(15);
	c.gridx=3; c.gridy=5; c.gridwidth=1;
	gb.setConstraints(etf, c);
        contentPane.add(etf);

        return contentPane;
    }


    private Object[] getAlgorithms(){
    	File myDir = new File("../plugins/"); // or any required directory pathname
	AlgorithmsFilter algfilter = new AlgorithmsFilter("class");
    	File[] files = myDir.listFiles(algfilter);
    	// can also use a FileFilter
	StringBuffer strb;
	int i;
    	Object[] algs = new Object[files.length];
	for(i=0; i < files.length; i++){
		strb = new StringBuffer(files[i].getName());
		strb.setLength(strb.lastIndexOf("."));
		algs[i] = strb.toString();
	}
	return algs;
    }

public class AlgorithmsFilter implements FilenameFilter {
  protected String pattern;
  public AlgorithmsFilter (String str) {
    pattern = str;
  }
  public boolean accept (File dir, String name) {
    if(name.equals("qpAlgorithm.class"))
    	return false;
    else
    	return name.toLowerCase().endsWith(pattern.toLowerCase());
  }
}
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame fr = new JFrame("qp - stand-alone version");
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        qpsgui demo = new qpsgui(fr);
        fr.setJMenuBar(demo.createMenuBar());
        fr.setContentPane(demo.createContentPane());

        //Display the window.
        fr.setSize(640, 480);
        fr.setVisible(true);
    }



    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
		
            }
        });
    }
}
