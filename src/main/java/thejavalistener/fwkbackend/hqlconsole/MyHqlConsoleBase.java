package thejavalistener.fwkbackend.hqlconsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.springframework.context.ApplicationContext;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import thejavalistener.fwkutils.awt.link.MyLink;
import thejavalistener.fwkutils.awt.panel.MyBorderLayout;
import thejavalistener.fwkutils.awt.panel.MyInsets;
import thejavalistener.fwkutils.awt.panel.MyLeftLayout;
import thejavalistener.fwkutils.awt.panel.MyScrollPane;
import thejavalistener.fwkutils.awt.splitpane.MySplitPane;
import thejavalistener.fwkutils.awt.tabbedpane.MyTabbedPane;
import thejavalistener.fwkutils.awt.table.MyTable;
import thejavalistener.fwkutils.awt.text.MyTextPane;
import thejavalistener.fwkutils.awt.variuos.MyAwt;
import thejavalistener.fwkutils.properties.MyFileProperties;
import thejavalistener.fwkutils.string.MyString;

public abstract class MyHqlConsoleBase
{
	private MyFileProperties properties;

	protected abstract void executeHql(String sql);

	// panel principal
	protected JPanel contentPane;

	// línea de comandos
	private MyTextPane commandLine;
	
	private MySplitPane splitPane;

	// panel de resultados
	private MyTabbedPane resultsTabbedPane;
		
	private EscuchaConsola escuchaConsola;
	
	@PostConstruct
	public void init()
	{
		properties = new MyFileProperties();
		
		// panel principal es un border layout
		contentPane = new JPanel(new BorderLayout());
		
		// línea de comandos
		commandLine = new MyTextPane();
		commandLine.setFont(new Font("Consolas", Font.PLAIN, 12));
		commandLine.addKeyListener(escuchaConsola = new EscuchaConsola());
		MyScrollPane scrollSQL=new MyScrollPane(commandLine.c());
		
		// Resultados
		resultsTabbedPane = new MyTabbedPane();
		resultsTabbedPane.c().setBorder(null);
		
		// En el center el SplitPane, con el commandLine (izq) y el resultsTabbedPane (der)
		Integer dividerLocation = properties.getObject("myhqlconsole.status.dividerLocation");
		dividerLocation=dividerLocation==null?200:dividerLocation;
		splitPane = new MySplitPane(MySplitPane.VERTICAL,scrollSQL,resultsTabbedPane.c());		
		splitPane.setDividerLocation(dividerLocation);
		splitPane.setMySplitePaneListener(i->properties.putObject("myhqlconsole.status.dividerLocation",(Integer)i));
		splitPane.setDividerSize(1);

		JPanel pCenter = new JPanel(new BorderLayout());
		pCenter.add(new MyInsets(splitPane.c(),10,10,0,0),BorderLayout.CENTER);
		
		// pSouth
		
		JPanel pLim = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton bLimpiar = new JButton("Limpiar");
		bLimpiar.addActionListener(e->resultsTabbedPane.removeAllTabs());
		pLim.add(bLimpiar);
		pCenter.add(pLim,BorderLayout.SOUTH);
		
		MyLeftLayout  pNorth = new MyLeftLayout();
		
		_addLink("Execute:","[ALT+X]",KeyEvent.ALT_DOWN_MASK,KeyEvent.VK_X,'X',pNorth);		
		_addLink("|    Create New Object:","[ALT+N]",KeyEvent.ALT_DOWN_MASK,KeyEvent.VK_N,'N',pNorth);
		_addLink("|    Clear All:","[ALT+C]",KeyEvent.ALT_DOWN_MASK,KeyEvent.VK_C,'C',pNorth);
		_addLink("|    Save:","[ALT+S]",KeyEvent.ALT_DOWN_MASK,KeyEvent.VK_S,'S',pNorth);

		pCenter.add(new MyInsets(pNorth,10,10,0,5),BorderLayout.NORTH);
		
		contentPane.add(pCenter,BorderLayout.CENTER);
		
		String txt = properties.getObject("myhqlconsole.status.commandline");
		commandLine.setText(MyString.ifNull(txt,""));
		
		commandLine.requestFocus();
	}
	
	@PreDestroy
	public void destroy()
	{
		save();
	}

	private void _addLink(String desc,String hotKey,int comb,int vk,char c,JPanel p)
	{
		MyLink lnk = new MyLink(desc);
		lnk.getStyle().linkForegroundUnselected = Color.GRAY;
		lnk.getStyle().linkInsets.left=0;
		lnk.getStyle().linkInsets.right=0;
		p.add(lnk.c());
		
		lnk = new MyLink(hotKey,MyLink.LINK);
		lnk.getStyle().linkInsets.left=0;
		lnk.getStyle().linkInsets.right=0;
		lnk.setActionListener(l->escuchaConsola.keyPressed(new KeyEvent(commandLine.c(),0,0,comb,vk,c)));
		p.add(lnk.c());
	}
	
	public void addResult(String sql,List<Object[]> rows)
	{
		Object[] headers = null;
		if( rows.size()>0 )
		{
			headers = new Object[rows.get(0).length];
			for(int i=0; i<headers.length; i++) headers[i] = "c"+i;
		}
		else
		{
			headers = new Object[0];
		}
		
		addResult(sql,rows,headers);
	}
	
	public void addResult(String sql,List<Object[]> rows,Object[] headers)
	{
		MyLink lnk = new MyLink(sql,MyLink.LINK);
		lnk.setActionListener(l->MyAwt.copyToClipboard(lnk.getText()));
		MyLeftLayout pLabel = new MyLeftLayout(lnk.c(),5,0,5,0);
		MyBorderLayout pResultado = new MyBorderLayout(pLabel,0,0,0,0,BorderLayout.NORTH);
				
		MyTable<?> table = new MyTable<>();
		table.setBackground(Color.BLACK);
		table.setForeground(Color.WHITE);
//		if( headers!=null ) 
		table.headers(headers);
		table.c().setRowHeight(table.c().getRowHeight()+5);
		
		
		table.setData(rows);
		MyScrollPane jsp = new MyScrollPane(table.c());
		jsp.setBorder(null);
		pResultado.add(jsp,BorderLayout.CENTER);    		
		resultsTabbedPane.addTab(pResultado,true,sql);
	}
		
	public JPanel c()
	{
		return contentPane;
	}

	
	class EscuchaConsola extends KeyAdapter
	{
	    public void keyPressed(KeyEvent e) 
	    {
	    	String txt = commandLine.getSelectedText();	    
	    	
	    	// ALT+C - Clear all
	    	if(e.isAltDown() && Character.toLowerCase(e.getKeyChar()) == 'c') 
	        {
	    		resultsTabbedPane.removeAllTabs();
	    		commandLine.requestFocus();
		    	return;
	        }

	    	// ALT+S - Save
	    	if(e.isAltDown() && Character.toLowerCase(e.getKeyChar()) == 's') 
	        {
	    		save();
	    		MyAwt.showInformationMessage("La línea de comandos fue guardada con éxito","Bien!",contentPane);
	    		return;
	        }

	    	
	    	// ALT+X - Ejecutar
	    	if(e.isAltDown() && Character.toLowerCase(e.getKeyChar()) == 'x') 
	        {
		    	if(txt==null)
		    	{
		    		txt = _selectTextLine(commandLine.getText(),commandLine.getCaretPosition());
		    	}

		    	// ejecuto el HQL
		    	executeHql(txt);

		    	save();
		    	return;
	        }
	    		        
	    	// ALT+N - New Object
	        if(e.isAltDown() && Character.toLowerCase(e.getKeyCode()) == 'n') 
	        {
				//getMyApp().showScreen(HQLCreateNewEntity.class).pack().centerH(150).apply();
				return;
	        }
	    }
	}
	
	public void save()
	{
		String txt = commandLine.getText();
		properties.putObject("myhqlconsole.status.commandline",txt);		
	}

	
	private String _selectTextLine(String txt,int curr)
	{
		int bounds[] = MyString.findParagraphBounds(txt,curr);
		if( bounds!=null )
		{
			commandLine.c().select(bounds[0],bounds[1]);
			return txt.substring(bounds[0],bounds[1]);
		}
		
		return "";
		
	}

	
	
}
