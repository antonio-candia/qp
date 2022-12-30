import java.awt.*; 
import java.awt.event.*; 
 
public class qpseq extends Frame { 
  TextField hash;
  TextField dictionary;
  TextField ips;
  TextField etf;
  TextArea msgs;
  Button startButton;
  Choice algorithm;

  public qpseq() { 
    super("qp"); 
    setSize(640, 480); 

    MenuBar mb = new MenuBar();
    qpMenu m = new qpMenu(this);
    HelpMenu m2 = new HelpMenu(this);
    mb.add(m); mb.add(m2);
    setMenuBar(mb);
    myButton startButton = new myButton("Iniciar");

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints consts = new GridBagConstraints();
    setFont(new Font("Helvetica", Font.PLAIN, 12));
    setLayout(gridbag);
    
    consts.fill = GridBagConstraints.HORIZONTAL;

    Label hashLabel = new Label("HASH a ser testado:");
    consts.gridx = 0; consts.gridy = 0; consts.gridwidth = 1;
    gridbag.setConstraints(hashLabel,consts);
    add(hashLabel);
    
    consts.gridx = 1; consts.gridy = 0; consts.gridwidth = 4;
    hash = new TextField("", 55);
    gridbag.setConstraints(hash,consts);    
    add(hash);
    
    Label algorithmLabel = new Label("Algoritmo:");
    consts.gridx = 0; consts.gridy = 1; consts.gridwidth = 1;
    gridbag.setConstraints(algorithmLabel,consts);
    add(algorithmLabel);
    
    consts.gridx = 1; consts.gridy = 1; consts.gridwidth = 4;
    algorithm = new Choice();
    algorithm = this.getAlgorithms();
    gridbag.setConstraints(algorithm,consts);    
    add(algorithm);

    Label dictLabel = new Label("Dicionário:");
    consts.gridx = 0; consts.gridy = 2; consts.gridwidth = 1;
    gridbag.setConstraints(dictLabel,consts);
    add(dictLabel);
    
    consts.gridx = 1; consts.gridy = 2; consts.gridwidth = 4;
    dictionary = new TextField("dict.qp", 55);
    gridbag.setConstraints(dictionary,consts);    
    add(dictionary);
    
    consts.gridx = 0; consts.gridy = 3; consts.gridwidth = 1;
    gridbag.setConstraints(startButton,consts);
    add(startButton);

    Label ipsLabel = new Label("IPS:");
    consts.gridx = 1; consts.gridy = 3; consts.gridwidth = 1;
    gridbag.setConstraints(ipsLabel,consts);
    add(ipsLabel);
    
    consts.gridx = 2; consts.gridy = 3; consts.gridwidth = 1;
    ips = new TextField("0", 14);
    gridbag.setConstraints(ips,consts);    
    add(ips);
    
    Label etfLabel = new Label("ETF:");
    consts.gridx = 3; consts.gridy = 3; consts.gridwidth = 1;
    gridbag.setConstraints(etfLabel,consts);
    add(etfLabel);
    
    consts.gridx = 4; consts.gridy = 3; consts.gridwidth = 1;
    etf = new TextField("Infinity", 14);
    gridbag.setConstraints(etf,consts);    
    add(etf);
    
    consts.gridx = 0; consts.gridy = 4; consts.gridwidth = 5;
    consts.insets = new Insets(10,0,0,0);
    msgs = new TextArea("Inicialização completa."+'\n', 18, 74, TextArea.SCROLLBARS_VERTICAL_ONLY);
    gridbag.setConstraints(msgs,consts);    
    add(msgs);
    
    addWindowListener(new WindowAdapter() { 
      public void windowClosing(WindowEvent e) { 
        setVisible(false); dispose(); 
        System.exit(0); 
      } 
    }); 

  } 

  public void searchByRules(){

  }


  public Choice getAlgorithms(){
    Choice alg = new Choice();
    alg.add("crypt");
    alg.add("md5");
    return alg;
  }
    
  // Encapsulate the look and behavior of the File menu 
class qpMenu extends Menu implements ActionListener { 
  qpseq mw;  // who owns us? 
  public qpMenu(qpseq m) { 
    super("Busca"); 
    mw = m; 
    MenuItem mi; 
    add(mi = new MenuItem("Arquivo de regras")); 
    mi.addActionListener(this); 
    add(mi = new MenuItem("Força-bruta")); 
    mi.addActionListener(this); 
    add(mi = new MenuItem("Sair")); 
    mi.addActionListener(this); 
  } 
  // respond to the Exit menu choice 
  public void actionPerformed(ActionEvent e) { 
    String item = e.getActionCommand(); 
    if (item.equals("Sair"))
      mw.exit();
    else
      if (item.equals("Arquivo de regras"))
	mw.searchByRules();
      else
//        if (item.equals("Arquivo de regras"))
//  	    mw.searchByBruteForce();
//        else
          System.out.println("Selected FileMenu " + item); 
  } 
}
 // Encapsulate the look and behavior of the Help menu 
class HelpMenu extends Menu implements ActionListener { 
  qpseq mw;  // who owns us? 
  public HelpMenu(qpseq m) { 
    super("Ajuda"); 
    mw = m; 
    MenuItem mi; 
    add(mi = new MenuItem("Fundamentals")); 
    mi.addActionListener(this); 
    add(mi = new MenuItem("Advanced")); 
    mi.addActionListener(this); 
    addSeparator(); 
    add(mi = new MenuItem("Sobre")); 
    mi.addActionListener(this); 
 
  } 
  // respond to a few menu items 
  public void actionPerformed(ActionEvent e) { 
    String item = e.getActionCommand(); 
    if (item.equals("Fundamentals")) 
      System.out.println("Fundamentals"); 
    else if (item.equals("Help!!!"))  
      System.out.println("Help!!!"); 
    // etc... 
  } 
} 

// gerenciamento do botao de inicio/parada da busca
class myButton extends Button implements ActionListener { 
  Task t;
  Benchmark b;

  public myButton(String label) { 
    super(label); 
    this.addActionListener(this); 
  }

  public void actionPerformed(ActionEvent e) {
	if(e.getActionCommand().equals("Iniciar"))
		this.startSearch();
	else
		this.stopSearch();
  }

  private void startSearch(){
	Rules r = new Rules("rules.qp");
	String tst;
	long inicio, fim, dif;
	String result;
	b = new Benchmark();
	this.setLabel("Parar");

	qpAlgorithm qpAlg = (qpAlgorithm) this.createObject("qp"+algorithm.getSelectedItem());
	t = new Task(hash.getText(), qpAlg);
	if((tst=t.pwdIsInCache()) != null){
		msgs.append("*** Cache Hit! ***"+'\n');
        	msgs.append("*** Cracked! *** - the password is ["+tst+"]"+'\n');
		this.stopSearch();
		return;
	}

	Search search = new Search(dictionary.getText(), t);
        inicio = System.currentTimeMillis();
	String currentRule = t.loadState();
	msgs.append("Hash: ["+t.getCurrentTarget()+"] Algorithm: ["+t.getCurrentAlgorithmName()+"]"+'\n');
	msgs.append("Rules Size: ["+r.size()+"]"+'\n');
	msgs.append("Current State: "+currentRule+'\n');
	if(currentRule == null)
		currentRule = r.getNextRule();
	else{
		msgs.append("Resuming previous run."+'\n');
		r.skipToRule(currentRule);
		currentRule = r.getNextRule();
	}	
	b.start(search);
        while (currentRule != null) {
		msgs.append("Current rule: "+currentRule+'\n');
		Double dips = new Double(b.getIPS());
		ips.setText(dips.toString());
		etf.setText(String.valueOf(new Double(b.getETF(r)).longValue())+" segundos");
		if((result = search.byRule(currentRule)) != null){
	          msgs.append("*** Cracked! *** - the password is ["+result+"]"+'\n');
                  fim = System.currentTimeMillis();
                  dif = fim - inicio;
	          msgs.append(search.stringsTested()+" strings tested by dictionary search method."+'\n');
	          msgs.append(new Double(dif/1000).intValue()+" secs ellapsed. Average:"+(dif/search.stringsTested())+" msecs/iteration."+'\n');
		  msgs.append("Caching password."+'\n');
		  t.cachePwdFound(result);
		  b.stop();
	          this.stopSearch();
		  return;
	        }
		t.saveState(currentRule);
		currentRule = r.getNextRule();
        }
	b.stop();
	msgs.append("Sorry, password not found."+'\n');
	msgs.append(search.stringsTested()+" strings tested by dictionary search method."+'\n');
        fim = System.currentTimeMillis();
        dif = fim - inicio;
        msgs.append(new Double(dif).intValue()/1000+" secs ellapsed. Average:"+(dif/search.stringsTested())+" msecs/iteration."+'\n');
	this.stopSearch();
   }

   Object createObject(String className) {
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




  private void stopSearch(){
	this.setLabel("Iniciar");
	b.stop();
	ips.setText("0");
	etf.setText("");
  }

}

// diálogo do "sobre" do menu "ajuda"
//class SobreDialog extends AppFrame {
  
//}

  public void exit() { 
    setVisible(false); // hide the Frame 
    dispose(); // tell windowing system to free resources 
    System.exit(0); // exit 
  } 
 
/*
// responsável pela visualizacao em formato mapa 2d
  public void 2dMap() extends AppFrame {
//    GraphicsConfiguration gc = new 

  } 
*/ 
 
  public static void main(String[] args) { 
    qpseq app = new qpseq(); 
    app.setVisible(true); 

    
  } 
} 
